package common.enums;

import java.util.Hashtable;

/**
 ���˷���      D���ɷ�    C��֧ȡ���˿�
 */
public enum DCFlagCode implements EnumApp {

    TXN_OUT("C", "֧ȡ���˿�"),
    TXN_IN("D", "�ɷ�");

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
