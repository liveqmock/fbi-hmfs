package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("009")
public class Msg009 extends SummaryMsg {
    //F12:���׷�ʽ  1��������2������
    @Hmb8583Field(12)
    public String txnType;

    //F13:ҵ������  1����λ��2��ҵ����3��������4��Ĩ��
    @Hmb8583Field(13)
    public String bizType;

    //F14:�������� ���𷽵Ľ���������
    @Hmb8583Field(14)
    public String origTxnCode;

    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F18����ϢID2
    @Hmb8583Field(18)
    public String infoId2;

    //F19����ϢID2����
    @Hmb8583Field(19)
    public String infoIdType2;

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F95�������ʽ��ʺ�
    @Hmb8583Field(95)
    public String payinCbsActno;

    //F96�������ʽ��ʺ�����
    @Hmb8583Field(96)
    public String payinCbsActtype;

    //F45�����׽��1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;
}
