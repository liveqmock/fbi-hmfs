package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
public class SummaryMsg extends HmbMsg{
    //F2�����ʱ��ĵı�ţ�Ψһ��ʶһ�ʱ��ģ����������ĵ����������У�����ı䣬�������£�2λ�꣨��ĺ���λ��+2λ��+2λ��+6λ���+4λ��������+2λ�����
    public String msgSn;

    //F3�����е��ӱ��ĵ���Ŀ(���������ܱ���)
    public int submsgNum;

    //F4�����ͱ��ķ���ϵͳ����
    public String sendSysId;

    //F5����ʶ���ķ���:���ֶ��ɷ��ؾ�ͳһ���壬�磺ά���ʽ�ϵͳΪ00
    public String origSysId;

    //F6�����Ĳ�����ʱ�䣺8λ���ڣ�6λʱ�䣨��ʽΪ��YYYYMMDDHHMMSS��
    public String msgDt;

    //F7�����ĵĽ�ֹ�������ڣ������������Ϊ�ǹ����գ�����Ҫ˳�ӵ���һ������
    public String msgEndDate;
}
