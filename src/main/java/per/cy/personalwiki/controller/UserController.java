package per.cy.personalwiki.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import per.cy.personalwiki.req.UserLoginRequest;
import per.cy.personalwiki.req.UserQueryRequest;
import per.cy.personalwiki.req.UserResetPasswordRequest;
import per.cy.personalwiki.req.UserSaveRequest;
import per.cy.personalwiki.resp.CommonResp;
import per.cy.personalwiki.resp.UserLoginResp;
import per.cy.personalwiki.resp.UserQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.service.UserService;
import per.cy.personalwiki.utils.SnowFlake;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    public static Logger logger= LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SnowFlake snowFlake;

    @RequestMapping("/list")
    public CommonResp<PageResp<UserQueryResp>> getUserList(@Valid UserQueryRequest userQueryRequest) {
        CommonResp<PageResp<UserQueryResp>> commonResp=new CommonResp<>();
        commonResp.setContent(userService.selectByExample(userQueryRequest));
        commonResp.setSuccess(true);
        return commonResp;
    }
    @RequestMapping("/save")
    public CommonResp saveUser(@Valid@RequestBody UserSaveRequest userSaveRequest) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        userService.saveUser(userSaveRequest);
        return commonResp;
    }
    @DeleteMapping ("/delete/{id}")
    public CommonResp deleteUser(@PathVariable("id")long id) {//RequestBody用于接收json类型的参数
        CommonResp commonResp=new CommonResp<>();
        userService.deleteUser(id);
        return commonResp;
    }

    @PostMapping("/resetPassword")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordRequest req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp<UserLoginResp> login(@Valid @RequestBody UserLoginRequest req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp loginResp=userService.login(req);

        Long token=snowFlake.nextId();
        logger.info("生成单点登录token：{}，并放入redis中", token);
        loginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(loginResp), 3600 * 24, TimeUnit.SECONDS);
        resp.setContent(loginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        redisTemplate.delete(token);
        logger.info("从redis中删除token: {}", token);
        return resp;
    }
}
