package dep.hmfs.online.hmb.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
public class Txn035 extends SubMsg{
    //F8����������
    public String actionCode;

    //F16����ϢID1
    public String infoId1;

    //F17����ϢID1����
    public String infoIdType1;

    //F20����Ϣ����
    public String infoCode;

    //F21����Ϣ����
    public String infoName;

    //F22����Ϣ��ַ
    public String infoAddr;

    //F23���ֻ���
    public String cellNum;

    //F24���������
    public String builderArea;

    //F25����������ID
    public String districtId;

    //F28�����㻧�˺�1��
    public String fundActno1;

    //F29�����㻧�˺�1����
    public String fundActtype1;

    //F30�����㻧�˺�2��
    public String fundActno2;

    //F31�����㻧�˺�2����
    public String fundActtype2;

    //F32�����㻧�˺�1
    public String settleActno1;

    //F33�����㻧�˺�1����
    public String settleActtype1;

    //F45�����׽��1
    public BigDecimal txnAmt1;

    //F71���������赥λ����    ���㻧Ϊ����ʱ��������д#
    public String devOrgName;

    //F87���տ����
    public String payinActName;

}
