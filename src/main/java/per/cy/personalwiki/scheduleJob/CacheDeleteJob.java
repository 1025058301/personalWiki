package per.cy.personalwiki.scheduleJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.cy.personalwiki.kafka.KafkaMessageConsumer;
import per.cy.personalwiki.utils.RedisUtil;

import java.util.Set;

@Component
public class CacheDeleteJob {
    private static final Logger logger = LoggerFactory.getLogger(CacheDeleteJob.class);

    @Autowired
    public RedisUtil redisUtil;
    @Autowired
    public KafkaTemplate kafkaTemplate;

    @Scheduled(fixedRate = 500)
    public void produceCacheDeleteMessage() {
        Set<Object> cacheKeys = redisUtil.getExpiredDelayTasks(RedisUtil.cacheDeleteDelayQueue);
        for (Object key : cacheKeys) {
            logger.info("定时任务生效，获取需要延时删除的key");
            kafkaTemplate.send(KafkaMessageConsumer.deleteCacheTOPIC, key).addCallback(
                    success -> logger.info("消息发送成功，需要被删除的key是{}", key),
                    failure -> {
                        logger.error("消息发送失败，需要被删除的key是{}，错误信息：{}", key, failure.getMessage());
                        // 在这里实现重试逻辑，例如重新发送消息
                        redisUtil.addCacheDelayTask(key.toString(),1000);
                    });
            logger.info("向kafka发送删除消息，需要被删除的key是{}", key);
        }
    }
}
