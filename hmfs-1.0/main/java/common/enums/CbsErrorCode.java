package common.enums;

import java.util.Hashtable;

/**
 CBS中间
 */
public enum CbsErrorCode implements EnumApp {

    DATA_ANALYSIS_ERROR("1000", "报文解析错误"),
    NET_COMMUNICATE_ERROR("2000", "网络连接异常"),
    NET_COMMUNICATE_TIMEOUT("3000", "网络连接超时"),
    SYSTEM_ERROR("9000","系统异常");

    private String code = null;
    private String title = null;
    private static Hashtable<String, CbsErrorCode> aliasEnums;

    CbsErrorCode(String code, String title) {
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

    public static CbsErrorCode valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
