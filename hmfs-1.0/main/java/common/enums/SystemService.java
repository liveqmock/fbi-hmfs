package common.enums;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-11
 * Time: обнГ12:22
 * To change this template use File | Settings | File Templates.
 */
public class SystemService {

    public static String formatDateByPattern(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }
    

}
