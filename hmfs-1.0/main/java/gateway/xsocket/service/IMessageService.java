package gateway.xsocket.service;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ����2:47
 * To change this template use File | Settings | File Templates.
 */
public interface IMessageService {
    byte[] handleMessage(byte[] bytes) throws IOException, ParseException;
}
