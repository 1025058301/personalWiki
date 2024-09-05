package per.cy.personalwiki.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//方法的返回值直接作为响应体，而不是视图名称
public class TestController {
    @RequestMapping("/hello")//支持所有http请求类型
    public static String hello(){
        return "hello world";
    }
}
