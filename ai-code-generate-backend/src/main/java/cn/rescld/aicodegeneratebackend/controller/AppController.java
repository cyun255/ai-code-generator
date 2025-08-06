package cn.rescld.aicodegeneratebackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.common.BaseResponse;
import cn.rescld.aicodegeneratebackend.common.ResultUtils;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.dto.app.AdminAppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppCreateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppQueryRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.vo.AppVO;
import cn.rescld.aicodegeneratebackend.service.AppService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 应用表 控制层。
 *
 * @author 残云cyun
 * @since 2025-08-06
 */
@Slf4j
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;

    // region 用户功能

    /**
     * 创建应用
     *
     * @param request 创建请求
     * @return 创建成功的应用ID
     */
    @SaCheckLogin
    @PostMapping("/create")
    public BaseResponse<Long> createApp(@RequestBody AppCreateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.hasBlank(request.getInitPrompt()),
                ErrorCode.PARAMS_ERROR, "初始化提示词不能为空");

        Long userId = StpUtil.getLoginIdAsLong();
        Long appId = appService.createApp(request, userId);

        return ResultUtils.success(appId);
    }

    /**
     * 用户更新自己的应用
     *
     * @param id 应用ID
     * @param request 更新请求
     * @return 更新后的应用信息
     */
    @SaCheckLogin
    @PutMapping("/{id}")
    public BaseResponse<AppVO> updateUserApp(@PathVariable Long id, @RequestBody AppUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        Long userId = StpUtil.getLoginIdAsLong();
        AppVO appVO = appService.updateUserApp(id, request, userId);

        return ResultUtils.success(appVO);
    }

    /**
     * 用户删除自己的应用
     *
     * @param id 应用ID
     * @return 删除结果
     */
    @SaCheckLogin
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteUserApp(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean result = appService.deleteUserApp(id, userId);

        return ResultUtils.success(result);
    }

    /**
     * 根据ID查看应用详情
     *
     * @param id 应用ID
     * @return 应用详情
     */
    @GetMapping("/{id}")
    public BaseResponse<AppVO> getAppById(@PathVariable Long id) {
        AppVO appVO = appService.getAppById(id);
        return ResultUtils.success(appVO);
    }

    /**
     * 分页查询用户自己的应用列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @SaCheckLogin
    @GetMapping("/page")
    public BaseResponse<Page<AppVO>> pageUserApps(@ModelAttribute AppQueryRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        Long userId = StpUtil.getLoginIdAsLong();
        Page<AppVO> page = appService.pageUserApps(request, userId);

        return ResultUtils.success(page);
    }

    /**
     * 分页查询精选应用列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping("/featured/page")
    public BaseResponse<Page<AppVO>> pageFeaturedApps(@ModelAttribute AppQueryRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        Page<AppVO> page = appService.pageFeaturedApps(request);
        return ResultUtils.success(page);
    }

    // endregion

    // region 管理员功能

    /**
     * 管理员删除应用
     *
     * @param id 应用ID
     * @return 删除结果
     */
    @SaCheckRole("admin")
    @DeleteMapping("/admin/{id}")
    public BaseResponse<Boolean> adminDeleteApp(@PathVariable Long id) {
        boolean result = appService.adminDeleteApp(id);
        return ResultUtils.success(result);
    }

    /**
     * 管理员更新应用
     *
     * @param request 更新请求
     * @return 更新后的应用信息
     */
    @SaCheckRole("admin")
    @PutMapping("/admin/update")
    public BaseResponse<AppVO> adminUpdateApp(@RequestBody AdminAppUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        AppVO appVO = appService.adminUpdateApp(request);
        return ResultUtils.success(appVO);
    }

    /**
     * 管理员分页查询应用列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @SaCheckRole("admin")
    @GetMapping("/admin/page")
    public BaseResponse<Page<AppVO>> adminPageApps(@ModelAttribute AppQueryRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        Page<AppVO> page = appService.adminPageApps(request);
        return ResultUtils.success(page);
    }

    /**
     * 管理员根据ID查看应用详情
     *
     * @param id 应用ID
     * @return 应用详情
     */
    @SaCheckRole("admin")
    @GetMapping("/admin/{id}")
    public BaseResponse<AppVO> adminGetAppById(@PathVariable Long id) {
        AppVO appVO = appService.getAppById(id);
        return ResultUtils.success(appVO);
    }

    // endregion
}
