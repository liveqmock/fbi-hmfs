package dep.hmfs.online.processor.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA3001;
import dep.hmfs.online.processor.cmb.domain.txn.TOA3001;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CmbTxn3001Processor extends CmbAbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA3001 tia3001 = new TIA3001();
        tia3001.body.refundApplyNo = new String(bytes, 0, 18).trim();

        TOA3001 toa3001 = null;
        // 查询汇总信息
        HisMsginLog totalRefundInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia3001.body.refundApplyNo, "00005");

        if (totalRefundInfo != null) {
            toa3001 = new TOA3001();
            toa3001.body.refundApplyNo = tia3001.body.refundApplyNo;
            toa3001.body.refundAmt = String.format("%.2f", totalRefundInfo.getTxnAmt1());
            toa3001.body.refundFlag = TxnCtlSts.SUCCESS.getCode().equals(totalRefundInfo.getTxnCtlSts()) ? "1" : "0";

            // 更新退款汇总报文和子报文交易处理状态为：处理中
            String[] refundSubMsgTypes = {"01039", "01043"};
            hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia3001.body.refundApplyNo, "00005", refundSubMsgTypes, TxnCtlSts.HANDLING);
        }

        return toa3001;
    }
}
