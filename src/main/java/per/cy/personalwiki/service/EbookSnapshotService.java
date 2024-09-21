package per.cy.personalwiki.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.mapper.EbookSnapshotMapper;
import per.cy.personalwiki.resp.StatisticResp;

import java.util.List;

@Service
public class EbookSnapshotService {
    public static Logger logger = LoggerFactory.getLogger(EbookSnapshotService.class);
    @Autowired
    EbookSnapshotMapper ebookSnapshotMapper;

    public void genSnapshot() {
        ebookSnapshotMapper.genSnapshot();
    }

    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapper.getStatistic();
    }

    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotMapper.get30Statistic();
    }
}
