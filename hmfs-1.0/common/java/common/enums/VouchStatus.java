package common.enums;

import java.util.Hashtable;

/**
 * 票据状态
 */
public enum VouchStatus implements EnumApp {

    VOUCH_RECEIVED("1", "领用"),
    VOUCH_USED("2", "使用"),
    VOUCH_CANCEL("3", "撤销");

    private String code = null;
    private String title = null;
    private static Hashtable<String, VouchStatus> aliasEnums;

    VouchStatus(String code, String title) {
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

    public static VouchStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
