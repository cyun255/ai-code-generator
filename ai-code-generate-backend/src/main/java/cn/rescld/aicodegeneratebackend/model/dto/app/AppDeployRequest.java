package cn.rescld.aicodegeneratebackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppDeployRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2544368303415897940L;

    /**
     * 应用 id
     */
    private Long appId;
}
