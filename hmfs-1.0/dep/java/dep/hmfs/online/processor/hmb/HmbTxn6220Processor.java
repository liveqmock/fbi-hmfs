package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg035;
import dep.hmfs.online.processor.hmb.domain.Msg051;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

/**
 * 分户拆分合并
 */
@Component
public class HmbTxn6220Processor extends HmbSyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6220Processor.class);


    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        try {
            return handleTxn6220(txnCode, hmbMsgList);
        } catch (Exception e) {
            logger.error("分户拆分合并出现错误.", e);
            throw new RuntimeException("分户拆分合并出现错误.", e);
        }
    }

    public int handleTxn6220(String msgSn, List<HmbMsg> hmbMsgList) throws ParseException, InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size())) {
            String msgType = hmbMsg.getMsgType();
            if ("01051".equals(msgType)) {
                hmbActinfoService.op125cancelActinfoFunds(msgSn, "hmfs", "hmbtxn6220", (Msg051) hmbMsg);
            } else if ("01033".equals(msgType)) {
                hmbActinfoService.createActinfoFundByHmbMsg(hmbMsg);
            } else if ("01035".equals(msgType)) {
                hmbActinfoService.op115deposite(msgSn, "hmfs", "hmbtxn6220", (Msg035) hmbMsg);
            }
        }
        return hmbMsgList.size() - 1;
    }


}
