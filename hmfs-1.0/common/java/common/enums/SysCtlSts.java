package common.enums;

import java.util.Hashtable;

/**
 系统状态
 */
public enum SysCtlSts implements EnumApp {

    INIT("0", "系统初始化"),
    SIGNON("1", "已签到"),
    SIGNOUT("2", "已签退"),
    HOST_CHECKED("3", "已主机对账"),
    HMB_CHECKED("4", "已国土局对账");

    private String code = null;
    private String title = null;
    private static Hashtable<String, SysCtlSts> aliasEnums;

    SysCtlSts(String code, String title) {
        this.init(code, title);
    }

    @SuppressWarnings("unchecked")
    private void init(String code, String title) {
        this.code = code;
        this.title = title;
        synchronized (this.getClass()) {
            if (aliasEnums == null) {
                aliasEnums = new Hashtable();
            }
        }
        aliasEnums.put(code, this);
        aliasEnums.put(title, this);
    }

    public static SysCtlSts valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
