package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cy.personalwiki.pojo.Demo;
import per.cy.personalwiki.service.DemoService;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    DemoService demoService;

    @RequestMapping("/test1")
    public List<Demo> getDemoList(){
        return demoService.selectByExample(null);
    }
}
