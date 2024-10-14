package per.cy.personalwiki.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic DleTOPIC() {
        return new NewTopic("CacheDelete_dle", 3, (short) 1); // 3个分区，2个副本
    }
    @Bean
    public NewTopic CacheTOPIC() {
        return new NewTopic("CacheDelete", 3, (short) 1); // 3个分区，2个副本
    }
}
