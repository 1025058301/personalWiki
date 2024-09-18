package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.cy.personalwiki.req.DocQueryRequest;
import per.cy.personalwiki.req.DocSaveRequest;
import per.cy.personalwiki.resp.DocQueryResp;
import per.cy.personalwiki.resp.CommonResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.service.DocService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {
    @Autowired
    DocService docService;

    @RequestMapping("/list")
    public CommonResp<PageResp<DocQueryResp>> getEookList(@Valid DocQueryRequest docQueryRequest) {
        CommonResp<PageResp<DocQueryResp>> commonResp=new CommonResp<>();
        commonResp.setContent(docService.selectByExample(docQueryRequest));
        commonResp.setSuccess(true);
        return commonResp;
    }

    @RequestMapping("/all")
    public CommonResp<List<DocQueryResp>> getEookList() {
        CommonResp<List<DocQueryResp>> commonResp=new CommonResp<>();
        commonResp.setContent(docService.selectAll());
        commonResp.setSuccess(true);
        return commonResp;
    }
    @RequestMapping("/save")
    public CommonResp saveDoc(@Valid@RequestBody DocSaveRequest docSaveRequest) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        docService.saveDoc(docSaveRequest);
        return commonResp;
    }
    @DeleteMapping ("/delete/{id}")
    public CommonResp deleteDoc(@PathVariable("id")long id) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        docService.deleteDoc(id);
        return commonResp;
    }
}
