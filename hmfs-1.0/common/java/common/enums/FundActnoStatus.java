package common.enums;

import java.util.Hashtable;

/**
 * ºËËãÕÊ»§×´Ì¬
 */
public enum FundActnoStatus implements EnumApp {

    NORMAL("0", "Õý³£"),
    FORBID("1", "·âÕÊ"),
    CANCEL("2", "×¢Ïú");

    private String code = null;
    private String title = null;
    private static Hashtable<String, FundActnoStatus> aliasEnums;

    FundActnoStatus(String code, String title) {
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

    public static FundActnoStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
