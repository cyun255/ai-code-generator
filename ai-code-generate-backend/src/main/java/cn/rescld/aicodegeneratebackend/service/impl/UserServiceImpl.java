package cn.rescld.aicodegeneratebackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.rescld.aicodegeneratebackend.common.ResultUtils;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.vo.UserVO;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.rescld.aicodegeneratebackend.model.entity.User;
import cn.rescld.aicodegeneratebackend.mapper.UserMapper;
import cn.rescld.aicodegeneratebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户表 服务层实现。
 *
 * @author 残云cyun
 * @since 2025-08-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Long register(String username, String password) {
        // 用户名长度不能少于4位
        ThrowUtils.throwIf(username.length() < 4 || username.length() > 16,
                ErrorCode.PARAMS_ERROR, "用户名长度为4~16位");

        // 密码长度8~16位
        ThrowUtils.throwIf(password.length() < 8 || password.length() > 32,
                ErrorCode.PARAMS_ERROR, "密码长度为8~32位");

        // 检查用户名是否已被注册
        // TODO: 因为使用了 Mybatis Flex 逻辑删除，导致这里查不到已经被注销的用户，等后续修复
        boolean exists = this.queryChain()
                .eq(User::getUsername, username)
                .exists();
        ThrowUtils.throwIf(exists, ErrorCode.PARAMS_ERROR, "该账号已被注册");

        // 密码加密
        String salt = RandomUtil.randomString(8);
        String encrypted = encryptPassword(password, salt);

        // 保存
        User user = new User();
        user.setUsername(username);
        user.setPassword(encrypted);
        user.setName("用户" + username);
        user.setSalt(salt);
        this.save(user);

        // 返回用户id
        return user.getId();
    }

    @Override
    public User login(String username, String password) {
        // 校验参数
        ThrowUtils.throwIf(username.length() < 4 ||
                password.length() < 8 ||
                password.length() > 16, ErrorCode.PARAMS_ERROR, "用户名或密码错误");

        // 根据用户名查询用户信息
        User user = this.queryChain()
                .eq(User::getUsername, username)
                .one();
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户名或密码错误");

        // 加密
        String salt = user.getSalt();
        String encrypted = encryptPassword(password, salt);

        // 校验用户名与密码是否匹配
        user = this.queryChain()
                .eq(User::getUsername, username)
                .eq(User::getPassword, encrypted)
                .one();
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户名或密码错误");

        return user;
    }

    @Override
    public <T> User update(Long id, T request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 从数据库中查询用户信息
        User user = this.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");

        // 将最新的信息插入数据库
        BeanUtils.copyProperties(request, user);
        this.updateById(user);

        // 将最新的信息返回
        return this.getById(id);
    }

    private String encryptPassword(String password, String salt) {
        return DigestUtil.sha256Hex(salt + password + salt);
    }
}
