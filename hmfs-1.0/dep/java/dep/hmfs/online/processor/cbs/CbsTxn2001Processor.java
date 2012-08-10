package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA2001;
import dep.hmfs.online.processor.cbs.domain.txn.TOA2001;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn2001Processor extends CbsAbstractTxnProcessor {

    @Override
    public TOA process(TIAHeader tiaHeader, byte[] bytes) {
        TIA2001 tia2001 = new TIA2001();
        tia2001.body.drawApplyNo = new String(bytes, 0, 18).trim();

        TOA2001 toa2001 = null;
        // 查询汇总信息
        HmMsgIn totalDrawInfo = hmbBaseService.qryTotalMsgByMsgSn(tia2001.body.drawApplyNo, "00007");

        if (totalDrawInfo != null) {
            toa2001 = new TOA2001();
            toa2001.body.drawApplyNo = tia2001.body.drawApplyNo;
            toa2001.body.drawAmt = String.format("%.2f", totalDrawInfo.getTxnAmt1());
            if (TxnCtlSts.SUCCESS.getCode().equals(totalDrawInfo.getTxnCtlSts())) {
                toa2001.body.drawFlag = "1";
            } else {
                hmbBaseService.updateMsginSts(tia2001.body.drawApplyNo, TxnCtlSts.HANDLING);
                toa2001.body.drawFlag = "0";
            }
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }

        return toa2001;
    }
}
