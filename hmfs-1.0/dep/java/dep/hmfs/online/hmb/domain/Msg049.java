package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("049")
public class Msg049 extends SubMsg{
    //F59����λID
    @Hmb8583Field(59)
    public String orgId;

    //F60����λ����  10���о֣�11�����֣�12�������̣�13��ҵί�᣻14����ҵ��˾��15����۵�λ��16������λ
    @Hmb8583Field(60)
    public String orgType;

    //F77���վݱ��
    @Hmb8583Field(77)
    public String receiptNo;

    //F89��ƾ֤״̬ 0-�ѵǼ�;1-������;2-��ʹ��;3-������;4-�Ѻ���
    @Hmb8583Field(89)
    public String voucherSts;

    //F90��ƾ֤����   00-��Ʒסլ;
    @Hmb8583Field(90)
    public String voucherType;

    //F91���������ı��
    @Hmb8583Field(91)
    public String linkMsgSn;
}

