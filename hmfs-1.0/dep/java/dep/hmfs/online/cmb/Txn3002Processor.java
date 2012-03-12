package dep.hmfs.online.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA3002;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn3002Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA3002 tia3002 = new TIA3002();
        tia3002.body.refundApplyNo = new String(bytes, 0, 18).trim();
        tia3002.body.refundAmt = new String(bytes, 18, 16).trim();
        tia3002.body.txnSerialNo = new String(bytes, 34, 16).trim();

        // TODO 退款业务 交易成功则返回空报文体
        HisMsginLog totalRefundInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia3002.body.refundApplyNo, "00005");
        if (!(totalRefundInfo.getTxnAmt1().compareTo(new BigDecimal(tia3002.body.refundAmt)) == 0)) {
            throw new RuntimeException("实际退款金额和应退款金额不一致！");
        } else {
            // TODO 发送至房管局并解析返回结果
            // TODO 成功则更新本地报文交易处理状态
            String[] refundSubMsgTypes = {"01039", "01043"};
            hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia3002.body.refundApplyNo, "00005", refundSubMsgTypes, TxnCtlSts.TXN_SUCCESS);

        }
        return null;
    }
}
