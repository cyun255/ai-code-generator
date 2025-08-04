package cn.rescld.aicodegeneratebackend.core;

import cn.rescld.aicodegeneratebackend.ai.AiCodeService;
import cn.rescld.aicodegeneratebackend.ai.model.MultiFileCodeResult;
import cn.rescld.aicodegeneratebackend.ai.model.SingleHtmlCodeResult;
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
    public Flux<String> generateAndSaveCode(CodeGenTypeEnum type, String userMessage) {
        ThrowUtils.throwIf(type == null, ErrorCode.SYSTEM_ERROR, "不支持该生产方式");
        return switch (type) {
            case SINGLE_HTML -> generateAndSaveSingleHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiCode(userMessage);
        };
    }

    private Flux<String> generateAndSaveMultiCode(String userMessage) {
        // 调用 AI 大模型，生成结果
        Flux<String> result = aiCodeService.generateMultiFileCode(userMessage);
        StringBuilder builder = new StringBuilder();
        return result
                // 将每次生成的结果保存下来
                .doOnNext(builder::append)
                // 全部完成后，将代码保存到本地
                .doOnComplete(() -> {
                    try {
                        MultiFileCodeResult parsed = CodeParser.parseMultiFileCode(builder.toString());
                        File file = CodeFileSaver.saveMultiFile(parsed);
                        log.info("文件保存成功：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("文件保存失败：{}", e.getMessage());
                    }
                });
    }

    private Flux<String> generateAndSaveSingleHtmlCode(String userMessage) {
        Flux<String> result = aiCodeService.generateSingleHtmlCode(userMessage);
        StringBuilder builder = new StringBuilder();
        return result
                .doOnNext(builder::append)
                .doOnComplete(() -> {
                    try {
                        SingleHtmlCodeResult parsed = CodeParser.parseHtmlCode(builder.toString());
                        File file = CodeFileSaver.saveSingleHtmlFile(parsed);
                        log.info("文件保存成功：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("文件保存失败：{}", e.getMessage());
                    }
                });
    }
}
