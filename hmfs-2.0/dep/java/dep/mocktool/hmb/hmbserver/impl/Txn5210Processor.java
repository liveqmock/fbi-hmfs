package dep.mocktool.hmb.hmbserver.impl;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.mocktool.hmb.hmbserver.AbstractTxnProcessor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç1:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class Txn5210Processor extends AbstractTxnProcessor {
    @Override
    public byte[] process() {
        try {
            return handleSignon();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return inBuf;
    }

    private byte[] handleSignon() throws InvocationTargetException, IllegalAccessException {
        deleteAndInsertMsginsByHmbMsgList();

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(createRtnMsg100(inMsgSn));
        byte[] txnBuf = messageFactory.marshal(inTxnCode, hmbMsgList);
        return txnBuf;
    }
}
