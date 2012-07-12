package common.enums;

import java.util.Hashtable;

/**
 CBS中间
 */
public enum CbsErrorCode implements EnumApp {

    QRY_NO_RECORDS("1001", "没有查询到数据记录"),
    TXN_NOT_EXIST("1002", "该交易不存在"),
    TXN_NO_EQUAL("1003", "实际交易金额和应交易金额不一致"),
    TXN_CANCELED("1004", "该笔交易已撤销"),
    TXN_NOT_KNOWN("1005", "该笔交易处理状态不明"),
    TXN_HANDLING("1006", "该交易已进入处理流程，无法撤销"),

    CBS_ACT_BAL_LESS("2001", "会计账户余额不足"),
    CBS_ACT_NOT_EXIST("2002", "该账户不存在"),
    CBS_ACT_BAL_ERROR("2003", "账户余额不一致"),
    CBS_ACT_TXNS_ERROR("2004", "账户交易明细不一致"),
    CBS_ACT_CHK_DATE_ERROR("2005", "对账日期错误"),

    FUND_ACT_BAL_LESS("3001", "核算账户余额不足"),
    FUND_ACT_NOT_EXIST("3002", "该核算户不存在"),
    FUND_ACT_CHK_ERROR("3003", "国土局账户余额对账异常"),

    VOUCHER_NUM_ERROR("4001", "票据号码输入有误"),
    VOUCHER_SEND_ERROR("4002", "票据号码发送失败"),

    SYS_NOT_SIGN_ON("5001", "系统未签到"),
    SYS_NOT_SIGN_OUT("5002", "系统未签退或不处于对帐完成状态"),

    MSG_IN_SN_NOT_EXIST("6001", "申请单号不存在"),
    NET_COMMUNICATE_ERROR("7000", "网络连接异常"),
    NET_COMMUNICATE_TIMEOUT("7001", "网络连接超时"),
    DATA_ANALYSIS_ERROR("8000", "报文解析错误"),

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
