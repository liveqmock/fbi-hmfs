package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:48
 * To change this template use File | Settings | File Templates.
 */
public class HmbMsg {
    //F1��2λ�������ࣨ00-����;01-�ӱ��ģ�+ 3λ�������
    @Hmb8583Field(1)
    public String msgType;

    //F128���������ı�־ 0����ʾû�к����ӱ��ģ�1����ʾ���к����ӱ��ģ�
    //@Hmb8583Field(128)
    //public String msgNextFlag = "0";


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
