package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.TxnCtlSts;
import common.enums.VouchStatus;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmVchStore;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA4001;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import dep.hmfs.online.service.hmb.TxnVouchService;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
    public TOA process(TIAHeader tiaHeader, byte[] bytes) throws InvocationTargetException, IllegalAccessException {

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

        if (VouchStatus.USED.getCode().equals(tia4001.body.billStatus)) {
            List<HmMsgIn> payInfoList = hmbClientReqService.qrySubMsgsByMsgSnAndTypes(tia4001.body.payApplyNo,
                    new String[]{"01035", "01045"});
            // ����Ƿ���ڴ����뵥��
            if (payInfoList.size() <= 0) {
                throw new RuntimeException(CbsErrorCode.QRY_NO_RECORDS.getCode());
            }
            // 2012-08-01 ����Ƿ��ѽ���ɹ�
            HmMsgIn oriMsgIn = payInfoList.get(0);
            if (!TxnCtlSts.SUCCESS.getCode().equals(oriMsgIn.getTxnCtlSts())) {
                throw new RuntimeException(CbsErrorCode.MSG_IN_SN_NOT_SUCCESS.getCode());
            }
            /*
           // 2012-10-16 һ�����뵥��Ӧ���Ʊ��ʱ���ڴ�ӡ��Ʊ�ݺ�Ʊ�ݺſ��ܲ���������ȡ���˼��
                       ��ע�⡿�ɴ˿��ܲ����������ǣ��������������һ����ʹ��Ʊ�ݵĵ������뵥�ţ����������뵥��Ʊ�ݶ�Ӧ����
           // 2012-08-01 �������뵥���Ƿ���ʹ��Ʊ��
            if (txnVouchService.isExistMsgSnVch(tia4001.body.payApplyNo)) {
                throw new RuntimeException(CbsErrorCode.MSG_IN_SN_VCH_EXIST.getCode());
            }*/
            //  2012-10-17 [���]��ϵͳ���Ѽ�¼ʹ��Ʊ���� + ��ǰʹ���� > �����뵥�ɿ��
            int usedVchCnt = txnVouchService.qryUsedVchCntByMsgsn(tia4001.body.payApplyNo);
            if ((endNo - startNo + 1) + usedVchCnt > payInfoList.size()) {
                throw new RuntimeException(CbsErrorCode.VOUCHER_OVER_LENGTH.getCode());
            }
        }

        // ���Ʊ���Ƿ���ʹ��
        for (long i = startNo; i <= endNo; i++) {
            //���Ʊ��״̬Ϊ���ϣ����������
            if(!VouchStatus.CANCEL.getCode().equals(tia4001.body.billStatus)){
                if (txnVouchService.isUsedVchNo(String.valueOf(i))) {
                    throw new RuntimeException(CbsErrorCode.VOUCHER_USED.getCode());
                }
            }
        }

        boolean isSendOver = false;
        String msgSn = hmbTxnsnGenerator.generateTxnsn("5610");
        try {
            isSendOver = hmbClientReqService.sendVouchsToHmb(msgSn, startNo, endNo, tia4001.body.payApplyNo,
                    tia4001.body.billStatus);
            if (isSendOver) {
                txnVouchService.insertVouchsByNo(msgSn, startNo, endNo, tiaHeader.serialNo,
                        tiaHeader.deptCode, tiaHeader.operCode, tia4001.body.payApplyNo,
                        tia4001.body.billStatus);

                //20150511 zhanrui
                if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                    txnVouchService.processVchUseOrCancel(tiaHeader.deptCode, VouchStatus.valueOfAlias(tia4001.body.billStatus), String.valueOf(startNo), String.valueOf(endNo),tiaHeader.operCode);
                }
            }
        } catch (Exception e) {
            logger.error("���ͱ�����������ʱ�����쳣��" + e.getMessage(), e);
            if (e.getMessage().startsWith("700")) {
                throw new RuntimeException(CbsErrorCode.NET_COMMUNICATE_ERROR.getCode());
            } else
                throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
        if (!isSendOver) {
            logger.error("Ʊ�ݹ����׷��͹��̳����쳣,ǰ̨������ˮ�ţ�" + tiaHeader.serialNo);
            throw new RuntimeException(CbsErrorCode.VOUCHER_SEND_ERROR.getCode());
        }
        return null;
    }
}
