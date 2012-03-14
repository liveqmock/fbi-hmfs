package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg004;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn5160Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5160Processor.class);

    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        Msg004 msg004 = new Msg004();
        msg004.msgSn = txnsnGenerator.generateTxnsn(txnCode);
        msg004.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg004.origSysId = "00";
        msg004.rtnInfoCode = "00";
        msg004.rtnInfo = "核算户信息更新处理完成";
        try {
            hmbBaseService.insertMsginsByHmbMsgList(txnCode, hmbMsgList);
            int cnt = hmbDetailMsgService.updateActinfosByMsgList(hmbMsgList.subList(1, hmbMsgList.size() - 1));
            msg004.rtnInfo = cnt + "核算户信息更新处理完成";
        } catch (Exception e) {
            logger.error("5160交易处理异常！", e);
            msg004.rtnInfoCode = "99";
            msg004.rtnInfo = "报文接收失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg004);
        return mf.marshal("5160", rtnHmbMsgList);
    }
}
