package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
public class SummaryMsg extends HmbMsg{
    //F2�����ʱ��ĵı�ţ�Ψһ��ʶһ�ʱ��ģ����������ĵ����������У�����ı䣬�������£�2λ�꣨��ĺ���λ��+2λ��+2λ��+6λ���+4λ��������+2λ�����
    @Hmb8583Field(2)
    public String msgSn;

    //F3�����е��ӱ��ĵ���Ŀ(���������ܱ���)
    @Hmb8583Field(3)
    public int submsgNum;

    //F4�����ͱ��ķ���ϵͳ����
    @Hmb8583Field(4)
    public String sendSysId;

    //F5����ʶ���ķ���:���ֶ��ɷ��ؾ�ͳһ���壬�磺ά���ʽ�ϵͳΪ00
    @Hmb8583Field(5)
    public String origSysId;

    //F6�����Ĳ�����ʱ�䣺8λ���ڣ�6λʱ�䣨��ʽΪ��YYYYMMDDHHMMSS��
    @Hmb8583Field(6)
    public String msgDt;

    //F7�����ĵĽ�ֹ�������ڣ������������Ϊ�ǹ����գ�����Ҫ˳�ӵ���һ������
    @Hmb8583Field(7)
    public String msgEndDate;
}
