package cn.rescld.aicodegeneratebackend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 240758512815327028L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 展示的用户昵称
     */
    private String name;

    /**
     * 用户头像超链接
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String profile;
}
