package dep.hmfs.online.hmb.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
public class Txn008 extends SummaryResponseMsg {
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

    //F84：资金付方会计账号
    public String payoutActno;

    //F85：付款方名称
    public String payoutActName;

    //F86：资金收方会计账号
    public String payinActno;

    //F87：收款方名称
    public String payinActName;
}
