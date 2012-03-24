package dep.hmfs.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 房产局8583报文数据域定义.  value: 8583域编号
 * User: zhanrui
 * Date: 12-3-9
 * Time: 下午12:12
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Hmb8583Field {
    int value() default 0;
}
