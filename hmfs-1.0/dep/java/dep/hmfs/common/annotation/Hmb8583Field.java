package dep.hmfs.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ������8583������������.  value: 8583����
 * User: zhanrui
 * Date: 12-3-9
 * Time: ����12:12
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Hmb8583Field {
    int value() default 0;
}
