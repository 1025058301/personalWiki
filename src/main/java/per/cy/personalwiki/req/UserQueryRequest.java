package per.cy.personalwiki.req;

public class UserQueryRequest extends PageRequest {

    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "UserQueryRequest{" +
                "loginName='" + loginName + '\'' +
                "} " + super.toString();
    }
}
