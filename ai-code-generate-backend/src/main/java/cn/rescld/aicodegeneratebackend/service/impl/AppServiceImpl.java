package cn.rescld.aicodegeneratebackend.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.constant.AppConstant;
import cn.rescld.aicodegeneratebackend.core.AiCodeGeneratorFacade;
import cn.rescld.aicodegeneratebackend.core.builder.VueProjectBuilder;
import cn.rescld.aicodegeneratebackend.core.handler.StreamHandlerExecutor;
import cn.rescld.aicodegeneratebackend.exception.BusinessException;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.mapper.AppMapper;
import cn.rescld.aicodegeneratebackend.model.dto.app.AdminAppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppCreateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppQueryRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.entity.App;
import cn.rescld.aicodegeneratebackend.model.entity.User;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import cn.rescld.aicodegeneratebackend.model.enums.MessageTypeEnum;
import cn.rescld.aicodegeneratebackend.model.vo.AppVO;
import cn.rescld.aicodegeneratebackend.model.vo.UserVO;
import cn.rescld.aicodegeneratebackend.service.AppService;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用表 服务层实现。
 *
 * @author 残云cyun
 * @since 2025-08-06
 */
@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private AppMapper appMapper;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, Long uid) {
        // 校验应用信息
        App app = validateAppById(appId);

        // 校验权限，只有应用创建者可以对话
        boolean exists = this.queryChain()
                .eq(App::getUserId, uid)
                .exists();
        ThrowUtils.throwIf(!exists, ErrorCode.NO_AUTH_ERROR);

        // 获取应用生成类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByType(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR);

        // 将用户提示词保存到数据库中
        chatHistoryService.addChatMessage(message, MessageTypeEnum.USER.getType(), appId, uid);

        // 与 AI 交互
        Flux<String> aiResponse = aiCodeGeneratorFacade.generateAndSaveCode(message, codeGenTypeEnum, appId);

        // 记录并保存 AI 提示词
        return streamHandlerExecutor.execute(aiResponse, chatHistoryService, codeGenTypeEnum, appId, uid);
    }

    @Override
    public String deployApp(Long uid, Long appId) {
        // 只有应用的创建者可以部署
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
        ThrowUtils.throwIf(!app.getUserId().equals(uid), ErrorCode.NO_AUTH_ERROR);

        // 获取部署 Key，若为空则创建随机字符
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(8);
        }

        // 校验代码是否已生成
        String sourceName = app.getCodeGenType() + "_" + app.getId().toString();
        String sourcePath = AppConstant.CODE_GEN_ROOT + File.separator + sourceName;
        File sourceDir = new File(sourcePath);
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(),
                ErrorCode.OPERATION_ERROR, "应用不存在");

        // Vue 项目特殊处理：执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByType(app.getCodeGenType());
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourcePath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourcePath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录: {}", distDir.getAbsolutePath());
        }

        // 将代码复制到部署目录
        String deployPath = AppConstant.DEPLOY_ROOT + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployPath), true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }

        // 更新数据库信息
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setUserId(uid);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployTime(LocalDateTime.now());
        this.updateById(updateApp);

        return AppConstant.DEPLOY_DOMAIN + "/" + deployKey;
    }

    @Override
    public Long createApp(AppCreateRequest request, Long userId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 校验提示词
        String initPrompt = request.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt),
                ErrorCode.PARAMS_ERROR, "初始化提示词不能为空");
        ThrowUtils.throwIf(initPrompt.length() > 5000,
                ErrorCode.PARAMS_ERROR, "初始化提示词不能超过5000个字符");

        // 保存应用信息
        App app = new App();
        app.setUserId(userId);
        app.setInitPrompt(initPrompt);
        // TODO: 应用名称先用提示词前12位，后续改成 AI 生成更合适的应用名称
        app.setName(initPrompt.substring(0, Math.min(12, initPrompt.length())));
        // TODO: 这里先用多文件方式生成，后续改成动态选择不同生成方式
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getType());
        this.save(app);

        // 返回应用 id
        return app.getId();
    }

    @Override
    public AppVO updateUserApp(Long id, AppUpdateRequest request, Long userId) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(id == null || id <= 0,
                ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(request.getName()),
                ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        ThrowUtils.throwIf(request.getName().length() > 32,
                ErrorCode.PARAMS_ERROR, "应用名称不能超过32个字符");

        // 检查应用是否存在且属于当前用户
        App existingApp = this.queryChain()
                .eq(App::getId, id)
                .eq(App::getUserId, userId)
                .one();
        ThrowUtils.throwIf(existingApp == null,
                ErrorCode.PARAMS_ERROR, "应用不存在或无权限访问");

        App app = new App();
        app.setId(existingApp.getId());
        app.setName(request.getName());
        this.updateById(app);

        // 返回更新后的应用信息
        return getAppById(id);
    }

    @Override
    public boolean deleteUserApp(Long id, Long userId) {
        ThrowUtils.throwIf(id == null || id <= 0,
                ErrorCode.PARAMS_ERROR, "应用ID不能为空");

        // 检查应用是否存在且属于当前用户
        App existingApp = this.queryChain()
                .eq(App::getId, id)
                .eq(App::getUserId, userId)
                .one();
        ThrowUtils.throwIf(existingApp == null,
                ErrorCode.PARAMS_ERROR, "应用不存在");

        return this.removeById(id);
    }

    @Override
    public AppVO getAppById(Long id) {
        App app = validateAppById(id);

        AppVO appVO = new AppVO();
        BeanUtils.copyProperties(app, appVO);
        return appVO;
    }

    @Override
    public Page<AppVO> pageUserApps(AppQueryRequest request, Long userId) {
        validatePageSize(request);

        Page<App> page = new Page<>();
        page.setPageNumber(request.getCurrent());
        page.setPageSize(request.getPageSize());

        QueryWrapper queryWrapper = new QueryWrapper()
                .eq(App::getUserId, userId)
                .like(App::getName, request.getName())
                .orderBy(App::getCreateTime, false);

        Page<App> result = appMapper.paginateWithRelationsAs(page, queryWrapper, App.class);
        return convertToVOPage(result);
    }

    @Override
    public Page<AppVO> pageFeaturedApps(AppQueryRequest request) {
        validatePageSize(request);

        Page<App> page = new Page<>();
        page.setPageNumber(request.getCurrent());
        page.setPageSize(request.getPageSize());

        // 精选应用按优先级排序，优先级高的在前
        this.queryChain()
                .like(App::getName, request.getName())
                .orderBy(App::getPriority, false)
                .orderBy(App::getCreateTime, false)
                .page(page);

        return convertToVOPage(page);
    }

    @Override
    public boolean adminDeleteApp(Long id) {
        validateAppById(id);
        return this.removeById(id);
    }

    @Override
    public AppVO adminUpdateApp(AdminAppUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(request.getId() == null || request.getId() <= 0,
                ErrorCode.PARAMS_ERROR, "应用ID不能为空");

        // 检查应用是否存在
        validateAppById(request.getId());

        // 校验参数
        if (StrUtil.isNotBlank(request.getName())) {
            ThrowUtils.throwIf(request.getName().length() > 32,
                    ErrorCode.PARAMS_ERROR, "应用名称不能超过32个字符");
        }
        if (request.getPriority() != null) {
            ThrowUtils.throwIf(request.getPriority() < 0,
                    ErrorCode.PARAMS_ERROR, "优先级不能为负数");
        }

        App app = new App();
        BeanUtils.copyProperties(request, app);
        this.updateById(app);

        return getAppById(request.getId());
    }

    @Override
    public Page<AppVO> adminPageApps(AppQueryRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        Page<App> page = new Page<>();
        page.setPageNumber(request.getCurrent());
        page.setPageSize(request.getPageSize());

        this.queryChain()
                .eq(App::getId, request.getId())
                .like(App::getName, request.getName())
                .like(App::getCover, request.getCover())
                .eq(App::getCodeGenType, request.getCodeGenType())
                .eq(App::getPriority, request.getPriority())
                .eq(App::getUserId, request.getUserId())
                .eq(App::getIsDelete, request.getIsDelete())
                .orderBy(App::getCreateTime, false)
                .page(page);

        return convertToVOPage(page);
    }

    /**
     * 将App分页结果转换为AppVO分页结果
     */
    private Page<AppVO> convertToVOPage(Page<App> page) {
        List<AppVO> list = page.getRecords().stream().map(app -> {
            User user = app.getUser();
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            AppVO appVO = new AppVO();
            BeanUtils.copyProperties(app, appVO);
            appVO.setUserVO(userVO);
            return appVO;
        }).toList();

        Page<AppVO> pageVO = new Page<>();
        BeanUtils.copyProperties(page, pageVO);
        pageVO.setRecords(list);
        return pageVO;
    }

    /**
     * 根据 id 校验应用
     *
     * @param id 应用 id
     * @return 返回存在的应用信息
     */
    private App validateAppById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0,
                ErrorCode.PARAMS_ERROR, "应用ID不能为空");

        App app = this.queryChain()
                .eq(App::getId, id)
                .one();
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");

        return app;
    }

    /**
     * 校验分页大小
     *
     * @param request 分页查询对象
     */
    private void validatePageSize(AppQueryRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(request.getPageSize() > 20,
                ErrorCode.PARAMS_ERROR, "一次最多只能查询20条信息");
    }
}
