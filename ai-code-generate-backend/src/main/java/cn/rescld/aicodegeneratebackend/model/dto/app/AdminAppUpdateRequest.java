package cn.rescld.aicodegeneratebackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminAppUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 964019593749517417L;

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
     * 优先级
     */
    private Integer priority;
}