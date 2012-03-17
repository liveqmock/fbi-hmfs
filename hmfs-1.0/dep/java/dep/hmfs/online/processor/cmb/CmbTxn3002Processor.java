package dep.hmfs.online.processor.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import dep.hmfs.online.service.BookkeepingService;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA3002;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import dep.hmfs.online.service.cbs.CbsTxnCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CmbTxn3002Processor extends CmbAbstractTxnProcessor {

    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private CbsTxnCheckService cbsTxnCheckService;
    @Autowired
    private HmbClientReqService hmbClientReqService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA3002 tia3002 = new TIA3002();
        tia3002.body.refundApplyNo = new String(bytes, 0, 18).trim();
        tia3002.body.refundAmt = new String(bytes, 18, 16).trim();

        String[] refundSubMsgTypes = {"01039", "01043"};

        HisMsginLog totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia3002.body.refundApplyNo, "00005");
        // 查询交易子报文记录
        List<HisMsginLog> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia3002.body.refundApplyNo, refundSubMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (cbsTxnCheckService.checkMsginTxnCtlSts(totalRefundInfo, fundInfoList, new BigDecimal(tia3002.body.refundAmt))) {
            // 退款交易。
            return handleRefundTxn(txnSerialNo, tia3002, totalRefundInfo, refundSubMsgTypes, fundInfoList);
        } else {
            // 交易状态已经成功，直接生成成功报文到业务平台
            return null;
        }
    }

    /*
      支取交易。
    */
    @Transactional
    private TOA handleRefundTxn(String cbsSerialNo, TIA3002 tia3002, HisMsginLog totalMsginLog, String[] subMsgTypes, List<HisMsginLog> payInfoList) throws Exception {

        // 会计账号记账
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia3002.body.refundAmt), DCFlagCode.TXN_OUT.getCode());
        // 批量核算户账户信息更新
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_OUT.getCode());

        hmbBaseService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia3002.body.refundApplyNo, "00005", subMsgTypes, TxnCtlSts.SUCCESS);

        // 5230 退款子报文序号
        String[] payMsgTypes = {"01039", "01043", "01033", "01051"};
        List<HisMsginLog> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(totalMsginLog.getMsgSn(), payMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalMsginLog.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalMsginLog), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException("【3002发送报文至房管局交易失败！】");
        }

    }


}
