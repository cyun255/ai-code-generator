package cn.rescld.aicodegeneratebackend.model.vo;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表 实体类。
 *
 * @author 残云cyun
 * @since 2025-08-01
 */
@Data
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 9054411253572002981L;

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
     * 用户头像超链接
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 0-admin; 1-user
     */
    private Integer role;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;
}
