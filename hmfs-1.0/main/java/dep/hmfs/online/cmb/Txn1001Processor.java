package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1001;
import dep.hmfs.online.cmb.domain.txn.TOA1001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn1001Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA1001 tia1001 = new TIA1001();
        tia1001.body.payApplyNo = new String(bytes, 0, 18).trim();

        // TODO 从数据库查询缴款汇总信息
        TOA1001 toa1001 = new TOA1001();
        toa1001.body.payApplyNo = tia1001.body.payApplyNo;
        toa1001.body.payAmt = "123456.88";
        toa1001.body.payFlag = "0";

        return toa1001;
    }
}
