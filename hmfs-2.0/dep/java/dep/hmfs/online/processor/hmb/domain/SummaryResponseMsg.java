package dep.hmfs.online.processor.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
public class SummaryResponseMsg extends SummaryMsg{
    //F9�����½�����ص�ԭʼ���׵ı��ı�ţ���Ĩ�ˡ������ཻ�ױ�Ĩ�ˡ��������׵ı��ı��
    @Hmb8583Field(9)
    public String origMsgSn = "#";

    //F10�����ڱ�ʶ���Ĵ���Ľ�����ɹ�Ϊ"00"���ɸ������б��Ĵ�����У�����ͨѶ�������˫��Լ�������룻���Ϊҵ�����������ͳһ�����롰99��
    @Hmb8583Field(10)
    public String rtnInfoCode = "#";

    //F11������˵�����Ĵ������Ķ�Ӧ������Ϣ��������Ĵ������Ϊ��99��,���ʾҵ���������Ϣ����ϸ������
    @Hmb8583Field(11)
    public String rtnInfo = "#";

    public String getOrigMsgSn() {
        return origMsgSn;
    }

    public void setOrigMsgSn(String origMsgSn) {
        this.origMsgSn = origMsgSn;
    }

    public String getRtnInfoCode() {
        return rtnInfoCode;
    }

    public void setRtnInfoCode(String rtnInfoCode) {
        this.rtnInfoCode = rtnInfoCode;
    }

    public String getRtnInfo() {
        return rtnInfo;
    }

    public void setRtnInfo(String rtnInfo) {
        this.rtnInfo = rtnInfo;
    }
}
