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
@HmbMessage("039")
public class Msg039 extends SubMsg{
    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F20����Ϣ����
    @Hmb8583Field(20)
    public String infoCode;

    //F21����Ϣ����
    @Hmb8583Field(21)
    public String infoName;

    //F22����Ϣ��ַ
    @Hmb8583Field(22)
    public String infoAddr;

    //F24���������
    @Hmb8583Field(24)
    public BigDecimal builderArea;

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F30�����㻧�˺�2��
    @Hmb8583Field(30)
    public String fundActno2;

    //F31�����㻧�˺�2����
    @Hmb8583Field(31)
    public String fundActtype2;

    //F32�����㻧�˺�1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33�����㻧�˺�1����
    @Hmb8583Field(33)
    public String settleActtype1;

    //F37���˻����
    @Hmb8583Field(37)
    public BigDecimal actBal;

    //F45�����׽��1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F49����Ϣ���1
    @Hmb8583Field(49)
    public BigDecimal intrAmt1;

    //F42���������з�֧�������
    @Hmb8583Field(42)
    public  String branchId;

    //F94�������ʽ��ʺ�����
    @Hmb8583Field(94)
    public String payinCbsActname;

    //F95�������ʽ��ʺ�
    @Hmb8583Field(95)
    public String payinCbsActno;

    //F104��֤������
    @Hmb8583Field(104)
    public String certType;

    //F105��֤�����
    @Hmb8583Field(105)
    public String certId;

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

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
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

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
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

    public String getFundActno2() {
        return fundActno2;
    }

    public void setFundActno2(String fundActno2) {
        this.fundActno2 = fundActno2;
    }

    public String getFundActtype2() {
        return fundActtype2;
    }

    public void setFundActtype2(String fundActtype2) {
        this.fundActtype2 = fundActtype2;
    }

    public String getSettleActno1() {
        return settleActno1;
    }

    public void setSettleActno1(String settleActno1) {
        this.settleActno1 = settleActno1;
    }

    public String getSettleActtype1() {
        return settleActtype1;
    }

    public void setSettleActtype1(String settleActtype1) {
        this.settleActtype1 = settleActtype1;
    }

    public BigDecimal getActBal() {
        return actBal;
    }

    public void setActBal(BigDecimal actBal) {
        this.actBal = actBal;
    }

    public BigDecimal getTxnAmt1() {
        return txnAmt1;
    }

    public void setTxnAmt1(BigDecimal txnAmt1) {
        this.txnAmt1 = txnAmt1;
    }

    public BigDecimal getIntrAmt1() {
        return intrAmt1;
    }

    public void setIntrAmt1(BigDecimal intrAmt1) {
        this.intrAmt1 = intrAmt1;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getPayinCbsActname() {
        return payinCbsActname;
    }

    public void setPayinCbsActname(String payinCbsActname) {
        this.payinCbsActname = payinCbsActname;
    }

    public String getPayinCbsActno() {
        return payinCbsActno;
    }

    public void setPayinCbsActno(String payinCbsActno) {
        this.payinCbsActno = payinCbsActno;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }
}

