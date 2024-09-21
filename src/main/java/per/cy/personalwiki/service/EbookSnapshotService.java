package per.cy.personalwiki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.mapper.EbookSnapshotMapper;
import per.cy.personalwiki.resp.StatisticResp;

import java.util.List;

@Service
public class EbookSnapshotService {
    @Autowired
    EbookSnapshotMapper ebookSnapshotMapper;
    public void genSnapshot(){
        ebookSnapshotMapper.genSnapshot();
    }

    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapper.getStatistic();
    }
}
