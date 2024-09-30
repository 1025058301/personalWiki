package per.cy.personalwiki.controller;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
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
    private String name;
    @Autowired
    RedissonClient redissonClient;

    public static Logger logger= LoggerFactory.getLogger(TestController.class);


    @RequestMapping("/hello")//支持所有http请求类型
    public String hello() {
        return "hello world" + " " + name;
    }

    @RequestMapping("/test")//支持所有http请求类型
    public List<Test> test() {
        return testService.getList();
    }

    @RequestMapping("/test/testBloom/{loginName}")//支持所有http请求类型
    public Boolean put(@PathVariable("loginName") String loginName) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("test");
        bloomFilter.tryInit(10000,0.01);
        if(bloomFilter.contains(loginName)){
            logger.info("布隆过滤器中有 {} 元素",loginName);
        }else {
            logger.info("布隆过滤器现在添加 {} 元素",loginName);
            bloomFilter.add(loginName);
        }
        if(bloomFilter.contains(loginName)){
            logger.info("现在布隆过滤器中真有 {} 元素了",loginName);
        }
        return true;
    }
}
