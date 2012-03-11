package common.enums;

import java.util.Hashtable;

/**
 记账方向      D：缴费    C：支取或退款
 */
public enum DCFlagCode implements EnumApp {

    TXN_OUT("C", "支取或退款"),
    TXN_IN("D", "缴费");

    private String code = null;
    private String title = null;
    private static Hashtable<String, DCFlagCode> aliasEnums;

    DCFlagCode(String code, String title) {
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

    public static DCFlagCode valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
