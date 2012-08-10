package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA2011;
import dep.hmfs.online.processor.cbs.domain.txn.TOA2011;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn2011Processor extends CbsAbstractTxnProcessor {

    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) {
        TIA2011 tia2011 = new TIA2011();
        tia2011.body.refundApplyNo = new String(bytes, 0, 18).trim();

        TOA2011 toa2011 = null;
        // 查询汇总信息
        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia2011.body.refundApplyNo, "00011");

        if (totalRefundInfo != null) {
            toa2011 = new TOA2011();
            toa2011.body.txnApplyNo = tia2011.body.refundApplyNo;
            toa2011.body.txnAmt = String.format("%.2f", totalRefundInfo.getTxnAmt1());

            if(TxnCtlSts.SUCCESS.getCode().equals(totalRefundInfo.getTxnCtlSts())) {
                toa2011.body.txnFlag = "1";
            } else {
                toa2011.body.txnFlag = "0";
                hmbBaseService.updateMsginSts(tia2011.body.refundApplyNo, TxnCtlSts.HANDLING);
            }
            //String[] refundSubMsgTypes = {"01039", "01043"};
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }

        return toa2011;
    }
}
