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
@HmbMessage("099")
public class Msg099 extends SubMsg{
    //F9�����½�����ص�ԭʼ���׵ı��ı�ţ���Ĩ�ˡ������ཻ�ױ�Ĩ�ˡ��������׵ı��ı��
    @Hmb8583Field(9)
    public String origMsgSn;
}

