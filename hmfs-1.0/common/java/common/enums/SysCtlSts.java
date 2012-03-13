package common.enums;

import java.util.Hashtable;

/**
 ϵͳ״̬
 */
public enum SysCtlSts implements EnumApp {

    INIT("0", "ϵͳ��ʼ��"),
    SIGNON("1", "��ǩ��"),
    SIGNOUT("2", "��ǩ��"),
    HOST_BALCHK_SUCCESS("3", "���������˳ɹ�"),
    HOST_DETLCHK_SUCCESS("4", "������ˮ���˳ɹ�"),
    HOST_CHK_SUCCESS("5", "�������˳ɹ�"),
    HMB_BALCHK_SUCCESS("6", "�����������˳ɹ�"),
    HMB_DETLCHK_SUCCESS("7", "��������ˮ���˳ɹ�"),
    HMB_CHK_SUCCESS("8", "�����ֶ��˳ɹ�");

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
