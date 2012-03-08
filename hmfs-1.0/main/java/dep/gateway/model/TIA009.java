package dep.gateway.model;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
public class TIA009 extends SummaryMsg {
    //F12:交易方式  1－批量；2－单笔
    public String txnType;

    //F13:业务种类  1－单位；2－业主；3－其他；4－抹账
    public String bizType;

    //F14:交易类型 发起方的交易类型码
    public String txnCode;

    //F16：信息ID1
    public String infoId1;

    //F17：信息ID1类型
    public String infoIdType1;

    //F18：信息ID2
    public String infoId2;

    //F19：信息ID2类型
    public String infoIdType2;

    //F25：归属区县ID
    public String districtId;

    //F28：核算户账号1。
    public String fundActno1;

    //F29：核算户账号1类型
    public String fundActtype1;

    //F95：划入资金帐号
    public String payinCbsActno;

    //F96：划入资金帐号类型
    public String payinCbsActtype;

    //F45：交易金额1
    public BigDecimal txnAmt1;
}
