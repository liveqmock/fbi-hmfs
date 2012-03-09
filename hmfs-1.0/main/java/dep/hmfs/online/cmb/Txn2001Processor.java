package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA2001;
import dep.hmfs.online.cmb.domain.txn.TOA2001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn2001Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA2001 tia2001 = new TIA2001();
        tia2001.body.payApplyNo = new String(bytes, 0, 18).trim();

        // TODO 从数据库查询缴款汇总信息
        TOA2001 toa2001 = new TOA2001();
        toa2001.body.payApplyNo = tia2001.body.payApplyNo;
        toa2001.body.payAmt = "123456.88";
        toa2001.body.payFlag = "0";

        return toa2001;
    }
}
