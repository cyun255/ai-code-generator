package cn.rescld.aicodegeneratebackend.core.handler;

import cn.rescld.aicodegeneratebackend.model.enums.MessageTypeEnum;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器
 * 处理原生三件套方式生成的简单流式数据
 */
public class SimpleTextStreamHandler {
    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               long appId, Long userId) {
        StringBuilder builder = new StringBuilder();
        return originFlux
                .doOnNext(builder::append)
                .doOnComplete(() -> chatHistoryService.addChatMessage(builder.toString(),
                        MessageTypeEnum.AI.getType(), appId, userId))
                .doOnError(error -> {
                    String errorMessage = "AI 回复失败：" + error.getMessage();
                    chatHistoryService.addChatMessage(errorMessage, MessageTypeEnum.AI.getType(), appId, userId);
                });
    }
}
