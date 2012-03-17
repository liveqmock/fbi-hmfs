package dep.hmfs.online.processor.hmb;

import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg009;
import dep.hmfs.online.processor.hmb.domain.Msg010;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn6210Processor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6210Processor.class);

    @Override
    @Transactional
    public byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        Msg009 msg009 = (Msg009) hmbMsgList.get(0);

        Msg010 summaryMsg = new Msg010();
        try {
            BeanUtils.copyProperties(summaryMsg, msg009);
        } catch (Exception e) {
            throw new RuntimeException("报文转换错误！");
        }
        summaryMsg.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        summaryMsg.origSysId = "00";
        summaryMsg.msgDt =  SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        summaryMsg.rtnInfoCode = "00";

        try {
            //TODO 事务处理
            hmbBaseService.updateOrInsertMsginsByHmbMsgList(txnCode, hmbMsgList);
            hmbActinfoService.splitFundActinfo(txnCode, hmbMsgList);
            summaryMsg.rtnInfo = "交易处理完成.";
        } catch (Exception e) {
            logger.error(txnCode + "交易处理异常！", e);
            summaryMsg.rtnInfoCode = "99";
            summaryMsg.rtnInfo = "交易失败,原因：" + e.getMessage();;
        }
        // 响应
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(summaryMsg);
        return mf.marshal(txnCode, rtnHmbMsgList);
    }
}
