package dep.mocktool.hmb.hmbserver.impl;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.mocktool.hmb.hmbserver.AbstractTxnProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class Txn5110Processor extends AbstractTxnProcessor {
    @Override
    public byte[] process(byte[] msgin) {
        return handle(msgin);
    }

    private byte[] handle(byte[] msgin){
        System.out.println("5110报文:" + new String(msgin));
        Msg100 msg100 = new Msg100();
        msg100.msgSn = "111122223333444455";
        msg100.sendSysId = "00";
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfo = "报文接收成功";

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg100);
        byte[] txnBuf = messageFactory.marshal("9999", hmbMsgList);
        return txnBuf;
    }
}
