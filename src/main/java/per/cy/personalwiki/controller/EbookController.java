package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cy.personalwiki.Util.CommonResp;
import per.cy.personalwiki.pojo.Demo;
import per.cy.personalwiki.pojo.Ebook;
import per.cy.personalwiki.service.DemoService;
import per.cy.personalwiki.service.EbookService;

import java.util.List;

@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Autowired
    EbookService ebookService;

    @RequestMapping("/list")
    public CommonResp<List<Ebook>> getDemoList() {
        CommonResp<List<Ebook>> commonResp=new CommonResp<>();
        commonResp.setContent(ebookService.selectByExample(null));
        commonResp.setSuccess(true);
        return commonResp;
    }
}
