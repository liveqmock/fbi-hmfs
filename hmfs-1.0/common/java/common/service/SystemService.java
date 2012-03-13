package common.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-11
 * Time: 下午12:22
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
    本笔报文的编号，唯一标识一笔报文，在整个报文的生命周期中，不会改变，规则如下：2位年（年的后两位）+2位月+2位日+6位序号+4位交易类型+2位发起地
     */
    // TODO 待修改
    public static String getDatagramNo() {
        return formatTodayByPattern("yyMMddssssss") + "561007";
    }
}
