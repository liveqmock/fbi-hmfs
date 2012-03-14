package dep.hmfs.online.processor.hmb;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbDetailMsgService;

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
    protected HmbBaseService hmbBaseService;
    @Resource
    protected HmbMessageFactory mf;
    @Resource
    HmbTxnsnGenerator txnsnGenerator;
    @Resource
    protected HmbDetailMsgService hmbDetailMsgService;

    public abstract byte[] process(String txnCode, List<HmbMsg> hmbMsgList);

}
