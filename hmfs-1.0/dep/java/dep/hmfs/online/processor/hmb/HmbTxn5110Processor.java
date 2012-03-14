package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg004;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn5110Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5110Processor.class);

    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        Msg100 msg100 = new Msg100();
        msg100.msgSn = txnsnGenerator.generateTxnsn(txnCode);
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        try {
            hmbBaseService.insertMsginsByHmbMsgList(txnCode, hmbMsgList);

            Msg004 msg004 = (Msg004) hmbMsgList.get(0);
            if ("00".equals(msg004.rtnInfoCode)) {
                int cnt = hmbDetailMsgService.createActinfosByMsgList(hmbMsgList.subList(1, hmbMsgList.size() - 1));
                msg100.rtnInfo = cnt + "笔单位结算户开户处理完成";
            } else {
                logger.info("5110国土局单位结算户开户处理失败！【国土局】返回信息：" + msg004.rtnInfo);
            }
        } catch (Exception e) {
            logger.error("5110交易处理异常！", e);
            msg100.rtnInfoCode = "99";
            msg100.rtnInfo = "报文接收失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg100);
        return mf.marshal("9999", rtnHmbMsgList);
    }
}
