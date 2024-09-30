package per.cy.personalwiki.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;

@Configuration
public class RedissonConfig {
    @Autowired
    private ResourceLoader resourceLoader;
    @Bean
    public RedissonClient redissonClient() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:config/redisson.yaml");
        Config config = Config.fromYAML(resource.getInputStream());
        // 从 YAML 文件加载配置
        return Redisson.create(config);
    }
}
