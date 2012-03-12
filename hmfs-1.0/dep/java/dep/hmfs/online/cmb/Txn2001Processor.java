package dep.hmfs.online.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA2001;
import dep.hmfs.online.cmb.domain.txn.TOA2001;
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
public class Txn2001Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA2001 tia2001 = new TIA2001();
        tia2001.body.drawApplyNo = new String(bytes, 0, 18).trim();

        TOA2001 toa2001 = null;
        // 查询汇总信息
        HisMsginLog totalDrawInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia2001.body.drawApplyNo, "00007");

        if (totalDrawInfo != null) {
            toa2001 = new TOA2001();
            toa2001.body.drawApplyNo = tia2001.body.drawApplyNo;
            toa2001.body.drawAmt = String.format("%.2f", totalDrawInfo.getTxnAmt1());
            toa2001.body.drawFlag = TxnCtlSts.TXN_SUCCESS.getCode().equals(totalDrawInfo.getTxnCtlSts()) ? "1" : "0";

            // 更新汇总报文和子报文交易处理状态为：处理中
            String[] drawMsgTypes = {"01041"};
            hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia2001.body.drawApplyNo, "00007", drawMsgTypes, TxnCtlSts.TXN_HANDLING);
        }

        return toa2001;
    }
}
