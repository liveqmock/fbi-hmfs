package dep.hmfs.online.hmb.domain;

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
}
