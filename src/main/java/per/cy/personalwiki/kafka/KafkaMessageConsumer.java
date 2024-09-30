package per.cy.personalwiki.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.service.DocService;
import per.cy.personalwiki.websocket.WebSocketServer;

@Service
public class KafkaMessageConsumer {

    @Autowired
    WebSocketServer webSocketServer;
    public static Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);
    public static final String voteTOPIC = "Vote";
    @KafkaListener(topics = voteTOPIC, groupId = "rootConsumer")
    public void consume(ConsumerRecord<String, String> record) {
        String msg = record.value();
        logger.info("从 Kafka 收到消息：{}", msg);
        // 原来使用 WebSocket 发送消息的逻辑
        webSocketServer.sendInfo(msg);
    }
}