package cn.rescld.aicodegeneratebackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 964019593749517416L;

    /**
     * 应用名称
     */
    private String name;
}