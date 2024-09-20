package per.cy.personalwiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import per.cy.personalwiki.exception.BusinessException;
import per.cy.personalwiki.exception.BusinessExceptionCode;
import per.cy.personalwiki.mapper.ContentMapper;
import per.cy.personalwiki.mapper.DocMapper;
import per.cy.personalwiki.pojo.Content;
import per.cy.personalwiki.pojo.Doc;
import per.cy.personalwiki.pojo.DocExample;
import per.cy.personalwiki.req.DocQueryRequest;
import per.cy.personalwiki.req.DocSaveRequest;
import per.cy.personalwiki.resp.DocQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.utils.CopyUtil;
import per.cy.personalwiki.utils.RedisUtil;
import per.cy.personalwiki.utils.RequestContext;
import per.cy.personalwiki.utils.SnowFlake;
import per.cy.personalwiki.websocket.WebSocketServer;

import java.util.List;

@Service
public class DocService {
    public static Logger logger = LoggerFactory.getLogger(DocService.class);
    @Autowired
    DocMapper docMapper;
    @Autowired
    ContentMapper contentMapper;

    @Autowired
    SnowFlake snowFlake;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    WebSocketService webSocketService;

    public PageResp<DocQueryResp> selectByExample(DocQueryRequest docQueryRequest) {
        DocExample example = new DocExample();
        DocExample.Criteria criteria = example.createCriteria();
        PageHelper.startPage(docQueryRequest.getPage(), docQueryRequest.getSize());
        List<Doc> docs = docMapper.selectByExample(example);
        PageInfo<Doc> pageInfo = new PageInfo<>(docs);
        logger.info("总行数：{}", pageInfo.getTotal());
        logger.info("总页数：{}", pageInfo.getPages());
        List<DocQueryResp> resList = CopyUtil.copyList(docs, DocQueryResp.class);
        PageResp<DocQueryResp> resp = new PageResp<>();
        resp.setList(resList);
        resp.setTotalsPages((int) pageInfo.getTotal());
        return resp;
    }

    public List<DocQueryResp> selectAll(long ebookId) {
        DocExample example = new DocExample();
        example.createCriteria().andEbookIdEqualTo(ebookId);
        example.setOrderByClause("sort asc");
        List<Doc> docs = docMapper.selectByExample(example);
        List<DocQueryResp> resList = CopyUtil.copyList(docs, DocQueryResp.class);
        return resList;
    }

    @Transactional
    public void saveDoc(DocSaveRequest docSaveRequest) {
        Doc doc = CopyUtil.copyInstance(docSaveRequest, Doc.class);
        Content content = CopyUtil.copyInstance(docSaveRequest, Content.class);
        if (ObjectUtils.isEmpty(doc.getId())) {
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void deleteDoc(long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void deleteDoc(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String selectContent(long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        docMapper.increaseViewCount(id);
        return content.getContent();
    }

    public void vote(long id) {
        String remoteIp= RequestContext.getRemoteIp();
        String voteToken="Vote_"+remoteIp+"_"+id;
        if(redisUtil.validateRepeat(voteToken,3600*24)){
            docMapper.increaseVoteCount(id);
            Doc docDb=docMapper.selectByPrimaryKey(id);
            String logId=MDC.get("LOG_ID");
            webSocketService.sendInfo("文档【" + docDb.getName() + "】被点赞！",logId);
        }else {
            throw new BusinessException(BusinessExceptionCode.USER_VOTE_REPEAT);
        }
    }
}
