package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����7:05
 * To change this template use File | Settings | File Templates.
 */
public class Txn100 extends HmbMsg{
    //F2�����ʱ��ĵı�ţ�Ψһ��ʶһ�ʱ��ģ����������ĵ����������У�����ı䣬�������£�2λ�꣨��ĺ���λ��+2λ��+2λ��+6λ���+4λ��������+2λ�����
    public String msgSn;

    //F4�����ͱ��ķ���ϵͳ����
    public String sendSysId;

    //F5����ʶ���ķ���:���ֶ��ɷ��ؾ�ͳһ���壬�磺ά���ʽ�ϵͳΪ00
    public String origSysId;

    //F10�����ڱ�ʶ���Ĵ���Ľ�����ɹ�Ϊ"00"���ɸ������б��Ĵ�����У�����ͨѶ�������˫��Լ�������룻���Ϊҵ�����������ͳһ�����롰99��
    public String rtnInfoCode;

    //F11������˵�����Ĵ������Ķ�Ӧ������Ϣ��������Ĵ������Ϊ��99��,���ʾҵ���������Ϣ����ϸ������
    public String rtnInfo;

}
