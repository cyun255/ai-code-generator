package cn.rescld.aicodegeneratebackend.core;

import cn.rescld.aicodegeneratebackend.ai.AiCodeService;
import cn.rescld.aicodegeneratebackend.ai.AiCodeServiceFactory;
import cn.rescld.aicodegeneratebackend.core.parser.CodeParserExecutor;
import cn.rescld.aicodegeneratebackend.core.saver.CodeFileSaverExecutor;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.TokenStream;
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
    private AiCodeServiceFactory aiCodeServiceFactory;

    /**
     * 使用 AI 代码生成器的统一入口
     *
     * @param type        生成代码类型
     * @param userMessage 用户提示词
     * @param appId       应用id
     * @return 文件保存的目录
     */
    public Flux<String> generateAndSaveCode(String userMessage, CodeGenTypeEnum type, Long appId) {
        ThrowUtils.throwIf(type == null, ErrorCode.SYSTEM_ERROR, "生成方式不可为空");

        // 根据应用 id 获取 AI 服务
        AiCodeService aiCodeService = aiCodeServiceFactory.getAiCodeService(appId, type);

        // 调用 AI 大模型生成代码
        return switch (type) {
            case SINGLE_HTML -> {
                Flux<String> stringFlux = aiCodeService.generateSingleHtmlCode(appId, userMessage);
                yield processSimpleStream(stringFlux, type, appId);
            }
            case MULTI_FILE -> {
                Flux<String> stringFlux = aiCodeService.generateMultiFileCode(appId, userMessage);
                yield processSimpleStream(stringFlux, type, appId);
            }
            case VUE_PROJECT -> {
                TokenStream tokenStream = aiCodeService.generateVueProjectCode(appId, userMessage);
                yield processTokenStream(tokenStream);
            }
        };
    }

    /**
     * 将 TokenStream 转成外部使用的 Flux
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse(sink::next)
                    .onPartialThinking(partialThinking ->
                            log.info("partial thinking:{}", partialThinking))
                    .onError(error -> {
                        log.info(error.getMessage());
                        sink.error(error);
                    })
                    .start();
        });
    }

    /**
     * 处理简单的 Stream 流，保存文件
     */
    private Flux<String> processSimpleStream(Flux<String> stream, CodeGenTypeEnum type, Long appId) {
        StringBuilder builder = new StringBuilder();
        return stream
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
