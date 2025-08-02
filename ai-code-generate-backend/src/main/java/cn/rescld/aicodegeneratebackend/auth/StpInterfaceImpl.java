package cn.rescld.aicodegeneratebackend.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.rescld.aicodegeneratebackend.mapper.UserMapper;
import cn.rescld.aicodegeneratebackend.model.entity.User;
import com.mybatisflex.core.query.QueryChain;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        QueryChain<User> queryChain = new QueryChain<>(userMapper);
        User user = queryChain.eq(User::getId, loginId).one();

        ArrayList<String> list = new ArrayList<>();
        switch (user.getRole()) {
            case 0: list.add("admin");
            case 1: list.add("user");
        }
        return list;
    }
}
