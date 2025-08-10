package cn.rescld.aicodegeneratebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.enums.MessageTypeEnum;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.rescld.aicodegeneratebackend.model.entity.ChatHistory;
import cn.rescld.aicodegeneratebackend.mapper.ChatHistoryMapper;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史记录表 服务层实现。
 *
 * @author 残云cyun
 * @since 2025-08-09
 */
@Slf4j
@Service
public class ChatHistoryServiceImpl
        extends ServiceImpl<ChatHistoryMapper, ChatHistory>
        implements ChatHistoryService {

    @Override
    public boolean addChatMessage(String message,
                                  String messageType,
                                  Long appId, Long userId) {
        // 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(message), ErrorCode.PARAMS_ERROR, "提示词不能为空");
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id不可为空");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户id不可为空");

        MessageTypeEnum type = MessageTypeEnum.getEnumByType(messageType);
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "不支持该消息类型");

        // 添加到数据库
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType)
                .build();
        return this.save(chatHistory);
    }

    @Override
    public List<ChatHistory> chatHistoryList(Long appId, Long pageSize,
                                             String lastCreateTime) {
        // 校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0,
                ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 20,
                ErrorCode.PARAMS_ERROR, "只能查询20条以内消息");

        // 查询历史记录
        return this.queryChain()
                .eq(ChatHistory::getAppId, appId)
                .lt(ChatHistory::getCreateTime, lastCreateTime)
                .orderBy(ChatHistory::getCreateTime, false)
                .limit(pageSize)
                .list();
    }

    @Override
    public int loadHistoryMessages(Long appId, MessageWindowChatMemory chatMemory, int count) {
        // 查询历史记录
        List<ChatHistory> chatHistoryList = this.queryChain()
                .eq(ChatHistory::getAppId, appId)
                .lt(ChatHistory::getCreateTime, LocalDateTime.now().toString())
                .orderBy(ChatHistory::getCreateTime, false)
                // 用户请求时，第一时间就会将提示词加入数据库
                // 这里需要过滤掉用户刚发送的提示词，所以 offset = 1
                .limit(1, count)
                .list();
        if (CollUtil.isEmpty(chatHistoryList)) {
            return 0;
        }

        // 查询时是按照时间降序排列，要翻转后交给 AI
        chatHistoryList = chatHistoryList.reversed();

        // 先清空历史记录，防止重复
        chatMemory.clear();
        for (ChatHistory chatHistory : chatHistoryList) {
            switch (MessageTypeEnum.getEnumByType(chatHistory.getMessageType())) {
                case AI -> chatMemory.add(AiMessage.from(chatHistory.getMessage()));
                case USER -> chatMemory.add(UserMessage.from(chatHistory.getMessage()));
                case null -> log.error("错误历史记录类型，id: {}", chatHistory.getId());
            }
        }
        return chatHistoryList.size();
    }
}
