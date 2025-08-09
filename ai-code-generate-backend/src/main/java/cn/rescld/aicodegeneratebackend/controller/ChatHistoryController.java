package cn.rescld.aicodegeneratebackend.controller;

import cn.rescld.aicodegeneratebackend.common.BaseResponse;
import cn.rescld.aicodegeneratebackend.common.ResultUtils;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import cn.rescld.aicodegeneratebackend.model.entity.ChatHistory;
import cn.rescld.aicodegeneratebackend.service.ChatHistoryService;

import java.util.List;

/**
 * 对话历史记录表 控制层。
 *
 * @author 残云cyun
 * @since 2025-08-09
 */
@RestController
@RequestMapping("/chat_history")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * 根据游标查询对话历史记录表。
     *
     * @param appId 应用 id
     * @param count 查询条数
     * @return 查询结果
     */
    @GetMapping("list")
    public BaseResponse<List<ChatHistory>> listChatHistory(@RequestParam Long appId,
                                                           @RequestParam Long count,
                                                           @RequestParam String lastCreateTime) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(count == null || count <= 0, ErrorCode.PARAMS_ERROR, "查询数量不能为空");

        List<ChatHistory> list = chatHistoryService.chatHistoryList(appId, count, lastCreateTime);

        return ResultUtils.success(list);
    }
}
