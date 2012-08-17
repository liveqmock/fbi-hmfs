package dep.hmfs.online.processor.cbs.domain.base;

import java.io.Serializable;

/*
A2��ˮ��	��16λ��	ҵ����ˮ�ţ���־ÿ�ʾ��彻�ס�������ˮ��Ψһ��������ˮ�ţ�
A3������	��4λ��	���������ϵͳ���������ҵ�������0000��ʾ�ɹ��������������ʾʧ�ܣ�����������Ӧ����
A4���ױ��	��4λ��	�����������
A5������	��9λ��	����������
A6��Ա���	��12λ��	���м��˹�Ա���
 */
public class TIAHeader implements Serializable {
    public String serialNo;
    public String errorCode;
    public String txnCode;
    public String deptCode = "hmfs";
    public String operCode = "hmfs";

    public void initFields(byte[] bytes) {
        if (bytes == null || bytes.length < 45) {
            throw new RuntimeException("����ͷ�ֽڳ��ȴ���");
        }
        serialNo = new String(bytes, 0, 16);
        errorCode = new String(bytes, 16, 4);
        txnCode = new String(bytes, 20, 4);
        deptCode = new String(bytes, 24, 9);
        operCode = new String(bytes, 33, 12);
    }
}
