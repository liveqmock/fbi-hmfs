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
 * Time: 上午11:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class HmbSyncAbstractTxnProcessor extends HmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HmbSyncAbstractTxnProcessor.class);

    @Override
    @Transactional
    public int run(String txnCode, String msgSn, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {

        // 同步交易报文
        // 查询已接收报文数
        int rcvdMsgInCnt = hmbBaseService.rcvedCntSyncMsginsByHmbMsgList(hmbMsgList);
        // 如果已存在报文，则视为已做业务处理
        if (rcvdMsgInCnt > 0) {
            return rcvdMsgInCnt;
        } else {
            hmbBaseService.insertMsginsByHmbMsgList(txnCode, hmbMsgList);
            return process(txnCode, msgSn, hmbMsgList);
        }
    }
}
