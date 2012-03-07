package gateway.xsocket.service;

import gateway.iso8583.IsoMessage;
import gateway.iso8583.IsoType;
import gateway.iso8583.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Time: ����2:27
 * To change this template use File | Settings | File Templates.
 */

public class SoktServerMsgService implements IMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SoktServerMsgService.class);
    private MessageFactory messageFactory;

    public SoktServerMsgService(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public byte[] handleMessage(byte[] bytes) {
        // TODO
        try {
            Map<String, List<IsoMessage>> txnMessageMap = messageFactory.parseTxnMessageMap(bytes);

            logger.info("�����ط���ˡ����ս��ױ��룺" + txnMessageMap.keySet().iterator().next());
            for (IsoMessage isoMessage : txnMessageMap.entrySet().iterator().next().getValue()) {
                logger.info("�����ط���ˡ����ձ��ı�ţ�" + isoMessage.getField(1));
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
