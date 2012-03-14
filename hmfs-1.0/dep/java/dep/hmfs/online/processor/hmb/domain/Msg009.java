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
@HmbMessage("009")
public class Msg009 extends SummaryMsg {
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

    //F95：划入资金帐号
    @Hmb8583Field(95)
    public String payinCbsActno;

    //F96：划入资金帐号类型
    @Hmb8583Field(96)
    public String payinCbsActtype;

    //F45：交易金额1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //=====================


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

    public String getPayinCbsActno() {
        return payinCbsActno;
    }

    public void setPayinCbsActno(String payinCbsActno) {
        this.payinCbsActno = payinCbsActno;
    }

    public String getPayinCbsActtype() {
        return payinCbsActtype;
    }

    public void setPayinCbsActtype(String payinCbsActtype) {
        this.payinCbsActtype = payinCbsActtype;
    }

    public BigDecimal getTxnAmt1() {
        return txnAmt1;
    }

    public void setTxnAmt1(BigDecimal txnAmt1) {
        this.txnAmt1 = txnAmt1;
    }
}
