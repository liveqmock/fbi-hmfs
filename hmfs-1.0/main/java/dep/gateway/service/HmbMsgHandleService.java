package dep.gateway.service;

import dep.gateway.hmb8583.HmbMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
 */

@Service
public class HmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);

    @Autowired
    private HmbMessageFactory hmbMessageFactory;


    @Override
    public byte[] handleMessage(byte[] bytes) {
        // TODO
/*
            Map<String, List<IsoMessage>> txnMessageMap = hmbMessageFactory.unmashal(bytes);

            logger.info("【本地服务端】接收交易编码：" + txnMessageMap.keySet().iterator().next());
            for (IsoMessage isoMessage : txnMessageMap.entrySet().iterator().next().getValue()) {
                logger.info("【本地服务端】接收报文编号：" + isoMessage.getField(1));
            }
*/
            // TODO
/*
            IsoMessage m = hmbMessageFactory.newMessage();

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
*/

        return null;
    }
}
