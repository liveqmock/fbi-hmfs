package dep.hmfs.online.processor.cbs;

import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA2003;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
// TODO 支取冲正--待定
@Component
@Deprecated
public class CbsTxn2003Processor extends CbsAbstractTxnProcessor {
    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) {
        TIA2003 tia2003 = new TIA2003();
        tia2003.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2003.body.drawAmt = new String(bytes, 18, 16).trim();

        return null;
    }
}
