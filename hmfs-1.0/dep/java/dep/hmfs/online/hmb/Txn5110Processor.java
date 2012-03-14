package dep.hmfs.online.hmb;

import dep.hmfs.online.hmb.domain.HmbMsg;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Txn5110Processor extends AbstractHmbTxnProcessor{
    @Override
    public byte[] process(String txnCode, List<HmbMsg> hmbMsgList) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
