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
import per.cy.personalwiki.req.EbookSaveRequest;
import per.cy.personalwiki.resp.EbookQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.utils.CopyUtil;
import per.cy.personalwiki.req.EbookQueryRequest;

import java.util.List;

@Service
public class EbookService {
    public static Logger logger= LoggerFactory.getLogger(EbookService.class);
    @Autowired
    EbookMapper ebookMapper;

    public PageResp<EbookQueryResp> selectByExample(EbookQueryRequest ebookQueryRequest) {
        EbookExample example = new EbookExample();
        EbookExample.Criteria criteria = example.createCriteria();
        if(!ObjectUtils.isEmpty(ebookQueryRequest.getName())){
            criteria.andNameLike("%" + ebookQueryRequest.getName() + "%");//%是通配符，可以匹配零个或多个任意字符
        }
        PageHelper.startPage(ebookQueryRequest.getPage(), ebookQueryRequest.getSize());
        List<Ebook> ebooks = ebookMapper.selectByExample(example);
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebooks);
        logger.info("总行数：{}", pageInfo.getTotal());
        logger.info("总页数：{}", pageInfo.getPages());
        List<EbookQueryResp> resList=CopyUtil.copyList(ebooks, EbookQueryResp.class);
        PageResp<EbookQueryResp> resp=new PageResp<>();
        resp.setList(resList);
        resp.setTotalsPages((int)pageInfo.getTotal());
        return resp;
    }
    public void saveEbook(EbookSaveRequest ebookSaveRequest){
        Ebook ebook=CopyUtil.copyInstance(ebookSaveRequest,Ebook.class);
        if(ObjectUtils.isEmpty(ebook.getId())){
            ebookMapper.insert(ebook);
        }else {
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}
