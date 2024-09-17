package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.cy.personalwiki.req.CategorySaveRequest;
import per.cy.personalwiki.resp.CommonResp;
import per.cy.personalwiki.resp.CategoryQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.service.CategoryService;
import per.cy.personalwiki.req.CategoryQueryRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping("/list")
    public CommonResp<PageResp<CategoryQueryResp>> getEookList(@Valid CategoryQueryRequest categoryQueryRequest) {
        CommonResp<PageResp<CategoryQueryResp>> commonResp=new CommonResp<>();
        commonResp.setContent(categoryService.selectByExample(categoryQueryRequest));
        commonResp.setSuccess(true);
        return commonResp;
    }

    @RequestMapping("/all")
    public CommonResp<List<CategoryQueryResp>> getEookList() {
        CommonResp<List<CategoryQueryResp>> commonResp=new CommonResp<>();
        commonResp.setContent(categoryService.selectAll());
        commonResp.setSuccess(true);
        return commonResp;
    }
    @RequestMapping("/save")
    public CommonResp saveCategory(@Valid@RequestBody CategorySaveRequest categorySaveRequest) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        categoryService.saveCategory(categorySaveRequest);
        return commonResp;
    }
    @DeleteMapping ("/delete/{id}")
    public CommonResp deleteCategory(@PathVariable("id")long id) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        categoryService.deleteCategory(id);
        return commonResp;
    }
}
