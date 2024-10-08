package per.cy.personalwiki.exception;

public enum BusinessExceptionCode {

    USER_LOGIN_NAME_EXIST("登录名已存在"),
    USER_LOGIN_FAIL("用户名不存在或密码错误"),
    USER_VOTE_REPEAT("不能重复点赞")
    ;
    private String desc;

    BusinessExceptionCode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
