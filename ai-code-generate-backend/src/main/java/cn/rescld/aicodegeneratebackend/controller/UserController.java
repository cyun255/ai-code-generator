package cn.rescld.aicodegeneratebackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.common.BaseResponse;
import cn.rescld.aicodegeneratebackend.common.ResultUtils;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.dto.UserLoginRequest;
import cn.rescld.aicodegeneratebackend.model.dto.UserRegisterRequest;
import cn.rescld.aicodegeneratebackend.model.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.rescld.aicodegeneratebackend.model.entity.User;
import cn.rescld.aicodegeneratebackend.service.UserService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户表 控制层。
 *
 * @author 残云cyun
 * @since 2025-08-01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 基本需求

    /**
     * 注册用户。
     *
     * @param user 注册用户信息
     * @return 注册成功的用户 id
     */
    @PostMapping("register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest user) {
        String username = user.getUsername();
        String password = user.getPassword();

        ThrowUtils.throwIf(StrUtil.hasBlank(username, password),
                ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");

        Long id = userService.register(username, password);

        return ResultUtils.success(id);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录信息
     * @return 登陆成功的用户信息
     */
    @PostMapping("login")
    public BaseResponse<UserVO> login(@RequestBody UserLoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        ThrowUtils.throwIf(StrUtil.hasBlank(username, password),
                ErrorCode.PARAMS_ERROR, "用户名或密码不能为空");

        User user = userService.login(username, password);

        // 数据脱敏
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 登录认证
        StpUtil.login(vo.getId());
        System.out.println(StpUtil.getRoleList().toString());
        return ResultUtils.success(vo);
    }

    /**
     * 用户登出
     */
    @PostMapping("logout")
    public BaseResponse<Long> logout() {
        StpUtil.logout();
        return ResultUtils.success(null);
    }

    /**
     * 根据主键获取用户表。
     *
     * @return 用户详情信息
     */
    @GetMapping
    public BaseResponse<UserVO> getInfo() {
        Object id = StpUtil.getLoginId();
        User user = userService.queryChain().eq(User::getId, id).one();
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ResultUtils.success(vo);
    }

    /**
     * 删除用户。
     */
    @SaCheckLogin
    @DeleteMapping
    public BaseResponse<?> remove() {
        Object id = StpUtil.getLoginId();
        QueryWrapper wrapper = new QueryWrapper().eq(User::getId, id);
        if (userService.remove(wrapper)) {
            StpUtil.logout();
            return ResultUtils.success(true);
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }

    // endregion

    // TODO: 下面懒得写了，后期补上

    /**
     * 根据主键更新用户表。
     *
     * @param user 用户表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    /**
     * 查询所有用户表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<User> list() {
        return userService.list();
    }

    /**
     * 分页查询用户表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<User> page(Page<User> page) {
        return userService.page(page);
    }

}
