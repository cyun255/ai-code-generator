package cn.rescld.aicodegeneratebackend.ai.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式响应基类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamMessage {
    private String type;
}
