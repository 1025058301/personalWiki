package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cy.personalwiki.req.EbookSaveRequest;
import per.cy.personalwiki.resp.CommonResp;
import per.cy.personalwiki.resp.EbookQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.service.EbookService;
import per.cy.personalwiki.req.EbookQueryRequest;

@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Autowired
    EbookService ebookService;

    @RequestMapping("/list")
    public CommonResp<PageResp<EbookQueryResp>> getEookList(EbookQueryRequest ebookQueryRequest) {
        CommonResp<PageResp<EbookQueryResp>> commonResp=new CommonResp<>();
        commonResp.setContent(ebookService.selectByExample(ebookQueryRequest));
        commonResp.setSuccess(true);
        return commonResp;
    }
    @RequestMapping("/save")
    public CommonResp saveEbook(@RequestBody EbookSaveRequest ebookSaveRequest) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        ebookService.saveEbook(ebookSaveRequest);
        return commonResp;
    }
}
