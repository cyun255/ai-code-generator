package cn.rescld.aicodegeneratebackend.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface AiCodeService {
    /**
     * 生成单个 HTML 文件代码
     *
     * @param message 用户提示词
     * @return 生成结果字符流
     */
    @SystemMessage(fromResource = "prompts/single-html-system-prompt.txt")
    Flux<String> generateSingleHtmlCode(@MemoryId Long appId, @UserMessage String message);

    /**
     * 生成多文件（HTML，CSS，JS）代码
     *
     * @param message 用户提示词
     * @return 生成的结果字符流
     */
    @SystemMessage(fromResource = "prompts/multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCode(@MemoryId Long appId, @UserMessage String message);

    /**
     * 生成 Vue 项目代码
     */
    @SystemMessage(fromResource = "prompts/vue-project-system.prompt.txt")
    TokenStream generateVueProjectCode(@MemoryId Long appId, @UserMessage String message);
}
