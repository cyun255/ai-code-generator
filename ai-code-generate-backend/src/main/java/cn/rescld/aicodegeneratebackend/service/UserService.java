package cn.rescld.aicodegeneratebackend.service;

import cn.rescld.aicodegeneratebackend.model.dto.user.AdminUpdateRequest;
import com.mybatisflex.core.service.IService;
import cn.rescld.aicodegeneratebackend.model.entity.User;

/**
 * 用户表 服务层。
 *
 * @author 残云cyun
 * @since 2025-08-01
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @return 注册成功的用户 id
     */
    Long register(String username, String password);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     *
     * @param request 需要更新的用户信息
     * @return 最新的用户信息
     */
    User update(AdminUpdateRequest request);
}
