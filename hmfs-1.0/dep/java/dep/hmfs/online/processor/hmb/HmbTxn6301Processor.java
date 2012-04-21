package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class HmbTxn6301Processor extends HmbSyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6301Processor.class);

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {

        Msg011 msg011 = (Msg011) hmbMsgList.get(0);
        BigDecimal subTxnamtSum = new BigDecimal(0.0);
        for (HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size())) {
            if (hmbMsg instanceof Msg039) {
                subTxnamtSum.add(((Msg039) hmbMsg).actBal);
            } else if (hmbMsg instanceof Msg051) {
            } else {
                throw new RuntimeException("5210交易子报文还有非法子报文序号[" + hmbMsg.msgType + "]");
            }
        }
        if (msg011.txnAmt1.compareTo(subTxnamtSum) != 0) {
            throw new RuntimeException("5210交易总金额不等于各子报文交易金额之和。[总金额]:"
                    + String.format("%.2f", msg011.txnAmt1) + " [各交易和]:" + String.format("%.2f", subTxnamtSum));
        }
        return 0;
    }
}
