package cn.rescld.aicodegeneratebackend;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("cn.rescld.aicodegeneratebackend.mapper")
public class AiCodeGenerateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCodeGenerateBackendApplication.class, args);
    }

}
