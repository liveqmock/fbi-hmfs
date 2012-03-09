package dep.hmfs.online.cmb;

import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA4001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn4001Processor extends AbstractTxnProcessor {
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA4001 tia4001 = new TIA4001();
        tia4001.body.billStatus = new String(bytes, 0, 1).trim();
        tia4001.body.billStartNo = new String(bytes, 1, 12).trim();
        tia4001.body.billStartNo = new String(bytes, 13, 12).trim();
        if(bytes.length == 43) {
            tia4001.body.payApplyNo = new String(bytes, 25, 18);
        }
        // TODO 票据管理业务 交易成功则返回空报文体

        return null;
    }
}
