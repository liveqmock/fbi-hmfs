package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA2003;
import dep.hmfs.online.cmb.domain.txn.TOA2003;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn2003Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(byte[] bytes) {
        TIA2003 tia2003 = new TIA2003();
        tia2003.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2003.body.drawAmt = new String(bytes, 18, 16).trim();
        tia2003.body.drawSerialNo = new String(bytes, 34, 16).trim();

        // TODO 从数据库查询缴款汇总信息
        TOA2003 toa2003 = new TOA2003();
        toa2003.body.drawApplyNo = tia2003.body.drawApplyNo;
        toa2003.body.drawSerialNo = tia2003.body.drawSerialNo;
        return toa2003;
    }
}
