package cn.rescld.aicodegeneratebackend.core;

import cn.rescld.aicodegeneratebackend.ai.AiCodeService;
import cn.rescld.aicodegeneratebackend.core.parser.CodeParserExecutor;
import cn.rescld.aicodegeneratebackend.core.saver.CodeFileSaverExecutor;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * 门面设计模式，组合生成与保存代码功能，
 * 给外部提供统一的调用方式
 */
@Slf4j
@Service
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeService aiCodeService;

    /**
     * 使用 AI 代码生成器的统一入口
     *
     * @param type        生成代码类型
     * @param userMessage 用户提示词
     * @return 文件保存的目录
     */
    public Flux<String> generateAndSaveCode(String userMessage, CodeGenTypeEnum type, Long appId) {
        ThrowUtils.throwIf(type == null, ErrorCode.SYSTEM_ERROR, "生成方式不可为空");

        // 调用 AI 大模型生成代码
        Flux<String> result = null;
        switch (type) {
            case SINGLE_HTML -> result = aiCodeService.generateSingleHtmlCode(userMessage);
            case MULTI_FILE -> result = aiCodeService.generateMultiFileCode(userMessage);
        }

        StringBuilder builder = new StringBuilder();
        return result
                // 将每次生成的结果保存下来
                .doOnNext(builder::append)
                // 全部完成后，将代码保存到本地
                .doOnComplete(() -> {
                    try {
                        Object parsed = CodeParserExecutor.execute(builder.toString(), type);
                        File file = CodeFileSaverExecutor.execute(parsed, type, appId);
                        log.info("文件保存成功：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("文件保存失败：{}", e.getMessage());
                    }
                });
    }
}
