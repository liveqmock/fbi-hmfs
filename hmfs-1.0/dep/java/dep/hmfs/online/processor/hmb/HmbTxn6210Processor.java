package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg009;
import dep.hmfs.online.processor.hmb.domain.Msg033;
import dep.hmfs.online.service.cbs.BookkeepingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

/**
 * ��Ŀ��ֺϲ�
 */
@Component
public class HmbTxn6210Processor extends HmbSyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6210Processor.class);

    @Autowired
    protected BookkeepingService bookkeepingService;

    @Override
    @Transactional
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        try {
            return splitFundActinfo(txnCode, hmbMsgList);
        } catch (Exception e) {
            logger.error("��Ŀ��ֺϲ����ִ���.", e);
            throw new RuntimeException("��Ŀ��ֺϲ����ִ���.", e);
        }
    }

    public int splitFundActinfo(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException, ParseException {
        //���º��㻧��Ϣ
        for (HmbMsg hmbMsg : hmbMsgList.subList(1, hmbMsgList.size())) {
            hmbActinfoService.op145changeFundActinfo((Msg033) hmbMsg);
        }

        Msg009 msg009 = (Msg009) hmbMsgList.get(0);
        String payOutActno = msg009.fundActno1;
        String payInActno = msg009.payinCbsActno;
        String msgSn = msg009.getMsgSn();
        BigDecimal amt = msg009.txnAmt1;

        //����ˮ����
        bookkeepingService.fundActBookkeeping(msgSn, payOutActno, amt, "D", "145", "145");
        bookkeepingService.fundActBookkeeping(msgSn, payInActno, amt, "C", "145", "145");
        return hmbMsgList.size() - 1;
    }

}
