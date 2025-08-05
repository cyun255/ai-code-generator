package cn.rescld.aicodegeneratebackend.model.dto.user;

import cn.rescld.aicodegeneratebackend.common.PageRequest;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3215910838492087066L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 展示的用户昵称
     */
    private String name;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 0-admin; 1-user
     */
    private Integer role;
}
