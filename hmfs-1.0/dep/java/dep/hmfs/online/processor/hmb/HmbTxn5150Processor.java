package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
@Deprecated
public class HmbTxn5150Processor extends HmbAsyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5150Processor.class);

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        hmbActinfoService.createActinfoFundsByMsgList(hmbMsgList.subList(1, hmbMsgList.size()));
        return hmbMsgList.size();
    }
}
