package dep.hmfs.online.processor.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("011")
public class Msg011 extends SummaryMsg {
    //F12:交易方式  1－批量；2－单笔
    @Hmb8583Field(12)
    public String txnType;

    //F13:业务种类  1－单位；2－业主；3－其他；4－抹账
    @Hmb8583Field(13)
    public String bizType;

    //F14:交易类型 发起方的交易类型码
    @Hmb8583Field(14)
    public String origTxnCode;

    //F16：信息ID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17：信息ID1类型
    @Hmb8583Field(17)
    public String infoIdType1;

    //F18：信息ID2
    @Hmb8583Field(18)
    public String infoId2;

    //F19：信息ID2类型
    @Hmb8583Field(19)
    public String infoIdType2;

    //F25：归属区县ID
    @Hmb8583Field(25)
    public String districtId;

    //F28：核算户账号1。
    @Hmb8583Field(28)
    public String fundActno1;

    //F29：核算户账号1类型
    @Hmb8583Field(29)
    public String fundActtype1;

    //F30：核算户账号2。
    @Hmb8583Field(30)
    public String fundActno2;

    //F31：核算户账号2类型
    @Hmb8583Field(31)
    public String fundActtype2;

    //F37：账户余额
    @Hmb8583Field(37)
    public BigDecimal actBal;

    //F41：开户银行名称
    @Hmb8583Field(41)
    public String bankName;

    //F42：开户银行分支机构编号
    @Hmb8583Field(42)
    public  String branchId;

    //F45：交易金额1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F84：资金付方会计账号
    @Hmb8583Field(84)
    public String payoutActno;

    //F86：资金收方会计账号
    @Hmb8583Field(86)
    public String payinActno;

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getOrigTxnCode() {
        return origTxnCode;
    }

    public void setOrigTxnCode(String origTxnCode) {
        this.origTxnCode = origTxnCode;
    }

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

    public String getInfoId2() {
        return infoId2;
    }

    public void setInfoId2(String infoId2) {
        this.infoId2 = infoId2;
    }

    public String getInfoIdType2() {
        return infoIdType2;
    }

    public void setInfoIdType2(String infoIdType2) {
        this.infoIdType2 = infoIdType2;
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

    public BigDecimal getActBal() {
        return actBal;
    }

    public void setActBal(BigDecimal actBal) {
        this.actBal = actBal;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public BigDecimal getTxnAmt1() {
        return txnAmt1;
    }

    public void setTxnAmt1(BigDecimal txnAmt1) {
        this.txnAmt1 = txnAmt1;
    }

    public String getPayoutActno() {
        return payoutActno;
    }

    public void setPayoutActno(String payoutActno) {
        this.payoutActno = payoutActno;
    }

    public String getPayinActno() {
        return payinActno;
    }

    public void setPayinActno(String payinActno) {
        this.payinActno = payinActno;
    }
}
