package cn.rescld.aicodegeneratebackend.ai;

import cn.rescld.aicodegeneratebackend.ai.tools.FileWriteTool;
import cn.rescld.aicodegeneratebackend.exception.BusinessException;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
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
    private StreamingChatModel deepSeekChatModel;

    @Resource
    private StreamingChatModel deepSeekReasonerModel;

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
        return aiCodeServiceCache.get(appId, key -> createAiCodeService(appId, CodeGenTypeEnum.MULTI_FILE));
    }

    /**
     * 重载方法，不影响原有代码运行
     */
    public AiCodeService getAiCodeService(Long appId, CodeGenTypeEnum codeGenType) {
        return aiCodeServiceCache.get(appId, key -> createAiCodeService(appId, codeGenType));
    }

    /**
     * 为每个应用单独创建 AiCodeService
     *
     * @param appId 应用 id
     * @return 创建好的 AiCodeService
     */
    private AiCodeService createAiCodeService(Long appId, CodeGenTypeEnum codeGenTypeEnum) {
        MessageWindowChatMemory chatMemory = getRedisChatMemory(appId);
        return switch (codeGenTypeEnum) {
            case SINGLE_HTML,
                 MULTI_FILE -> AiServices.builder(AiCodeService.class)
                    .streamingChatModel(deepSeekChatModel)
                    .chatMemoryProvider(id -> chatMemory)
                    .build();
            case VUE_PROJECT -> AiServices.builder(AiCodeService.class)
                    .streamingChatModel(deepSeekReasonerModel)
                    .chatMemoryProvider(id -> chatMemory)
                    .tools(new FileWriteTool())
                    .hallucinatedToolNameStrategy(toolExecutionRequest ->
                            ToolExecutionResultMessage.from(toolExecutionRequest,
                                    "Error: there is no tool called " + toolExecutionRequest.name()))
                    .build();
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持该代码生成方式");
        };
    }

    /**
     * 根据应用 id 构造 redis 对话记忆
     *
     * @param appId 应用 id
     * @return 对话记忆实例
     */
    private MessageWindowChatMemory getRedisChatMemory(Long appId) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();

        int count = chatHistoryService.loadHistoryMessages(appId, chatMemory, 20);
        log.info("load chat history count:{}", count);
        return chatMemory;
    }
}
