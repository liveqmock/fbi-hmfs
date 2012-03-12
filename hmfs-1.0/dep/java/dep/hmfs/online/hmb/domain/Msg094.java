package dep.hmfs.online.hmb.domain;

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
@HmbMessage("094")
public class Msg094 extends SubMsg{
    //F32：结算户账号1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33：结算户账号1类型
    @Hmb8583Field(33)
    public String settleActtype1;

    //F37：账户余额
    @Hmb8583Field(37)
    public BigDecimal actBal;

    //F59：单位ID
    @Hmb8583Field(59)
    public String orgId;

    //F60：单位类型  10：市局；11：区局；12：开发商；13：业委会；14：物业公司；15：审价单位；16：监理单位
    @Hmb8583Field(60)
    public String orgType;

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

    public BigDecimal getActBal() {
        return actBal;
    }

    public void setActBal(BigDecimal actBal) {
        this.actBal = actBal;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }
}
