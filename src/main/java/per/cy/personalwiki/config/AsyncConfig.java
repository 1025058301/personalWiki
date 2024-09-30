//package per.cy.personalwiki.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import per.cy.personalwiki.service.WebSocketService;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Configuration
//@EnableAsync  // 启用异步任务支持
//public class AsyncConfig {
//    public static Logger logger= LoggerFactory.getLogger(AsyncConfig.class);
//
//    @Bean(name = "asyncTaskExecutor")  // 线程池的 Bean 名称
//    public Executor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        // 核心线程数
//        executor.setCorePoolSize(1);
//        // 最大线程数
//        executor.setMaxPoolSize(1);
//        // 队列容量
//        executor.setQueueCapacity(100);
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        // 线程名称前缀
//        executor.setThreadNamePrefix("Async-Thread-");
//        // 线程池初始化
//        executor.initialize();
//        logger.info("线程池初始化完成，线程池核心线程数{}，最大线程数{},拒绝策略{}",executor.getCorePoolSize(),executor.getMaxPoolSize(),executor.getThreadPoolExecutor().getRejectedExecutionHandler());
//        return executor;
//    }
//}