package per.cy.personalwiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import per.cy.personalwiki.exception.BusinessException;
import per.cy.personalwiki.exception.BusinessExceptionCode;
import per.cy.personalwiki.mapper.UserMapper;
import per.cy.personalwiki.pojo.User;
import per.cy.personalwiki.pojo.UserExample;
import per.cy.personalwiki.req.UserLoginRequest;
import per.cy.personalwiki.req.UserQueryRequest;
import per.cy.personalwiki.req.UserResetPasswordRequest;
import per.cy.personalwiki.req.UserSaveRequest;
import per.cy.personalwiki.resp.UserLoginResp;
import per.cy.personalwiki.resp.UserQueryResp;
import per.cy.personalwiki.resp.PageResp;
import per.cy.personalwiki.utils.CopyUtil;
import per.cy.personalwiki.utils.SnowFlake;

import java.util.List;

@Service
public class UserService {
    public static Logger logger= LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserMapper userMapper;

    @Autowired
    SnowFlake snowFlake;
    public PageResp<UserQueryResp> selectByExample(UserQueryRequest userQueryRequest) {
        PageHelper.startPage(userQueryRequest.getPage(), userQueryRequest.getSize());
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        if(!ObjectUtils.isEmpty(userQueryRequest.getLoginName())){
            criteria.andLoginNameLike("%" + userQueryRequest.getLoginName() + "%");//%是通配符，可以匹配零个或多个任意字符
        }
        List<User> users = userMapper.selectByExample(example);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        logger.info("总行数：{}", pageInfo.getTotal());
        logger.info("总页数：{}", pageInfo.getPages());
        List<UserQueryResp> resList=CopyUtil.copyList(users, UserQueryResp.class);
        PageResp<UserQueryResp> resp=new PageResp<>();
        resp.setList(resList);
        resp.setTotalsPages((int)pageInfo.getTotal());
        return resp;
    }
    public void saveUser(UserSaveRequest userSaveRequest){
        User user=CopyUtil.copyInstance(userSaveRequest,User.class);
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        logger.info("test对应的密码:{}",user.getPassword());
        if(ObjectUtils.isEmpty(user.getId())){
            if(ObjectUtils.isEmpty(getUserByLoginName(user.getLoginName()))){
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else {
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }else {
            user.setLoginName(null);//修改时不能修改用户名
            user.setPassword(null);//修改时不修改密码，单独接口修改密码
            userMapper.updateByPrimaryKeySelective(user);
        }
    }
    public void deleteUser(long id){
        userMapper.deleteByPrimaryKey(id);
    }
    public User getUserByLoginName(String loginName){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> users = userMapper.selectByExample(example);
        if(users.isEmpty()){
            return null;
        }else {
            return users.get(0);
        }
    }
    public void resetPassword(UserResetPasswordRequest req) {
        User user = CopyUtil.copyInstance(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public UserLoginResp login(UserLoginRequest request){
        User user=getUserByLoginName(request.getLoginName());
        if(ObjectUtils.isEmpty(user)){
            throw new BusinessException(BusinessExceptionCode.USER_LOGIN_FAIL);
        }else {
            if(user.getPassword().equals(request.getPassword())){
                return CopyUtil.copyInstance(user, UserLoginResp.class);
            }else {
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_FAIL);
            }
        }
    }
}
