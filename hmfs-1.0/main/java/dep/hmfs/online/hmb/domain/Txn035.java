package dep.hmfs.online.hmb.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class Txn035 extends SubMsg{
    //F8：动作代码
    public String actionCode;

    //F16：信息ID1
    public String infoId1;

    //F17：信息ID1类型
    public String infoIdType1;

    //F20：信息编码
    public String infoCode;

    //F21：信息名称
    public String infoName;

    //F22：信息地址
    public String infoAddr;

    //F23：分户数
    public String cellNum;

    //F24：建筑面积
    public String builderArea;

    //F25：归属区县ID
    public String districtId;

    //F28：核算户账号1。
    public String fundActno1;

    //F29：核算户账号1类型
    public String fundActtype1;

    //F30：核算户账号2。
    public String fundActno2;

    //F31：核算户账号2类型
    public String fundActtype2;

    //F32：结算户账号1
    public String settleActno1;

    //F33：结算户账号1类型
    public String settleActtype1;

    //F45：交易金额1
    public BigDecimal txnAmt1;

    //F71：开发建设单位名称    核算户为区县时，可以填写#
    public String devOrgName;

    //F87：收款方名称
    public String payinActName;

}
