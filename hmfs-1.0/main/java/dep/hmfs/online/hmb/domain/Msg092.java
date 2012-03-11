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
@HmbMessage("092")
public class Msg092 extends SubMsg{
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

    //F54：8位日期＋6位时间（格式为：YYYYMMDDHHMMSS）
    @Hmb8583Field(54)
    public String txnDt;

    //F57：流水编号
    @Hmb8583Field(55)
    public String streamNo;
}
