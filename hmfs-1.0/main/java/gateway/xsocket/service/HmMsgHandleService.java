package gateway.xsocket.service;

import gateway.iso8583.IsoMessage;
import gateway.iso8583.IsoType;
import gateway.iso8583.MessageFactory;
import gateway.iso8583.parse.ConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
 */

@Service
public class HmMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CbsMsgHandleService.class);

    private MessageFactory messageFactory;

    public HmMsgHandleService() {
        try {
            messageFactory = ConfigParser.createFromClasspathConfig("/j8583-config.xml");
        } catch (IOException e) {
            logger.info("【本地服务端】J8583初始化配置异常!");
        }
    }

    @Override
    public byte[] handleMessage(byte[] bytes) {
        // TODO
        try {
            Map<String, List<IsoMessage>> txnMessageMap = messageFactory.parseTxnMessageMap(bytes);

            logger.info("【本地服务端】接收交易编码：" + txnMessageMap.keySet().iterator().next());
            for (IsoMessage isoMessage : txnMessageMap.entrySet().iterator().next().getValue()) {
                logger.info("【本地服务端】接收报文编号：" + isoMessage.getField(1));
            }
            // TODO
            IsoMessage m = messageFactory.newMessage(0x200);

            m.setValue(4, "sfsfs", IsoType.LVAR, 0);
            m.setValue(12, new Date(), IsoType.TIME, 0);
            m.setValue(15, new Date(), IsoType.DATE4, 0);
            m.setValue(17, new Date(), IsoType.DATE_EXP, 0);
            m.setValue(37, 12345678, IsoType.LLVAR, 12);
            m.setValue(41, "TEST-TERMINAL", IsoType.ALPHA, 16);
            m.setHasNext(false);
            FileOutputStream fout = new FileOutputStream("d:/tmp/iso.bin");
            m.write(fout);

            fout.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
