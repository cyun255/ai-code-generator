package cn.rescld.aicodegeneratebackend.model.dto.app;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 964019593749517415L;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;
}