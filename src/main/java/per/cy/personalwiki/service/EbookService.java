package per.cy.personalwiki.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import per.cy.personalwiki.mapper.EbookMapper;
import per.cy.personalwiki.pojo.Ebook;
import per.cy.personalwiki.pojo.EbookExample;
import per.cy.personalwiki.resp.EbookResp;
import per.cy.personalwiki.utils.CopyUtil;
import per.cy.personalwiki.res.EbookRequest;

import java.util.List;

@Service
public class EbookService {
    @Autowired
    EbookMapper ebookMapper;

    public List<EbookResp> selectByExample(EbookRequest ebookRequest) {
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        if(!ObjectUtils.isEmpty(ebookRequest.getName())){
            criteria.andNameLike("%" + ebookRequest.getName() + "%");//%是通配符，可以匹配零个或多个任意字符
        }
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
        return CopyUtil.copyList(ebooks, EbookResp.class);

    }
}
