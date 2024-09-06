package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cy.personalwiki.mapper.TestMapper;
import per.cy.personalwiki.pojo.Test;
import per.cy.personalwiki.service.TestService;

import java.util.List;

@RestController//方法的返回值直接作为响应体，而不是视图名称
public class TestController {
    @Autowired
    TestService testService;
    @Value("${my_config.name}")
    private  String name;
    @RequestMapping("/hello")//支持所有http请求类型
    public  String hello(){
        return "hello world"+" "+ name;
    }
    @RequestMapping("/test")//支持所有http请求类型
    public List<Test> test(){
        return testService.getList();
    }
}
