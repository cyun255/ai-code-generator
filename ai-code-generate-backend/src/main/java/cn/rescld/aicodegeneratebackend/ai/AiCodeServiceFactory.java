package cn.rescld.aicodegeneratebackend.ai;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCodeServiceFactory {

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Bean
    public AiCodeService aiCodeService() {
        return AiServices.builder(AiCodeService.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(id -> MessageWindowChatMemory
                        .builder()
                        .id(id)
                        .chatMemoryStore(redisChatMemoryStore)
                        .maxMessages(20)
                        .build())
                .build();
    }
}
