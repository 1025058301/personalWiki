package per.cy.personalwiki.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cy.personalwiki.mapper.DemoMapper;
import per.cy.personalwiki.mapper.EbookMapper;
import per.cy.personalwiki.pojo.Demo;
import per.cy.personalwiki.pojo.DemoExample;
import per.cy.personalwiki.pojo.Ebook;
import per.cy.personalwiki.pojo.EbookExample;
import per.cy.personalwiki.resp.EbookResp;
import res.EbookRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Autowired
    EbookMapper ebookMapper;

    public List<EbookResp> selectByExample(EbookRequest ebookRequest){
        EbookExample example=new EbookExample();
        EbookExample.Criteria criteria=example.createCriteria();
        criteria.andNameLike("%"+ebookRequest.getName()+"%");//%是通配符，可以匹配零个或多个任意字符
        List<Ebook> ebooks=ebookMapper.selectByExample(example);
        List<EbookResp> ebookResps=new ArrayList<>();
        for(Ebook ebook:ebooks){
            EbookResp ebookResp=new EbookResp();
            BeanUtils.copyProperties(ebook,ebookResp);
            ebookResps.add(ebookResp);
        }
        return ebookResps;

    }

}
