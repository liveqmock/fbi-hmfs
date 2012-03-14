package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HmbTxn5110Processor extends HmbAbstractTxnProcessor {
    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
