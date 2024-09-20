package per.cy.personalwiki.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.websocket.WebSocketServer;

@Service
public class WebSocketService {
    public static Logger logger= LoggerFactory.getLogger(WebSocketService.class);
    @Autowired
    WebSocketServer webSocketServer;
    @Async("asyncTaskExecutor")
    public void sendInfo(String msg,String logId){
        MDC.put("LOG_ID",logId);
        logger.info("异步执行通知操作，当前线程名-{}",Thread.currentThread().getName());
        webSocketServer.sendInfo(msg);
    }
}
