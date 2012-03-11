package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("005")
public class Msg005 extends SummaryMsg {
    //F12:交易方式  1－批量；2－单笔
    public String txnType;

    //F13:业务种类  1－单位；2－业主；3－其他；4－抹账
    public String bizType;

    //F14:交易类型 发起方的交易类型码
    public String origTxnCode;

    //F16：信息ID1
    public String infoId1;

    //F17：信息ID1类型
    public String infoIdType1;

    //F25：归属区县ID
    public String districtId;

    //F28：核算户账号1。
    public String fundActno1;

    //F29：核算户账号1类型
    public String fundActtype1;

    //F32：结算户账号1
    public String settleActno1;

    //F33：结算户账号1类型
    public String settleActtype1;

    //F45：交易金额1
    public BigDecimal txnAmt1;

    //F79：缴款类型 00:首次交款 01:维修资金追缴 02:维修资金续交 03:结息交款 04:欠款补交 05:其他交款 06:收益资金交款
    public String depType;
}
