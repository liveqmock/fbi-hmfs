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
    public BigDecimal builderArea;

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

    public String getInfoId1() {
        return infoId1;
    }

    public void setInfoId1(String infoId1) {
        this.infoId1 = infoId1;
    }

    public String getInfoIdType1() {
        return infoIdType1;
    }

    public void setInfoIdType1(String infoIdType1) {
        this.infoIdType1 = infoIdType1;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoAddr() {
        return infoAddr;
    }

    public void setInfoAddr(String infoAddr) {
        this.infoAddr = infoAddr;
    }

    public BigDecimal getBuilderArea() {
        return builderArea;
    }

    public void setBuilderArea(BigDecimal builderArea) {
        this.builderArea = builderArea;
    }

    public String getFundActno1() {
        return fundActno1;
    }

    public void setFundActno1(String fundActno1) {
        this.fundActno1 = fundActno1;
    }

    public String getFundActtype1() {
        return fundActtype1;
    }

    public void setFundActtype1(String fundActtype1) {
        this.fundActtype1 = fundActtype1;
    }

    public BigDecimal getTxnAmt1() {
        return txnAmt1;
    }

    public void setTxnAmt1(BigDecimal txnAmt1) {
        this.txnAmt1 = txnAmt1;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getDepStandard1() {
        return depStandard1;
    }

    public void setDepStandard1(String depStandard1) {
        this.depStandard1 = depStandard1;
    }

    public String getDepType() {
        return depType;
    }

    public void setDepType(String depType) {
        this.depType = depType;
    }

    public String getDepPerson() {
        return depPerson;
    }

    public void setDepPerson(String depPerson) {
        this.depPerson = depPerson;
    }

    public String getHouseCardNo() {
        return houseCardNo;
    }

    public void setHouseCardNo(String houseCardNo) {
        this.houseCardNo = houseCardNo;
    }

    public String getHouseContNo() {
        return houseContNo;
    }

    public void setHouseContNo(String houseContNo) {
        this.houseContNo = houseContNo;
    }

    public String getPayinActno() {
        return payinActno;
    }

    public void setPayinActno(String payinActno) {
        this.payinActno = payinActno;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getLinkMsgSn() {
        return linkMsgSn;
    }

    public void setLinkMsgSn(String linkMsgSn) {
        this.linkMsgSn = linkMsgSn;
    }
}

