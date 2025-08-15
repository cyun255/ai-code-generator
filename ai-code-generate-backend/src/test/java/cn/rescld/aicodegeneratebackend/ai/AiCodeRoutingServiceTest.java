package cn.rescld.aicodegeneratebackend.ai;

import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class AiCodeRoutingServiceTest {

    @Resource
    private AiCodeRoutingService aiCodeRoutingService;

    @Test
    void routeCodeGenType() {
        CodeGenTypeEnum genTypeEnum = aiCodeRoutingService.routeCodeGenType("给我生成一个电商网站");
        log.info(genTypeEnum.getType());
    }
}