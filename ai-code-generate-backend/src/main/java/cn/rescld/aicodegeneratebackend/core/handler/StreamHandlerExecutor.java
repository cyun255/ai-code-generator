package cn.rescld.aicodegeneratebackend.core.handler;

import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class StreamHandlerExecutor {

    @Resource
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    public Flux<String> execute(Flux<String> originFlux,
                                ChatHistoryService chatHistoryService,
                                CodeGenTypeEnum codeGenTypeEnum,
                                long appId, Long userId) {
        return switch (codeGenTypeEnum) {
            case SINGLE_HTML,
                 MULTI_FILE -> new SimpleTextStreamHandler().handle(originFlux, chatHistoryService, appId, userId);
            case VUE_PROJECT -> jsonMessageStreamHandler.handle(originFlux, chatHistoryService, appId, userId);
        };
    }
}
