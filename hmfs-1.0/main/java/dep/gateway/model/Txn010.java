package dep.gateway.model;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
public class Txn010 extends SummaryToaMsg {
    //F16����ϢID1
    public String infoId1;

    //F17����ϢID1����
    public String infoIdType1;

    //F18����ϢID2
    public String infoId2;

    //F19����ϢID2����
    public String infoIdType2;

    //F25����������ID
    public String districtId;

    //F28�����㻧�˺�1��
    public String fundActno1;

    //F29�����㻧�˺�1����
    public String fundActtype1;

    //F95�������ʽ��ʺ�
    public String payinCbsActno;

    //F96�������ʽ��ʺ�����
    public String payinCbsActtype;

    //F45�����׽��1
    public BigDecimal txnAmt1;
}
