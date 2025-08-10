package cn.rescld.aicodegeneratebackend.service;

import com.mybatisflex.core.service.IService;
import cn.rescld.aicodegeneratebackend.model.entity.ChatHistory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.util.List;

/**
 * 对话历史记录表 服务层。
 *
 * @author 残云cyun
 * @since 2025-08-09
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加聊天记录记录
     *
     * @param message     消息
     * @param messageType 消息类型
     * @param appId       应用 id
     * @param userId      用户 id
     * @return {@code true} 添加成功 {@code false} 添加失败
     */
    boolean addChatMessage(String message,
                           String messageType,
                           Long appId, Long userId);

    /**
     * 根据游标查询历史聊天记录
     *
     * @param appId          应用 id
     * @param pageSize       查询条数
     * @param lastCreateTime 最后创建时间（游标）
     * @return 查询到的历史记录
     */
    List<ChatHistory> chatHistoryList(Long appId, Long pageSize,
                                      String lastCreateTime);

    /**
     * 装载会话记忆
     *
     * @param appId      应用 id
     * @param chatMemory 会话记忆对象
     * @param count      加载条数
     * @return 成功加载的条数
     */
    int loadHistoryMessages(Long appId, MessageWindowChatMemory chatMemory, int count);
}
