package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg033;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class HmbTxn6302Processor extends HmbAsyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6302Processor.class);

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        List<HmbMsg> hmbSubMsgList = hmbMsgList.subList(1, hmbMsgList.size());
        logger.info("================= [6302] begin =============================");
        for (HmbMsg msg : hmbSubMsgList) {
            if (msg instanceof Msg033) {
                Msg033 msg033 = (Msg033) msg;

                if ("#".equals(msg033.fundActno2)) {
                    logger.info("项目户[" + msg033.fundActno1 + "] [" + msg033.fundActtype1 + "]");
                    logger.info("项目户[" + msg033.fundActno2 + "] [" + msg033.fundActtype2 + "]");
                    hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                }
            }
        }
        for (HmbMsg msg : hmbSubMsgList) {
            if (msg instanceof Msg033) {
                Msg033 msg033 = (Msg033) msg;
                if (!"#".equals(msg033.fundActno2)) {
                    logger.info("分户[" + msg033.fundActno1 + "] [" + msg033.fundActtype1 + "]");
                    logger.info("分户[" + msg033.fundActno2 + "] [" + msg033.fundActtype2 + "]");
                    hmbActinfoService.createActinfoFundByHmbMsg(msg033);
                }
            }
        }
        /* List<HmbMsg> msg033List = new ArrayList<HmbMsg>();
        for (HmbMsg msg : hmbMsgList) {
            if ("01033".equals(msg.getMsgType())) {
                Msg033 msg033 = (Msg033) msg;
                msg033List.add(msg033);
            }
        }*/
        logger.info("================= [6302] end =============================");
        return hmbSubMsgList.size();
    }
}
