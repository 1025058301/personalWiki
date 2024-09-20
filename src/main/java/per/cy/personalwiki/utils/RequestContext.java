package per.cy.personalwiki.utils;

public class RequestContext {
    private static final ThreadLocal<String> remoteIp = new ThreadLocal<>();

    public static void setRemoteIp(String remoteIp) {
        RequestContext.remoteIp.set(remoteIp);
    }

    public static String getRemoteIp() {
        return remoteIp.get();
    }

}
