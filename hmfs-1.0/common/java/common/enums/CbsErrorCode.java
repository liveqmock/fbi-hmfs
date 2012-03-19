package common.enums;

import java.util.Hashtable;

/**
 CBS中间
 */
public enum CbsErrorCode implements EnumApp {

    DATA_ANALYSIS_ERROR("8000", "报文解析错误"),

    QRY_NO_RECORDS("1001", "没有查询到数据记录"),
    TXN_NO_EXIST("1002", "该交易不存在"),
    TXN_NO_EQUAL("1003", "实际交易金额和应交易金额不一致"),
    TXN_CANCELED("1004", "该笔交易已撤销"),
    TXN_NOT_KNOWN("1005", "该笔交易处理状态不明"),
    TXN_HANDLING("1006", "该交易已进入处理流程，无法撤销"),

    ACCOUNT_BAL_LESS("2001", "会计账户余额不足"),

    FUND_BAL_LESS("3001", "核算账户余额不足"),

    NET_COMMUNICATE_ERROR("7000", "网络连接异常"),
    NET_COMMUNICATE_TIMEOUT("7001", "网络连接超时"),
    SYSTEM_ERROR("9000","系统异常,请联系管理员");

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
