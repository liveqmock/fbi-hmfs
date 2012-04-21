package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

// ¿Õ»§»®³ö
@Component
public class HmbTxn6311Processor extends HmbSyncSubAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6311Processor.class);

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        return hmbActinfoService.cancelActinfoFundsByMsgList(hmbMsgList.subList(1, hmbMsgList.size()));
    }
}
