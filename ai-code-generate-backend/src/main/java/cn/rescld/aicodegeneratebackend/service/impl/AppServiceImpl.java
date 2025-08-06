package cn.rescld.aicodegeneratebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.mapper.AppMapper;
import cn.rescld.aicodegeneratebackend.model.dto.app.AdminAppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppCreateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppQueryRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.entity.App;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import cn.rescld.aicodegeneratebackend.model.enums.IsDeleteEnum;
import cn.rescld.aicodegeneratebackend.model.vo.AppVO;
import cn.rescld.aicodegeneratebackend.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用表 服务层实现。
 *
 * @author 残云cyun
 * @since 2025-08-06
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

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
                .eq(App::getIsDelete, IsDeleteEnum.NORMAL.getValue())
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
                .eq(App::getIsDelete, IsDeleteEnum.NORMAL.getValue())
                .one();
        ThrowUtils.throwIf(existingApp == null,
                ErrorCode.PARAMS_ERROR, "应用不存在");

        return this.deleteApp(id);
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

        this.queryChain()
                .eq(App::getUserId, userId)
                .eq(App::getIsDelete, IsDeleteEnum.NORMAL.getValue())
                .like(App::getName, request.getName())
                .orderBy(App::getCreateTime, false)
                .page(page);

        return convertToVOPage(page);
    }

    @Override
    public Page<AppVO> pageFeaturedApps(AppQueryRequest request) {
        validatePageSize(request);

        Page<App> page = new Page<>();
        page.setPageNumber(request.getCurrent());
        page.setPageSize(request.getPageSize());

        // 精选应用按优先级排序，优先级高的在前
        this.queryChain()
                .eq(App::getIsDelete, IsDeleteEnum.NORMAL.getValue())
                .like(App::getName, request.getName())
                .orderBy(App::getPriority, false)
                .orderBy(App::getCreateTime, false)
                .page(page);

        return convertToVOPage(page);
    }

    @Override
    public boolean adminDeleteApp(Long id) {
        validateAppById(id);
        return this.deleteApp(id);
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
        List<AppVO> voList = page.getRecords().stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtils.copyProperties(app, appVO);
            return appVO;
        }).collect(Collectors.toList());

        Page<AppVO> voPage = new Page<>();
        voPage.setPageNumber(page.getPageNumber());
        voPage.setPageSize(page.getPageSize());
        voPage.setTotalRow(page.getTotalRow());
        voPage.setTotalPage(page.getTotalPage());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 根据应用 id 逻辑删除
     *
     * @param id 应用 id
     * @return {@code true} 删除成功 {@code false} 删除失败
     */
    private boolean deleteApp(Long id) {
        App app = new App();
        app.setId(id);
        app.setIsDelete(IsDeleteEnum.DELETED.getValue());
        return this.updateById(app);
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
                .eq(App::getIsDelete, IsDeleteEnum.NORMAL.getValue())
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
