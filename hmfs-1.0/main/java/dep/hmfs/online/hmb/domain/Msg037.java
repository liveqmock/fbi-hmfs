package dep.hmfs.online.hmb.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
public class Msg037 extends SubMsg{
    //F8����������
    public String actionCode;

    //F16����ϢID1
    public String infoId1;

    //F17����ϢID1����
    public String infoIdType1;

    //F21����Ϣ����
    public String infoName;

    //F22����Ϣ��ַ
    public String infoAddr;

    //F24���������
    public String builderArea;

    //F28�����㻧�˺�1��
    public String fundActno1;

    //F29�����㻧�˺�1����
    public String fundActtype1;

    //F45�����׽��1
    public BigDecimal txnAmt1;

    //F59����λID
    public String orgId;

    //F60����λ����  10���о֣�11�����֣�12�������̣�13��ҵί�᣻14����ҵ��˾��15����۵�λ��16������λ
    public String orgType;

    //F61����λ����
    public String orgName;

    //F77���վݱ��
    public String receiptNo;

    //F78�������׼1
    public String depStandard1;

    //F79���ɿ����� 00:�״ν��� 01:ά���ʽ�׷�� 02:ά���ʽ����� 03:��Ϣ���� 04:Ƿ��� 05:�������� 06:�����ʽ𽻿�
    public String depType;

    //F80���ɴ���
    public String depPerson;

    //F81��������
    public String houseCardNo;

    //F82��������ͬ��
    public String houseContNo;

    //F86���ʽ��շ�����˺�
    public String payinActno;

    //F90��ƾ֤����   00-��Ʒסլ;
    public String voucherType;

    //F91���������ı��
    public String linkMsgSn;
}

