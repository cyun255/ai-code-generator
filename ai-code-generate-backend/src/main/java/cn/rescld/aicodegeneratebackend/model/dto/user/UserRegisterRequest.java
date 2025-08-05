package cn.rescld.aicodegeneratebackend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5284024200941374798L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
