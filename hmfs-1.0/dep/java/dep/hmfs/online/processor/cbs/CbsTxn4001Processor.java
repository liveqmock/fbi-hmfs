package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA4001;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import dep.hmfs.online.service.hmb.TxnVouchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn4001Processor extends CbsAbstractTxnProcessor {

    private static Logger logger = LoggerFactory.getLogger(CbsTxn4001Processor.class);

    @Autowired
    private TxnVouchService txnVouchService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbTxnsnGenerator hmbTxnsnGenerator;

    @Override
    @Transactional
    public TOA process(String txnSerialNo, byte[] bytes) throws InvocationTargetException, IllegalAccessException {

        TIA4001 tia4001 = new TIA4001();
        tia4001.body.billStatus = new String(bytes, 0, 1).trim();
        tia4001.body.billStartNo = new String(bytes, 1, 12).trim();
        tia4001.body.billEndNo = new String(bytes, 13, 12).trim();
        if (bytes.length > 25) {
            if (bytes.length != 43) {
                throw new RuntimeException(CbsErrorCode.DATA_ANALYSIS_ERROR.getCode());
            } else {
                tia4001.body.payApplyNo = new String(bytes, 25, 18).trim();
            }
        }
        // TODO ����Ƿ���ڴ����뵥��
        List<HmMsgIn> payInfoList = hmbClientReqService.qrySubMsgsByMsgSnAndTypes(tia4001.body.payApplyNo,
                new String[]{"00005"});

        if (payInfoList.size() <= 0) {
            throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
        }

        /*
        Ʊ��״̬	         1	    1:���ã�2:ʹ�ã�3:����
        ��ӡƱ����ʼ���	12	    Ʊ����ʼ���
        ��ӡƱ�ݽ������	12	    Ʊ�ݽ������
        �ɿ�֪ͨ����	    18	    �Ǳ����ƾ֤ʹ��ʱ��д
         */
        long startNo = 0;
        long endNo = 0;
        try {
            startNo = Long.parseLong(tia4001.body.billStartNo);
            endNo = Long.parseLong(tia4001.body.billEndNo);
        } catch (Exception e) {
            logger.error("Ʊ����ֹ�����������", e);
            throw new RuntimeException(CbsErrorCode.VOUCHER_NUM_ERROR.getCode());
        }
        if (startNo > endNo) {
            throw new RuntimeException(CbsErrorCode.VOUCHER_NUM_ERROR.getCode());
        }

        boolean isSendOver = false;
        String msgSn = hmbTxnsnGenerator.generateTxnsn("5610");
        try {
            isSendOver = hmbClientReqService.sendVouchsToHmb(msgSn, startNo, endNo, tia4001.body.payApplyNo,
                    tia4001.body.billStatus);
            if (isSendOver) {
                txnVouchService.insertVouchsByNo(msgSn, startNo, endNo, txnSerialNo, tia4001.body.payApplyNo,
                        tia4001.body.billStatus);
            }
        } catch (Exception e) {
            logger.error("���ͱ�����������ʱ�����쳣��" + e.getMessage(), e);
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
        if (!isSendOver) {
            logger.error("Ʊ�ݹ����׷��͹��̳����쳣,ǰ̨������ˮ�ţ�" + txnSerialNo);
            throw new RuntimeException(CbsErrorCode.VOUCHER_SEND_ERROR.getCode());
        }
        return null;
    }
}
