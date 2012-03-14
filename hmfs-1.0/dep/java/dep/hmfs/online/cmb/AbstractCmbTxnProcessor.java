package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: обнГ7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCmbTxnProcessor {

    public abstract TOA process(String txnSerialNo, byte[] bytes) throws Exception;
}
