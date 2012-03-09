package dep.hmfs.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 报文 交易码定义.
 * User: zhanrui
 * Date: 12-3-9
 * Time: 下午12:12
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HmbMessage {
    String value() default "";
}
