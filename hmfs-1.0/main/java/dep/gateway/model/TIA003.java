package dep.gateway.model;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
public class TIA003 extends SummaryMsgTIA{
    //F12:���׷�ʽ  1��������2������
    public String txnType;

    //F13:ҵ������  1����λ��2��ҵ����3��������4��Ĩ��
    public String bizType;

    //F14:�������� ���𷽵Ľ���������
    public String txnCode;

    //F16����ϢID1
    public String infoId1;

    //F17����ϢID1����
    public String infoIdType1;

    //F25����������ID
    public String districtId;
}
