package per.cy.personalwiki.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() throws IOException {
        // 从 YAML 文件加载配置
        Config config = Config.fromYAML(new File("src/main/resources/config/redisson.yaml"));
        return Redisson.create(config);
    }
}
