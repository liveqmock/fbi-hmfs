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
@HmbMessage("100")
public class Msg100 extends HmbMsg{
    //F2�����ʱ��ĵı�ţ�Ψһ��ʶһ�ʱ��ģ����������ĵ����������У�����ı䣬�������£�2λ�꣨��ĺ���λ��+2λ��+2λ��+6λ���+4λ��������+2λ�����
    @Hmb8583Field(2)
    public String msgSn;

    //F4�����ͱ��ķ���ϵͳ����
    @Hmb8583Field(4)
    public String sendSysId;

    //F5����ʶ���ķ���:���ֶ��ɷ��ؾ�ͳһ���壬�磺ά���ʽ�ϵͳΪ00
    @Hmb8583Field(5)
    public String origSysId;

    //F10�����ڱ�ʶ���Ĵ���Ľ�����ɹ�Ϊ"00"���ɸ������б��Ĵ�����У�����ͨѶ�������˫��Լ�������룻���Ϊҵ�����������ͳһ�����롰99��
    @Hmb8583Field(10)
    public String rtnInfoCode;

    //F11������˵�����Ĵ������Ķ�Ӧ������Ϣ��������Ĵ������Ϊ��99��,���ʾҵ���������Ϣ����ϸ������
    @Hmb8583Field(11)
    public String rtnInfo;
}
