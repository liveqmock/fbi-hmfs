package dep.test.hmb.hmbserver.impl;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.test.hmb.hmbserver.AbstractTxnProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç1:22
 * To change this template use File | Settings | File Templates.
 */
public class Txn7000Processor extends AbstractTxnProcessor {
    @Override
    public byte[] process(byte[] msgin) {
        return handleSignon(msgin);
    }

    private byte[] handleSignon(byte[] msgin){
        Msg002 msg002 = new Msg002();
        assembleSummaryMsg(msg002, 1);

        msg002.rtnInfoCode = "00";

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg002);
        byte[] txnBuf = messageFactory.marshal("7000", hmbMsgList);
        return txnBuf;
    }
}
