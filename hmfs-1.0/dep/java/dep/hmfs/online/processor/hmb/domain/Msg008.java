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
@HmbMessage("008")
public class Msg008 extends SummaryResponseMsg {
    //F16：信息ID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17：信息ID1类型
    @Hmb8583Field(17)
    public String infoIdType1;

    //F25：归属区县ID
    @Hmb8583Field(25)
    public String districtId;

    //F28：核算户账号1。
    @Hmb8583Field(28)
    public String fundActno1;

    //F29：核算户账号1类型
    @Hmb8583Field(29)
    public String fundActtype1;

    //F32：结算户账号1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33：结算户账号1类型
    @Hmb8583Field(33)
    public String settleActtype1;

    //F45：交易金额1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F84：资金付方会计账号
    @Hmb8583Field(84)
    public String payoutActno;

    //F85：付款方名称
    @Hmb8583Field(85)
    public String payoutActName;

    //F86：资金收方会计账号
    @Hmb8583Field(86)
    public String payinActno;

    //F87：收款方名称
    @Hmb8583Field(87)
    public String payinActName;

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

    public String getPayoutActno() {
        return payoutActno;
    }

    public void setPayoutActno(String payoutActno) {
        this.payoutActno = payoutActno;
    }

    public String getPayoutActName() {
        return payoutActName;
    }

    public void setPayoutActName(String payoutActName) {
        this.payoutActName = payoutActName;
    }

    public String getPayinActno() {
        return payinActno;
    }

    public void setPayinActno(String payinActno) {
        this.payinActno = payinActno;
    }

    public String getPayinActName() {
        return payinActName;
    }

    public void setPayinActName(String payinActName) {
        this.payinActName = payinActName;
    }
}
