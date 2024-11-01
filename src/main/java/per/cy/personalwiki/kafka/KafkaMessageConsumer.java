package per.cy.personalwiki.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.service.DocService;
import per.cy.personalwiki.utils.DelayedMessage;
import per.cy.personalwiki.websocket.WebSocketServer;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaMessageConsumer {

    @Autowired
    WebSocketServer webSocketServer;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);
    public static final String voteTOPIC = "Vote";
    public static final String deleteCacheTOPIC = "CacheDelete";
    public static final String deleteCacheDleTOPIC = "CacheDelete_dle";



    private final DelayQueue<DelayedMessage> delayQueue = new DelayQueue<>();
    @KafkaListener(topics = voteTOPIC, groupId = "rootConsumer")
    public void consumeVote(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String msg = record.value();
        logger.info("从 Kafka 收到消息：{}", msg);
        // 原来使用 WebSocket 发送消息的逻辑
        webSocketServer.sendInfo(msg);
        acknowledgment.acknowledge();
    }
    @KafkaListener(topics = deleteCacheTOPIC, groupId = "rootConsumer")
    public void consumeDeleteCache(ConsumerRecord<String, String> record,Acknowledgment acknowledgment) {
        String cacheKey = record.value();
        logger.info("Kafka收到删除消息，对应缓存的key为"+cacheKey+",删除redis中的该key。");
        redisTemplate.delete(cacheKey);
        acknowledgment.acknowledge();
    }
//    @Scheduled(fixedRate = 500)
//    public void consumeDelayedMessages() {
//        DelayedMessage delayedMessage;
//        while ((delayedMessage = delayQueue.poll()) != null) {
//            try{
//                if (delayedMessage != null) {
//                    redisTemplate.delete(delayedMessage.getMessage());
//                    logger.info("从延时队列中取出消息，删除key为"+delayedMessage.getMessage()+" 的缓存");
//                }
//            }catch (Exception e){
//                logger.error(e.getMessage());
//                logger.info("删除key为"+delayedMessage.getMessage()+" 的缓存失败，当前重试次数为 "+delayedMessage.getRetryTime());
//                retryOrSendToDlt(delayedMessage);
//            }
//
//        }
//    }
//    private void retryOrSendToDlt(DelayedMessage delayedMessage) {
//        int nextRetryTime = delayedMessage.getRetryTime() + 1;
//        switch (delayedMessage.getRetryTime()) {
//            case 0:
//                delayQueue.offer(new DelayedMessage(delayedMessage.getMessage(), 2, TimeUnit.SECONDS, nextRetryTime));
//                break;
//            case 1:
//                delayQueue.offer(new DelayedMessage(delayedMessage.getMessage(), 4, TimeUnit.SECONDS, nextRetryTime));
//                break;
//            case 2:
//                delayQueue.offer(new DelayedMessage(delayedMessage.getMessage(), 8, TimeUnit.SECONDS, nextRetryTime));
//                break;
//            default:
//                kafkaTemplate.send(deleteCacheDleTOPIC, "缓存 key: " + delayedMessage.getMessage() + " 重试 3 次后删除失败");
//        }
//    }
}