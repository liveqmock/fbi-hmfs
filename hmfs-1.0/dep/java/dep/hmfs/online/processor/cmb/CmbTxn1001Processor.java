package dep.hmfs.online.processor.cmb;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA1001;
import dep.hmfs.online.processor.cmb.domain.txn.TOA1001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CmbTxn1001Processor extends CmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CmbTxn1001Processor.class);

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        logger.info("���������ĳ��ȡ�:" + bytes.length);
        TIA1001 tia1001 = new TIA1001();
        tia1001.body.payApplyNo = new String(bytes, 0, 18).trim();
        logger.info("�����뵥�š���" + tia1001.body.payApplyNo);

        TOA1001 toa1001 = null;
        // ��ѯ���������Ϣ
        HmMsgIn totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1001.body.payApplyNo, "00005");

        if (totalPayInfo != null) {
            toa1001 = new TOA1001();
            toa1001.body.payApplyNo = tia1001.body.payApplyNo;
            toa1001.body.payAmt = String.format("%.2f", totalPayInfo.getTxnAmt1());
            // ���½��������Ϣ����ϸ��Ϣ״̬Ϊ�������� -- ������ɺ󽻿���Ϣ���ɳ���
            // String[] payMsgTypes = {"01035", "01045"};
            if (!TxnCtlSts.SUCCESS.getCode().equals(totalPayInfo.getTxnCtlSts())) {
                toa1001.body.payFlag = "0";
                hmbBaseService.updatePayMsginsTxnCtlStsByMsgSn(tia1001.body.payApplyNo, TxnCtlSts.HANDLING);
            } else {
                toa1001.body.payFlag = "1";
            }
        } else {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }
        return toa1001;
    }
}