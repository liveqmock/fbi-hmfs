package common.enums;

import java.util.Hashtable;

/**
 TXN_CTL_STS
 */
public enum TxnCtlSts implements EnumApp {

    TXN_INIT("00", "初始接收"),
    TXN_CANCEL("01", "已撤销"),
    TXN_HANDLING("10", "交易处理中"),
    TXN_SUCCESS("20", "交易成功");

    private String code = null;
    private String title = null;
    private static Hashtable<String, TxnCtlSts> aliasEnums;

    TxnCtlSts(String code, String title) {
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

    public static TxnCtlSts valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
