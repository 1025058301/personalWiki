package per.cy.personalwiki.scheduleJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.cy.personalwiki.service.EbookService;
import per.cy.personalwiki.service.EbookSnapshotService;
import per.cy.personalwiki.utils.SnowFlake;

@Component
public class GenSnapshotJob {

    private static final Logger logger = LoggerFactory.getLogger(GenSnapshotJob.class);
    @Autowired
    EbookSnapshotService ebookSnapshotService;
    @Autowired
    SnowFlake snowFlake;

    /**
     * 固定时间间隔，fixedRate单位毫秒
     */
//    @Scheduled(fixedRate = 5000)
//    public void simple() throws InterruptedException {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
//        String dateString = formatter.format(new Date());
////        Thread.sleep(2000);
//        LOG.info("每隔5秒钟执行一次： {}", dateString);
//    }

    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     */
    @Scheduled(cron = "10/60 * * * * ?")
    public void cron() throws InterruptedException {
        MDC.put("LOG_ID",String.valueOf(snowFlake.nextId()));
        logger.info("定时生成今日电子书快照开始");
        long current=System.currentTimeMillis();
        ebookSnapshotService.genSnapshot();
        logger.info("生成今日电子书快照结束，耗时{}ms",System.currentTimeMillis()-current);
    }

}

