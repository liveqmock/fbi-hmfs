package dep.gateway.service;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ионГ2:47
 * To change this template use File | Settings | File Templates.
 */
public interface IMessageHandler {
    byte[] handleMessage(byte[] bytes) throws IOException, ParseException;
}
