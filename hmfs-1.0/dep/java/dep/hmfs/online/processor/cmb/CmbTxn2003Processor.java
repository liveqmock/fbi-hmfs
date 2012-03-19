package dep.hmfs.online.processor.cmb;

import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA2003;
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
public class CmbTxn2003Processor extends CmbAbstractTxnProcessor {
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA2003 tia2003 = new TIA2003();
        tia2003.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2003.body.drawAmt = new String(bytes, 18, 16).trim();

        return null;
    }
}
