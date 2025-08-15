package cn.rescld.aicodegeneratebackend.ai;

import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "openAiChatModel")
public interface AiCodeRoutingService {
    @SystemMessage(fromResource = "prompts/smart-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodeGenType(String message);
}
