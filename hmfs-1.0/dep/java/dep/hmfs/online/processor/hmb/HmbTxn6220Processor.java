package dep.hmfs.online.processor.hmb;

import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg005;
import dep.hmfs.online.processor.hmb.domain.Msg006;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn6220Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6220Processor.class);

    @Override
    public byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        Msg005 msg005 = (Msg005) hmbMsgList.get(0);

        Msg006 summaryMsg = new Msg006();
        try {
            PropertyUtils.copyProperties(summaryMsg, msg005);
        } catch (Exception e) {
            throw new RuntimeException("报文转换错误！");
        }
        summaryMsg.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        summaryMsg.origSysId = "00";
        summaryMsg.msgDt =  SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        summaryMsg.rtnInfoCode = "00";

        try {
            hmbDetailMsgService.handleTxn6220(txnCode, hmbMsgList);
            summaryMsg.rtnInfo = "交易处理完成.";
        } catch (Exception e) {
            logger.error(txnCode + "交易处理异常！", e);
            summaryMsg.rtnInfoCode = "99";
            summaryMsg.rtnInfo = "交易处理失败";
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(summaryMsg);
        return mf.marshal(txnCode, rtnHmbMsgList);
    }
}
