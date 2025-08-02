package cn.rescld.aicodegeneratebackend.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 964019593749517414L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
