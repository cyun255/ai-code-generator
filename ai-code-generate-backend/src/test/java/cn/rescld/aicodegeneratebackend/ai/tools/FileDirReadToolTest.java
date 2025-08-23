package cn.rescld.aicodegeneratebackend.ai.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class FileDirReadToolTest {

    @Resource
    private FileDirReadTool fileDirReadTool;

    @Test
    void readDir() {
        String result = fileDirReadTool.readDir("./", 316401289015590912L);
        System.out.println(result);
    }
}