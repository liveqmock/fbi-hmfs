package common.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-11
 * Time: ����12:22
 * To change this template use File | Settings | File Templates.
 */
public class SystemService {

    public static String formatTodayByPattern(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    public static long daysBetween(String strDate1, String strDate2, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date1 = sdf.parse(strDate1);
        Date date2 = sdf.parse(strDate2);
        long days = (date1.getTime()-date2.getTime())/(24*60*60*1000);
        return days > 0 ? days : (days * -1);
    }
    
    /*
    ���ʱ��ĵı�ţ�Ψһ��ʶһ�ʱ��ģ����������ĵ����������У�����ı䣬�������£�2λ�꣨��ĺ���λ��+2λ��+2λ��+6λ���+4λ��������+2λ�����
     */
    // TODO ���޸�
    public static String getDatagramNo() {
        return formatTodayByPattern("yyMMddssssss") + "561007";
    }
}
