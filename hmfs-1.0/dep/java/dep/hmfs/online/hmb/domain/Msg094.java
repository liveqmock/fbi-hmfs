package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("094")
public class Msg094 extends SubMsg{
    //F32�����㻧�˺�1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33�����㻧�˺�1����
    @Hmb8583Field(33)
    public String settleActtype1;

    //F37���˻����
    @Hmb8583Field(37)
    public BigDecimal actBal;

    //F59����λID
    @Hmb8583Field(59)
    public String orgId;

    //F60����λ����  10���о֣�11�����֣�12�������̣�13��ҵί�᣻14����ҵ��˾��15����۵�λ��16������λ
    @Hmb8583Field(60)
    public String orgType;
}
