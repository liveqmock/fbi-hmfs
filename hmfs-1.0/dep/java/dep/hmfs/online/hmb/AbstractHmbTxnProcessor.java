package dep.hmfs.online.hmb;

import dep.hmfs.online.hmb.domain.HmbMsg;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractHmbTxnProcessor {
    public abstract byte[] process(String txnCode, List<HmbMsg> hmbMsgList);
}
