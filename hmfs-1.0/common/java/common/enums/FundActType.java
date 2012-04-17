package common.enums;

import java.util.Hashtable;

/**
 * �����ʻ�����
 */
public enum FundActType implements EnumApp {

    PROJECT("511", "��Ŀ�ʻ�"),
    INDIVID("570", "�ֻ��ʻ�");

    private String code = null;
    private String title = null;
    private static Hashtable<String, FundActType> aliasEnums;

    FundActType(String code, String title) {
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

    public static FundActType valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
