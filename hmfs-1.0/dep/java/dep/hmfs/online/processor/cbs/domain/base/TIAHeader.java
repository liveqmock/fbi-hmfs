package dep.hmfs.online.processor.cbs.domain.base;

import java.io.Serializable;

/*
A2��ˮ��	��16λ��	ҵ����ˮ�ţ���־ÿ�ʾ��彻�ס�������ˮ��Ψһ��������ˮ�ţ�
A3������	��4λ��	���������ϵͳ���������ҵ�������0000��ʾ�ɹ��������������ʾʧ�ܣ�����������Ӧ����
A4���ױ��	��4λ��	�����������
 */
public class TIAHeader implements Serializable {
    public String serialNo;
    public String errorCode;
    public String txnCode;

    public void initFields(byte[] bytes) {
        if(bytes == null || bytes.length < 24) {
            throw new RuntimeException("����ͷ�ֽڳ��Ȳ�����");
        }
        serialNo = new String(bytes, 0, 16);
        errorCode = new String(bytes, 16, 4);
        txnCode = new String(bytes, 20, 4);
    }
}
