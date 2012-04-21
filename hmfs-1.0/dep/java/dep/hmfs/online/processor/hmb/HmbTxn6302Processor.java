package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg033;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
public class HmbTxn6302Processor extends HmbAsyncAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbTxn6302Processor.class);

    @Override
    public int process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        List<HmbMsg> msg033List = new ArrayList<HmbMsg>();
        for(HmbMsg msg : hmbMsgList) {
            if(msg instanceof Msg033) {
                Msg033 msg033 = (Msg033)msg;
                msg033List.add(msg033);
            }
        }
        return hmbActinfoService.createActFundsByMsgList(msg033List);
    }
}
