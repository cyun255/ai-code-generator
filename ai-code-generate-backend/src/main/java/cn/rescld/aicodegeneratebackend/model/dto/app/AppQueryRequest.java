package cn.rescld.aicodegeneratebackend.model.dto.app;

import cn.rescld.aicodegeneratebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3215910838492087067L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 是否删除
     */
    private Integer isDelete;
}