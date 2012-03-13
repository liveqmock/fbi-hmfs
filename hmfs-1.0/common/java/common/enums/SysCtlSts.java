package common.enums;

import java.util.Hashtable;

/**
 系统状态
 */
public enum SysCtlSts implements EnumApp {

    INIT("0", "系统初始化"),
    SIGNON("1", "已签到"),
    SIGNOUT("2", "已签退"),
    HOST_BALCHK_SUCCESS("3", "主机余额对账成功"),
    HOST_DETLCHK_SUCCESS("4", "主机流水对账成功"),
    HOST_CHK_SUCCESS("5", "主机对账成功"),
    HMB_BALCHK_SUCCESS("6", "国土局余额对账成功"),
    HMB_DETLCHK_SUCCESS("7", "国土局流水对账成功"),
    HMB_CHK_SUCCESS("8", "国土局对账成功");

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
