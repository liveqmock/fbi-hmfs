package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA3001;
import dep.hmfs.online.cmb.domain.txn.TOA3001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn3001Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(byte[] bytes) {
        TIA3001 tia3001 = new TIA3001();
        tia3001.body.refundApplyNo = new String(bytes, 0, 18).trim();

        // TODO 从数据库查询缴款汇总信息
        TOA3001 toa3001 = new TOA3001();
        toa3001.body.refundApplyNo = tia3001.body.refundApplyNo;
        toa3001.body.refundAmt = "123456.88";
        toa3001.body.refundFlag = "0";

        return toa3001;
    }
}
