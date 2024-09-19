package per.cy.personalwiki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import per.cy.personalwiki.req.UserQueryRequest;
import per.cy.personalwiki.req.UserResetPasswordRequest;
import per.cy.personalwiki.req.UserSaveRequest;
import per.cy.personalwiki.resp.CommonResp;
import per.cy.personalwiki.resp.UserQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

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
}
