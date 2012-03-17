package dep.hmfs.online.processor.cmb;

import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.service.hmb.HmbBaseService;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class CmbAbstractTxnProcessor {

    @Resource
    protected HmbBaseService hmbBaseService;

    public abstract TOA process(String txnSerialNo, byte[] bytes) throws Exception;
}
