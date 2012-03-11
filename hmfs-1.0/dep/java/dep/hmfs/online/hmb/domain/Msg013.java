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
@HmbMessage("013")
public class Msg013 extends SummaryMsg {
    //F9�����½�����ص�ԭʼ���׵ı��ı�ţ���Ĩ�ˡ������ཻ�ױ�Ĩ�ˡ��������׵ı��ı��
    @Hmb8583Field(9)
    public String origMsgSn;

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

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F32�����㻧�˺�1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33�����㻧�˺�1����
    @Hmb8583Field(33)
    public String settleActtype1;

    //F45�����׽��1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F79���ɿ����� 00:�״ν��� 01:ά���ʽ�׷�� 02:ά���ʽ����� 03:��Ϣ���� 04:Ƿ��� 05:�������� 06:�����ʽ𽻿�
    @Hmb8583Field(79)
    public String depType;
}
