package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-18
 * Time: ����11:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class HmbAsyncAbstractTxnProcessor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbAsyncAbstractTxnProcessor.class);

    @Override
    @Transactional
    public int run(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        process(txnCode, msgSn, hmbMsgList);
        return hmbBaseService.updateOrInsertMsginsByHmbMsgList(txnCode, hmbMsgList);
    }
}
