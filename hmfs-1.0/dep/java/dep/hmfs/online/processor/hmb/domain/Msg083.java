package dep.hmfs.online.processor.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("083")
public class Msg083 extends SubMsg{
    //F16：信息ID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17：信息ID1类型
    @Hmb8583Field(17)
    public String infoIdType1;

    //F20：信息编码
    @Hmb8583Field(20)
    public String infoCode;

    //F21：信息名称
    @Hmb8583Field(21)
    public String infoName;

    //F22：信息地址
    @Hmb8583Field(22)
    public String infoAddr;

    //F23：分户数
    @Hmb8583Field(23)
    public String cellNum;

    //F24：建筑面积
    @Hmb8583Field(24)
    public String builderArea;

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

    //F32：结算户账号1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33：结算户账号1类型
    @Hmb8583Field(33)
    public String settleActtype1;

    //F45：交易金额1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F53：借贷标志
    @Hmb8583Field(53)
    public String dcFlag;


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

    public String getCellNum() {
        return cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }

    public String getBuilderArea() {
        return builderArea;
    }

    public void setBuilderArea(String builderArea) {
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

    public BigDecimal getTxnAmt1() {
        return txnAmt1;
    }

    public void setTxnAmt1(BigDecimal txnAmt1) {
        this.txnAmt1 = txnAmt1;
    }

    public String getDcFlag() {
        return dcFlag;
    }

    public void setDcFlag(String dcFlag) {
        this.dcFlag = dcFlag;
    }
}

