package cn.rescld.aicodegeneratebackend.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

@SpringBootTest
@ActiveProfiles("dev")
class AiCodeServiceTest {

    @Resource
    private AiCodeService aiCodeService;

    @Test
    void generateSingleHtmlCode() {
        Flux<String> chat = aiCodeService.generateSingleHtmlCode("跟我生成一个登录页面，不超过20行代码");
        chat.subscribe(System.out::print);
        System.out.println();
    }

    @Test
    void generateMultiFileCode() {
        Flux<String> chat = aiCodeService.generateMultiFileCode("跟我生成一个登录页面，不超过20行代码");
        chat.subscribe(System.out::print);
        System.out.println();
    }
}