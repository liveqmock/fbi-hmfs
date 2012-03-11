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
@HmbMessage("033")
public class Msg033 extends SubMsg{
    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

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

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F30�����㻧�˺�2��
    @Hmb8583Field(30)
    public String fundActno2;

    //F31�����㻧�˺�2����
    @Hmb8583Field(31)
    public String fundActtype2;

    //F71���������赥λ����    ���㻧Ϊ����ʱ��������д#
    @Hmb8583Field(71)
    public String devOrgName;

    //F76�����ݽ�������
    @Hmb8583Field(76)
    public String houseDepType;

    //F78�������׼1
    @Hmb8583Field(78)
    public String depStandard1;

    //F83�������׼2
    @Hmb8583Field(83)
    public String depStandard2;

    //F99���Ƿ����
    @Hmb8583Field(99)
    public String sellFlag;

    //F100��¥��
    @Hmb8583Field(100)
    public String buildingNo;

    //F101���ź�
    @Hmb8583Field(101)
    public String unitNo;

    //F102���Һ�
    @Hmb8583Field(102)
    public String roomNo;

    //F104��֤������
    @Hmb8583Field(104)
    public String certType;

    //F105��֤�����
    @Hmb8583Field(105)
    public String certId;

    //F64����λ��ϵ�绰
    @Hmb8583Field(64)
    public String orgPhone;

    //F82��������ͬ��
    @Hmb8583Field(82)
    public String houseContNo;

    //F88����������ϵ�绰
    @Hmb8583Field(88)
    public String houseCustPhone;

    //F93�����޵���
    @Hmb8583Field(93)
    public String elevatorType;

    //F106���������ܶ�
    @Hmb8583Field(106)
    public String houseTotalAmt;

    //F38������ʺ�
    @Hmb8583Field(38)
    public String cbsActno;
}
