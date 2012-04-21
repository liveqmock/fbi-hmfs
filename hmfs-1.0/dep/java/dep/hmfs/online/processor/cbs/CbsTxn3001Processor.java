package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA3001;
import dep.hmfs.online.processor.cbs.domain.txn.TOA3001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn3001Processor extends CbsAbstractTxnProcessor {

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA3001 tia3001 = new TIA3001();
        tia3001.body.refundApplyNo = new String(bytes, 0, 18).trim();

        TOA3001 toa3001 = null;
        // 查询汇总信息
        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia3001.body.refundApplyNo, "00005");

        if (totalRefundInfo != null) {
            toa3001 = new TOA3001();
            toa3001.body.refundApplyNo = tia3001.body.refundApplyNo;
            toa3001.body.refundAmt = String.format("%.2f", totalRefundInfo.getTxnAmt1());
            if (TxnCtlSts.SUCCESS.getCode().equals(totalRefundInfo.getTxnCtlSts())) {
                toa3001.body.refundFlag = "1";
            } else {
                toa3001.body.refundFlag = "0";
                hmbBaseService.updateMsginSts(tia3001.body.refundApplyNo, TxnCtlSts.HANDLING);
            }
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }

        return toa3001;
    }
}
