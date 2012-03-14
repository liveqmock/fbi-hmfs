package dep.hmfs.online.processor.hmb;

import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class HmbAsynTxnProcessor extends HmbAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(HmbAsynTxnProcessor.class);
    @Resource
    HmbTxnsnGenerator txnsnGenerator;

    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        Msg100 msg100 = new Msg100();
        msg100.msgSn = txnsnGenerator.generateTxnsn(txnCode);
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfoCode = "报文接收成功";
        try {
            insertMsginsByHmbMsgList(txnCode, hmbMsgList);
        } catch (Exception e) {
            logger.error("报文接收保存失败！", e);
            msg100.rtnInfoCode = "99";
            msg100.rtnInfoCode = "报文接收失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg100);
        return mf.marshal("9999", rtnHmbMsgList);
    }
}
