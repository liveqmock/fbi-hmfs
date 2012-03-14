package dep.hmfs.online.processor.hmb;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: обнГ7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class HmbAbstractTxnProcessor {
    public abstract byte[] process(String txnCode, List<HmbMsg> hmbMsgList);
}
