package cn.rescld.aicodegeneratebackend.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class AiCodeServiceFactoryTest {

    @Resource
    private AiCodeServiceFactory aiCodeServiceFactory;

    @Test
    void getAiCodeService() {
        AiCodeService aiCodeService = aiCodeServiceFactory.getAiCodeService(310657853712568320L);
        log.info(aiCodeService.toString());
        Flux<String> stringFlux = aiCodeService.generateSingleHtmlCode(310657853712568320L, "精简地总结一下前面的内容");
        stringFlux.subscribe(System.out::println);
    }
}