package cn.rescld.aicodegeneratebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.enums.MessageTypeEnum;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.rescld.aicodegeneratebackend.model.entity.ChatHistory;
import cn.rescld.aicodegeneratebackend.mapper.ChatHistoryMapper;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 对话历史记录表 服务层实现。
 *
 * @author 残云cyun
 * @since 2025-08-09
 */
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
}
