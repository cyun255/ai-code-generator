package cn.rescld.aicodegeneratebackend.core.builder;

import cn.rescld.aicodegeneratebackend.constant.AppConstant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class VueProjectBuilderTest {

    @Resource
    private VueProjectBuilder vueProjectBuilder;

    @Test
    void buildProjectAsync() throws InterruptedException {
        String projectPath = AppConstant.CODE_GEN_ROOT + "/vue_project_" + 316401289015590912L;
        vueProjectBuilder.buildProjectAsync(projectPath);
    }
}