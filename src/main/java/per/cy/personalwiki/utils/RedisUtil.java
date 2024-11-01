package per.cy.personalwiki.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RedisUtil.class);

    public static String cacheDeleteDelayQueue="cacheDeleteDelayQueue";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * true：不存在，放一个KEY
     * false：已存在
     * @param key
     * @param second
     * @return
     */
    public boolean validateRepeat(String key, long second) {
        if (redisTemplate.hasKey(key)) {
            LOG.info("key已存在：{}", key);
            return false;
        } else {
            LOG.info("key不存在，放入：{}，过期 {} 秒", key, second);
            redisTemplate.opsForValue().set(key, key, second, TimeUnit.SECONDS);
            return true;
        }
    }
    public void addCacheDelayTask(String cacheKey, long delayTime) {
        long delayTimeMillis=System.currentTimeMillis()+delayTime;
        double score=(double) delayTimeMillis;
        LOG.info("向redis延时队列添加延时任务：删除缓存的key为{}, 过期时间是{}", cacheKey, score);
        redisTemplate.opsForZSet().add(cacheDeleteDelayQueue, cacheKey, score);
    }
    public Set<Object> getExpiredDelayTasks(String key) {
        // 获取到期的任务
        long currentTime=System.currentTimeMillis();
        Set<Object> expiredTasks = redisTemplate.opsForZSet().rangeByScore(key, 0, currentTime);

        if (expiredTasks != null && !expiredTasks.isEmpty()) {
            // 删除获取到的到期任务
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, currentTime);
            LOG.info("获取并删除了{}个到期任务，当前时间戳：{}", expiredTasks.size(), currentTime);
        } else {
//            LOG.info("没有找到到期的任务，当前时间戳：{}", currentTime);
        }
        return expiredTasks;
    }
}

