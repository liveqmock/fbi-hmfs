package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
public class SummaryResponseMsg extends SummaryMsg{
    //F9�����½�����ص�ԭʼ���׵ı��ı�ţ���Ĩ�ˡ������ཻ�ױ�Ĩ�ˡ��������׵ı��ı��
    public String origMsgSn;

    //F10�����ڱ�ʶ���Ĵ���Ľ�����ɹ�Ϊ"00"���ɸ������б��Ĵ�����У�����ͨѶ�������˫��Լ�������룻���Ϊҵ�����������ͳһ�����롰99��
    public String rtnInfoCode;

    //F11������˵�����Ĵ������Ķ�Ӧ������Ϣ��������Ĵ������Ϊ��99��,���ʾҵ���������Ϣ����ϸ������
    public String rtnInfo;
}
