package cn.rescld.aicodegeneratebackend.ai;

import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class AiCodeServiceFactory {

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 缓存 AiCodeService
     */
    private final Cache<Long, AiCodeService> aiCodeServiceCache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(30))
                    .build();

    /**
     * 通过缓存获取 AiCodeService，若未命中则自动创建
     *
     * @param appId 应用 id
     * @return 创建好的 AiCodeService
     */
    public AiCodeService getAiCodeService(Long appId) {
        return aiCodeServiceCache.get(appId, this::createAiCodeService);
    }

    /**
     * 为每个应用单独创建 AiCodeService
     *
     * @param appId 应用 id
     * @return 创建好的 AiCodeService
     */
    private AiCodeService createAiCodeService(Long appId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();

        int count = chatHistoryService.loadHistoryMessages(appId, chatMemory, 20);
        log.info("load chat history count:{}", count);

        return AiServices.builder(AiCodeService.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(__ -> chatMemory)
                .build();
    }
}
