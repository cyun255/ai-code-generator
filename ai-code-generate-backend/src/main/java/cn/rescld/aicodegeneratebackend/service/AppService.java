package cn.rescld.aicodegeneratebackend.service;

import cn.rescld.aicodegeneratebackend.model.dto.app.AdminAppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppCreateRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppQueryRequest;
import cn.rescld.aicodegeneratebackend.model.dto.app.AppUpdateRequest;
import cn.rescld.aicodegeneratebackend.model.entity.App;
import cn.rescld.aicodegeneratebackend.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

/**
 * 应用表 服务层。
 *
 * @author 残云cyun
 * @since 2025-08-06
 */
public interface AppService extends IService<App> {

    /**
     * 应用聊天生成代码，SSE返回给前端
     *
     * @param appId   应用id
     * @param message 用户提示词
     * @param uid     登录的用户id
     * @return AI 生成结果流
     */
    Flux<String> chatToGenCode(Long appId, String message, Long uid);

    /**
     * 创建应用
     *
     * @param request 创建请求
     * @param userId  用户ID
     * @return 创建成功的应用ID
     */
    Long createApp(AppCreateRequest request, Long userId);

    /**
     * 用户更新自己的应用
     *
     * @param id      应用ID
     * @param request 更新请求
     * @param userId  用户ID
     * @return 更新后的应用信息
     */
    AppVO updateUserApp(Long id, AppUpdateRequest request, Long userId);

    /**
     * 用户删除自己的应用
     *
     * @param id     应用ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUserApp(Long id, Long userId);

    /**
     * 根据ID查看应用详情
     *
     * @param id 应用ID
     * @return 应用详情
     */
    AppVO getAppById(Long id);

    /**
     * 分页查询用户自己的应用列表
     *
     * @param request 查询请求
     * @param userId  用户ID
     * @return 分页结果
     */
    Page<AppVO> pageUserApps(AppQueryRequest request, Long userId);

    /**
     * 分页查询精选应用列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    Page<AppVO> pageFeaturedApps(AppQueryRequest request);

    /**
     * 管理员删除应用
     *
     * @param id 应用ID
     * @return 是否删除成功
     */
    boolean adminDeleteApp(Long id);

    /**
     * 管理员更新应用
     *
     * @param request 更新请求
     * @return 更新后的应用信息
     */
    AppVO adminUpdateApp(AdminAppUpdateRequest request);

    /**
     * 管理员分页查询应用列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    Page<AppVO> adminPageApps(AppQueryRequest request);
}
