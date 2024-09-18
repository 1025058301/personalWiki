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
import java.util.Arrays;
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
    @DeleteMapping ("/delete/{idStr}")
    public CommonResp deleteDoc(@PathVariable("idStr")String idStr) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        List<String> list= Arrays.asList(idStr.split(","));
        docService.deleteDoc(list);
        return commonResp;
    }

    @RequestMapping("/getContent/{id}")
    public CommonResp<String> getContent(@PathVariable("id")long id) {
        CommonResp<String> commonResp=new CommonResp<>();
        commonResp.setContent(docService.selectContent(id));
        commonResp.setSuccess(true);
        return commonResp;
    }
}
