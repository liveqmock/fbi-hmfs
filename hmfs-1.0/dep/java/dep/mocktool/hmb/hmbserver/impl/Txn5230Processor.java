package dep.mocktool.hmb.hmbserver.impl;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import dep.mocktool.hmb.hmbserver.AbstractTxnProcessor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *ÍË¿î
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç1:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class Txn5230Processor extends AbstractTxnProcessor {
    @Override
    public byte[] process(byte[] msgin) {
        try {
            return handleSignon(msgin);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return msgin;
    }

    private byte[] handleSignon(byte[] msgin) throws InvocationTargetException, IllegalAccessException {
        Map<String, List<HmbMsg>> responseMap = getResponseMap(msgin);

        String rtnTxnCode = (String) responseMap.keySet().toArray()[0];
        List<HmbMsg> msgList = responseMap.get(rtnTxnCode);

        deleteAndInsertMsginsByHmbMsgList(rtnTxnCode, msgList);

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(createRtnMsg100(((SummaryMsg)msgList.get(0)).msgSn));
        byte[] txnBuf = messageFactory.marshal(rtnTxnCode, hmbMsgList);
        return txnBuf;
    }
}
