package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("008")
public class Msg008 extends SummaryResponseMsg {
    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F32�����㻧�˺�1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33�����㻧�˺�1����
    @Hmb8583Field(33)
    public String settleActtype1;

    //F45�����׽��1
    @Hmb8583Field(45)
    public BigDecimal txnAmt1;

    //F84���ʽ𸶷�����˺�
    @Hmb8583Field(84)
    public String payoutActno;

    //F85���������
    @Hmb8583Field(85)
    public String payoutActName;

    //F86���ʽ��շ�����˺�
    @Hmb8583Field(86)
    public String payinActno;

    //F87���տ����
    @Hmb8583Field(87)
    public String payinActName;
}
