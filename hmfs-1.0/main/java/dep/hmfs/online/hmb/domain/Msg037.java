package dep.hmfs.online.hmb.domain;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class Msg037 extends SubMsg{
    //F8：动作代码
    public String actionCode;

    //F16：信息ID1
    public String infoId1;

    //F17：信息ID1类型
    public String infoIdType1;

    //F21：信息名称
    public String infoName;

    //F22：信息地址
    public String infoAddr;

    //F24：建筑面积
    public String builderArea;

    //F28：核算户账号1。
    public String fundActno1;

    //F29：核算户账号1类型
    public String fundActtype1;

    //F45：交易金额1
    public BigDecimal txnAmt1;

    //F59：单位ID
    public String orgId;

    //F60：单位类型  10：市局；11：区局；12：开发商；13：业委会；14：物业公司；15：审价单位；16：监理单位
    public String orgType;

    //F61：单位名称
    public String orgName;

    //F77：收据编号
    public String receiptNo;

    //F78：交存标准1
    public String depStandard1;

    //F79：缴款类型 00:首次交款 01:维修资金追缴 02:维修资金续交 03:结息交款 04:欠款补交 05:其他交款 06:收益资金交款
    public String depType;

    //F80：缴存人
    public String depPerson;

    //F81：户卡号
    public String houseCardNo;

    //F82：购房合同号
    public String houseContNo;

    //F86：资金收方会计账号
    public String payinActno;

    //F90：凭证类型   00-商品住宅;
    public String voucherType;

    //F91：关联报文编号
    public String linkMsgSn;
}

