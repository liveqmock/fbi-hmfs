package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA1011;
import dep.hmfs.online.processor.cbs.domain.txn.TOA1011;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CbsTxn1011Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn1011Processor.class);
    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        logger.info("【报文正文长度】:" + bytes.length);
        TIA1011 tia1011 = new TIA1011();
        tia1011.body.txnApplyNo = new String(bytes, 0, 18).trim();
        logger.info("【申请单号】：" + tia1011.body.txnApplyNo);

        TOA1011 toa1011 = null;
        // 查询交款汇总信息
        HmMsgIn totalTxnInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1011.body.txnApplyNo, "00011");

        if (totalTxnInfo != null) {
            toa1011 = new TOA1011();
            toa1011.body.txnApplyNo = tia1011.body.txnApplyNo;
            toa1011.body.txnAmt = String.format("%.2f", totalTxnInfo.getTxnAmt1());
            toa1011.body.openActBankName = totalTxnInfo.getBankName();
            toa1011.body.openActBankId = totalTxnInfo.getBranchId();
            toa1011.body.payAccount = totalTxnInfo.getPayoutActno();
            toa1011.body.recvAccount = totalTxnInfo.getPayinActno();

            if (!TxnCtlSts.SUCCESS.getCode().equals(totalTxnInfo.getTxnCtlSts())) {
                toa1011.body.txnFlag = "0";
                hmbBaseService.updateMsginSts(tia1011.body.txnApplyNo, TxnCtlSts.HANDLING);
            }else {
                toa1011.body.txnFlag = "1";
            }
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }
        return toa1011;
    }
}