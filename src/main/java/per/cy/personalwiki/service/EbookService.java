package per.cy.personalwiki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.mapper.DemoMapper;
import per.cy.personalwiki.mapper.EbookMapper;
import per.cy.personalwiki.pojo.Demo;
import per.cy.personalwiki.pojo.DemoExample;
import per.cy.personalwiki.pojo.Ebook;
import per.cy.personalwiki.pojo.EbookExample;

import java.util.List;

@Service
public class EbookService {
    @Autowired
    EbookMapper ebookMapper;

    public List<Ebook> selectByExample(EbookExample ebookExample){
        return ebookMapper.selectByExample(ebookExample);
    }

}
