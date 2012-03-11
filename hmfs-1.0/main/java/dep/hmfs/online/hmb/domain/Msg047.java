package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("047")
public class Msg047 extends SubMsg{
    //F8����������
    @Hmb8583Field(8)
    public String actionCode;

    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F21����Ϣ����
    @Hmb8583Field(21)
    public String infoName;

    //F22����Ϣ��ַ
    @Hmb8583Field(22)
    public String infoAddr;

    //F24���������
    @Hmb8583Field(24)
    public String builderArea;

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F45�����׽��1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F59����λID
    @Hmb8583Field(59)
    public String orgId;

    //F60����λ����  10���о֣�11�����֣�12�������̣�13��ҵί�᣻14����ҵ��˾��15����۵�λ��16������λ
    @Hmb8583Field(60)
    public String orgType;

    //F61����λ����
    @Hmb8583Field(61)
    public String orgName;

    //F77���վݱ��
    @Hmb8583Field(77)
    public String receiptNo;

    //F78�������׼1
    @Hmb8583Field(78)
    public String depStandard1;

    //F79���ɿ����� 00:�״ν��� 01:ά���ʽ�׷�� 02:ά���ʽ����� 03:��Ϣ���� 04:Ƿ��� 05:�������� 06:�����ʽ𽻿�
    @Hmb8583Field(79)
    public String depType;

    //F80���ɴ���
    @Hmb8583Field(80)
    public String depPerson;

    //F81��������
    @Hmb8583Field(81)
    public String houseCardNo;

    //F82��������ͬ��
    @Hmb8583Field(82)
    public String houseContNo;

    //F86���ʽ��շ�����˺�
    @Hmb8583Field(86)
    public String payinActno;

    //F90��ƾ֤����   00-��Ʒסլ;
    @Hmb8583Field(90)
    public String voucherType;

    //F91���������ı��
    @Hmb8583Field(91)
    public String linkMsgSn;
}

