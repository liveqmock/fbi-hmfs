package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg005;
import dep.hmfs.online.processor.hmb.domain.Msg035;
import dep.hmfs.online.processor.hmb.domain.Msg045;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class HmbTxn5210Processor extends HmbAsyncAbstractTxnProcessor {
    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        Msg005 msg005 = (Msg005) hmbMsgList.get(0);
        BigDecimal subTxnamtSum = new BigDecimal(0);
        for (HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size())) {
            if (hmbMsg instanceof Msg035) {
                subTxnamtSum.add(((Msg035) hmbMsg).txnAmt1);
            } else if (hmbMsg instanceof Msg045) {
                subTxnamtSum.add(((Msg045) hmbMsg).txnAmt1);
            } else {
                throw new RuntimeException("5210�����ӱ��Ļ��зǷ��ӱ������[" + hmbMsg.msgType + "]");
            }
        }
        if (msg005.txnAmt1.compareTo(subTxnamtSum) != 0) {
            throw new RuntimeException("5210�����ܽ����ڸ��ӱ��Ľ��׽��֮�͡�[�ܽ��]:"
                    + String.format("%.2f", msg005.txnAmt1) + " [�����׺�]:" + String.format("%.2f", subTxnamtSum));
        }
        return 0;
    }
}
