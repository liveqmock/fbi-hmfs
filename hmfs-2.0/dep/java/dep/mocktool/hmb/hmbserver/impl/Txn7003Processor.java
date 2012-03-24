package dep.mocktool.hmb.hmbserver.impl;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.mocktool.hmb.hmbserver.AbstractTxnProcessor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * »’÷’”‡∂Ó∂‘’ .
 * User: zhanrui
 * Date: 12-3-14
 * Time: œ¬ŒÁ1:22
 * To change this template use File | Settings | File Templates.
 */
@Service
public class Txn7003Processor extends AbstractTxnProcessor {
    @Override
    public byte[] process() {
        return handleSignon();
    }

    private byte[] handleSignon(){
        Msg002 msg002 = new Msg002();
        assembleSummaryMsg(msg002, 1);

        msg002.rtnInfoCode = "00";

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg002);
        byte[] txnBuf = messageFactory.marshal(inTxnCode, hmbMsgList);
        return txnBuf;
    }
}
