package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg099;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class HmbTxn7002Processor extends HmbSyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn7002Processor.class);
    private static final String[] CAN_CANCEL_CODES = {"01035", "01039", "01041", "01043", "01045"};

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        Msg099 msg099 = (Msg099) hmbMsgList.get(1);
        return hmbBaseService.cancelMsginsByMsgSnAndTypes(msg099.origMsgSn, CAN_CANCEL_CODES);
    }
}
