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
@HmbMessage("027")
public class Msg027 extends SubMsg{
    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F18����ϢID2
    @Hmb8583Field(18)
    public String infoId2;

    //F19����ϢID2����
    @Hmb8583Field(19)
    public String infoIdType2;

    //F20����Ϣ����
    @Hmb8583Field(20)
    public String infoCode;

    //F21����Ϣ����
    @Hmb8583Field(21)
    public String infoName;

    //F22����Ϣ��ַ
    @Hmb8583Field(22)
    public String infoAddr;

    //F23���ֻ���
    @Hmb8583Field(23)
    public String cellNum;

    //F24���������
    @Hmb8583Field(24)
    public String builderArea;

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;

    //F71���������赥λ����    ���㻧Ϊ����ʱ��������д#
    @Hmb8583Field(71)
    public String devOrgName;
}
