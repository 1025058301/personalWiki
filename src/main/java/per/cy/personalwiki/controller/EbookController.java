package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cy.personalwiki.resp.CommonResp;
import per.cy.personalwiki.resp.EbookResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.service.EbookService;
import per.cy.personalwiki.req.EbookRequest;

import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Autowired
    EbookService ebookService;

    @RequestMapping("/list")
    public CommonResp<PageResp<EbookResp>> getEookList(EbookRequest ebookRequest) {
        CommonResp<PageResp<EbookResp>> commonResp=new CommonResp<>();
        commonResp.setContent(ebookService.selectByExample(ebookRequest));
        commonResp.setSuccess(true);
        return commonResp;
    }
}
