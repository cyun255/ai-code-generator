package cn.rescld.aicodegeneratebackend.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.common.BaseResponse;
import cn.rescld.aicodegeneratebackend.common.ResultUtils;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.dto.user.*;
import cn.rescld.aicodegeneratebackend.model.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import cn.rescld.aicodegeneratebackend.model.entity.User;
import cn.rescld.aicodegeneratebackend.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表 控制层。
 *
 * @author 残云cyun
 * @since 2025-08-01
 */
@Slf4j
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
     * 根据登录态获取用户信息。
     *
     * @return 用户详情信息
     */
    @SaCheckLogin
    @GetMapping
    public BaseResponse<UserVO> getInfo() {
        Long id = StpUtil.getLoginIdAsLong();
        User user = userService.queryChain().eq(User::getId, id).one();
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ResultUtils.success(vo);
    }

    /**
     * 根据登录态删除用户。
     */
    @SaCheckLogin
    @DeleteMapping
    public BaseResponse<?> remove() {
        Long id = StpUtil.getLoginIdAsLong();
        if (userService.removeById(id)) {
            return ResultUtils.success("用户删除成功");
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "用户删除失败");
    }

    /**
     * 根据登录态更新用户信息。
     *
     * @param request 用户表
     * @return 最新的用户信息
     */
    @SaCheckLogin
    @PutMapping
    public BaseResponse<UserVO> update(@RequestBody UserUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 从数据库中查询用户信息
        Long id = StpUtil.getLoginIdAsLong();
        AdminUpdateRequest updateRequest = new AdminUpdateRequest();
        BeanUtils.copyProperties(request, updateRequest);
        updateRequest.setId(id);
        User user = userService.update(updateRequest);

        // 将最新的信息返回给前端
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ResultUtils.success(vo);
    }

    // endregion

    // region admin

    /**
     * 分页查询用户表。
     *
     * @param request 分页对象
     * @return 脱敏后的查询结果
     */
    @SaCheckRole("admin")
    @GetMapping("page")
    public BaseResponse<Page<UserVO>> page(@ModelAttribute UserQueryRequest request) {
        // 查询分页结果
        Page<User> page = new Page<>();
        page.setPageNumber(request.getCurrent());
        page.setPageSize(request.getPageSize());
        userService.queryChain()
                .eq(User::getId, request.getId())
                .eq(User::getRole, request.getRole())
                .like(User::getName, request.getName())
                .like(User::getUsername, request.getUsername())
                .like(User::getProfile, request.getProfile())
                .page(page);

        // 数据脱敏
        List<UserVO> collect = page.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        Page<UserVO> vo = new Page<>();
        vo.setPageNumber(page.getPageNumber());
        vo.setPageSize(page.getPageSize());
        vo.setTotalRow(page.getTotalRow());
        vo.setTotalPage(page.getTotalPage());
        vo.setRecords(collect);
        return ResultUtils.success(vo);
    }

    /**
     * 根据用户 id 更新用户信息
     *
     * @param request 更新对象
     * @return 脱敏后的最新用户信息
     */
    @SaCheckRole("admin")
    @PostMapping("update")
    public BaseResponse<UserVO> updateById(@RequestBody AdminUpdateRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 从数据库中查询用户信息
        User user = userService.update(request);

        // 将最新的信息返回给前端
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ResultUtils.success(vo);
    }

    // endregion
}
