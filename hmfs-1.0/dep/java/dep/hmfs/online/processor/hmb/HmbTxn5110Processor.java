package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class HmbTxn5110Processor extends HmbAbstractTxnProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(HmbTxn5110Processor.class);
    
    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        try {
            insertMsginsByHmbMsgList(txnCode, hmbMsgList);

        } catch (InvocationTargetException e) {
            logger.error("5110交易处理异常！", e);
        } catch (IllegalAccessException e) {
            logger.error("5110交易处理异常！", e);
        }
        return null;
    }
}
