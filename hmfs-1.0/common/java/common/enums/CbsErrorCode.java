package common.enums;

import java.util.Hashtable;

/**
 CBS�м�
 */
public enum CbsErrorCode implements EnumApp {

    QRY_NO_RECORDS("1001", "û�в�ѯ�����ݼ�¼"),
    TXN_NOT_EXIST("1002", "�ý��ײ�����"),
    TXN_NO_EQUAL("1003", "ʵ�ʽ��׽���Ӧ���׽�һ��"),
    TXN_CANCELED("1004", "�ñʽ����ѳ���"),
    TXN_NOT_KNOWN("1005", "�ñʽ��״���״̬����"),
    TXN_HANDLING("1006", "�ý����ѽ��봦�����̣��޷�����"),

    CBS_ACT_BAL_LESS("2001", "����˻�����"),
    CBS_ACT_NOT_EXIST("2002", "���˻�������"),
    CBS_ACT_BAL_ERROR("2003", "�˻���һ��"),
    CBS_ACT_TXNS_ERROR("2004", "�˻�������ϸ��һ��"),
    CBS_ACT_CHK_DATE_ERROR("2005", "�������ڴ���"),

    FUND_ACT_BAL_LESS("3001", "�����˻�����"),
    FUND_ACT_NOT_EXIST("3002", "�ú��㻧������"),
    FUND_ACT_CHK_ERROR("3003", "�������˻��������쳣"),

    VOUCHER_NUM_ERROR("4001", "Ʊ�ݺ�����������"),
    VOUCHER_SEND_ERROR("4002", "Ʊ�ݺ��뷢��ʧ��"),

    SYS_NOT_SIGN_ON("5001", "ϵͳδǩ��"),
    SYS_NOT_SIGN_OUT("5002", "ϵͳδǩ�˻򲻴��ڶ������״̬"),

    MSG_IN_SN_NOT_EXIST("6001", "���뵥�Ų�����"),
    NET_COMMUNICATE_ERROR("7000", "���������쳣"),
    NET_COMMUNICATE_TIMEOUT("7001", "�������ӳ�ʱ"),
    DATA_ANALYSIS_ERROR("8000", "���Ľ�������"),

    SYSTEM_ERROR("9000","ϵͳ�쳣,����ϵ����Ա");

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
