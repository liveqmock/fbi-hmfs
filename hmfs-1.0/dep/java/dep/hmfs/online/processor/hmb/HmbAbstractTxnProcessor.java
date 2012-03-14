package dep.hmfs.online.processor.hmb;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.service.hmb.HmbClientReqService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ÏÂÎç7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class HmbAbstractTxnProcessor {

    @Resource
    protected HmbClientReqService hmbClientReqService;
    @Resource
    protected HmbMessageFactory mf;
    @Resource
    HmbTxnsnGenerator txnsnGenerator;

    public abstract byte[] process(String txnCode, List<HmbMsg> hmbMsgList);

}
