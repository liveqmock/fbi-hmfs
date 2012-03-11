package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("003")
public class Msg003 extends SummaryMsg{
    //F12:���׷�ʽ  1��������2������
    @Hmb8583Field(12)
    public String txnType;

    //F13:ҵ������  1����λ��2��ҵ����3��������4��Ĩ��
    @Hmb8583Field(13)
    public String bizType;

    //F14:�������� ���𷽵Ľ���������
    @Hmb8583Field(14)
    public String origTxnCode;

    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;
}
