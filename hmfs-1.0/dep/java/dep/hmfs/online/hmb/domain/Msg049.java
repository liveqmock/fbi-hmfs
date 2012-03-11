package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("049")
public class Msg049 extends SubMsg{
    //F59：单位ID
    @Hmb8583Field(59)
    public String orgId;

    //F60：单位类型  10：市局；11：区局；12：开发商；13：业委会；14：物业公司；15：审价单位；16：监理单位
    @Hmb8583Field(60)
    public String orgType;

    //F77：收据编号
    @Hmb8583Field(77)
    public String receiptNo;

    //F89：凭证状态 0-已登记;1-已领用;2-已使用;3-已作废;4-已核销
    @Hmb8583Field(89)
    public String voucherSts;

    //F90：凭证类型   00-商品住宅;
    @Hmb8583Field(90)
    public String voucherType;

    //F91：关联报文编号
    @Hmb8583Field(91)
    public String linkMsgSn;
}

