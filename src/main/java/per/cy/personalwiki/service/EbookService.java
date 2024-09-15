package per.cy.personalwiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import per.cy.personalwiki.mapper.EbookMapper;
import per.cy.personalwiki.pojo.Ebook;
import per.cy.personalwiki.pojo.EbookExample;
import per.cy.personalwiki.resp.EbookResp;
import per.cy.personalwiki.utils.CopyUtil;
import per.cy.personalwiki.req.EbookRequest;

import java.util.List;

@Service
public class EbookService {
    public static Logger logger= LoggerFactory.getLogger(EbookService.class);
    @Autowired
    EbookMapper ebookMapper;

    public List<EbookResp> selectByExample(EbookRequest ebookRequest) {
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        if(!ObjectUtils.isEmpty(ebookRequest.getName())){
            criteria.andNameLike("%" + ebookRequest.getName() + "%");//%是通配符，可以匹配零个或多个任意字符
        }
        PageHelper.startPage(2,3);
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        logger.info("总行数：{}", pageInfo.getTotal());
        logger.info("总页数：{}", pageInfo.getPages());
        return CopyUtil.copyList(ebooks, EbookResp.class);


    }
}
