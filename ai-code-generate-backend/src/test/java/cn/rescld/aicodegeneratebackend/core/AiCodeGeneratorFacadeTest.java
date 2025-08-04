package cn.rescld.aicodegeneratebackend.core;

import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveMultiCode() {
        String message = "给我生成一份登陆页面，不超过20行代码";
        Flux<String> result = aiCodeGeneratorFacade.generateAndSaveCode(message, CodeGenTypeEnum.MULTI_FILE);
        List<String> list = result.collectList().block();
        Assertions.assertNotNull(list);
        System.out.println(String.join("", list));
    }

    @Test
    void generateAndSaveSingleHtmlCode() {
        String message = "给我生成一份登陆页面，不超过20行代码";
        Flux<String> result = aiCodeGeneratorFacade.generateAndSaveCode(message, CodeGenTypeEnum.SINGLE_HTML);
        List<String> list = result.collectList().block();
        Assertions.assertNotNull(list);
        System.out.println(String.join("", list));
    }
}