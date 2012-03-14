package dep.hmfs.online.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA3001;
import dep.hmfs.online.cmb.domain.txn.TOA3001;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn3001Processor extends AbstractCmbTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) {
        TIA3001 tia3001 = new TIA3001();
        tia3001.body.refundApplyNo = new String(bytes, 0, 18).trim();

        TOA3001 toa3001 = null;
        // ��ѯ������Ϣ
        HisMsginLog totalRefundInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia3001.body.refundApplyNo, "00005");

        if (totalRefundInfo != null) {
            toa3001 = new TOA3001();
            toa3001.body.refundApplyNo = tia3001.body.refundApplyNo;
            toa3001.body.refundAmt = String.format("%.2f", totalRefundInfo.getTxnAmt1());
            toa3001.body.refundFlag = TxnCtlSts.TXN_SUCCESS.getCode().equals(totalRefundInfo.getTxnCtlSts()) ? "1" : "0";

            // �����˿���ܱ��ĺ��ӱ��Ľ��״���״̬Ϊ��������
            String[] refundSubMsgTypes = {"01039", "01043"};
            hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia3001.body.refundApplyNo, "00005", refundSubMsgTypes, TxnCtlSts.TXN_HANDLING);
        }

        return toa3001;
    }
}
