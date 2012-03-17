package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn5230Processor extends HmbAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5230Processor.class);

    @Override
    public byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {

        Msg100 msg100 = createRtnMsg100(msgSn);
        try {
            hmbBaseService.updateOrInsertMsginsByHmbMsgList(txnCode, hmbMsgList);
            hmActinfoFundService.updateActinfoFundsByMsgList(hmbMsgList.subList(1, hmbMsgList.size()));
        } catch (Exception e) {
            logger.error("报文接收保存失败！", e);
            msg100.rtnInfoCode = "99";
            msg100.rtnInfo = "报文接收失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg100);
        return mf.marshal("9999", rtnHmbMsgList);
    }
}
