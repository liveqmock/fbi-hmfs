package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("007")
public class Msg007 extends SummaryMsg {
    //F12:���׷�ʽ  1��������2������
    public String txnType;

    //F13:ҵ������  1����λ��2��ҵ����3��������4��Ĩ��
    public String bizType;

    //F14:�������� ���𷽵Ľ���������
    public String origTxnCode;

    //F16����ϢID1
    public String infoId1;

    //F17����ϢID1����
    public String infoIdType1;

    //F25����������ID
    public String districtId;

    //F28�����㻧�˺�1��
    public String fundActno1;

    //F29�����㻧�˺�1����
    public String fundActtype1;

    //F32�����㻧�˺�1
    public String settleActno1;

    //F33�����㻧�˺�1����
    public String settleActtype1;

    //F45�����׽��1
    public BigDecimal txnAmt1;

    //F84���ʽ𸶷�����˺�
    public String payoutActno;

    //F85���������
    public String payoutActName;

    //F86���ʽ��շ�����˺�
    public String payinActno;

    //F87���տ����
    public String payinActName;
}
