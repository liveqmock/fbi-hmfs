package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTxnProcessor {
    public abstract TOA process(byte[] bytes);
}
