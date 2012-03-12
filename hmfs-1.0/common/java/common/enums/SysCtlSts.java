package common.enums;

import java.util.Hashtable;

/**
 ϵͳ״̬
 */
public enum SysCtlSts implements EnumApp {

    INIT("0", "ϵͳ��ʼ��"),
    SIGNON("1", "��ǩ��"),
    SIGNOUT("2", "��ǩ��"),
    HOST_CHECKED("3", "����������"),
    HMB_CHECKED("4", "�ѹ����ֶ���");

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
