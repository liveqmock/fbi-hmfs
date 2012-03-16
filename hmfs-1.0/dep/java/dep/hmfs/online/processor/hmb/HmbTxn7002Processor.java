package dep.hmfs.online.processor.hmb;

import common.service.HisMsginLogService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg099;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn7002Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn7002Processor.class);
    @Autowired
    private HisMsginLogService hisMsginLogService;
    private static final String[] CAN_CANCEL_CODES = {"01035", "01039", "01041", "01043", "01045"};
    @Override
    public byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        Msg002 msg002 = new Msg002();
        msg002.msgSn = msgSn;
        msg002.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg002.origSysId = "00";
        msg002.rtnInfoCode = "00";
        try {
            hmbBaseService.updateMsginsByHmbMsgList(txnCode, hmbMsgList);
            Msg001 msg001 = (Msg001)hmbMsgList.get(0);
            msg002.origMsgSn = msg001.msgSn;

            Msg099 msg099 = (Msg099)hmbMsgList.get(1);
            int cnt = hisMsginLogService.cancelMsginsByMsgSnAndTypes(msg099.origMsgSn, CAN_CANCEL_CODES);
            msg002.rtnInfo = cnt + "笔交易撤销处理完成";

        } catch (Exception e) {
            logger.error("7002交易处理异常！", e);
            msg002.rtnInfoCode = "99";
            msg002.rtnInfo = "报文接收失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg002);
        return mf.marshal("7002", rtnHmbMsgList);
    }
}
