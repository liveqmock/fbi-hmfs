package gateway.xsocket.service;

import gateway.iso8583.IsoMessage;
import gateway.iso8583.MessageFactory;
import gateway.iso8583.parse.ConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ÉÏÎç2:27
 * To change this template use File | Settings | File Templates.
 */

public class SoktServerMsgService implements IMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SoktServerMsgService.class);

    @Override
    public byte[] handleMessage(byte[] bytes) throws IOException, ParseException {
        // TODO
        MessageFactory messageFactory = ConfigParser.createFromClasspathConfig("/j8583-config.xml");
        Map<String, List<IsoMessage>> txnMessageMap = messageFactory.parseTxnMessage(bytes);

        return null;
    }
}
