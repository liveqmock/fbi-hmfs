package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.repository.hmfs.model.*;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.hmfs.online.processor.web.WebTxn1007003Processor;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbSysTxnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * ����.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 */
@Component
public class CbsTxn5001Processor extends CbsAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CbsTxn5001Processor.class);

    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Resource
    protected HmbSysTxnService hmbSysTxnService;

    @Autowired
    private WebTxn1007003Processor webTxn7003Processor;

    /**
     * 20120719 zhanrui
     * ��������������ʳ�������
     * �޸����񴫲����ԣ�REQUIRES_NEW  �ڶ���ʧ�ܣ��׳��쳣������£��Ա�����ʷ����ѯ��
     */
    @Override
    @Transactional
    public TOA process(String txnSerialNo, byte[] bytes) {
        /*
        �����˺�	30	ά���ʽ��ܲ����˺�
        �˻����	16	�˺ŵ������
        ����	8	yyyyMMdd
         */
        String cbsActNo = new String(bytes, 0, 30).trim();
        String accountBalance = new String(bytes, 30, 16).trim();
        String txnDate = new String(bytes, 46, 8).trim();
        logger.info("���������˺ţ�" + cbsActNo + "����������" + accountBalance + "���������������ڣ�" + txnDate);

        HmSysCtl hmSysCtl = hmbActinfoService.getSysCtl();
        String bankId = hmSysCtl.getBankId();

        clearTodayChkData(cbsActNo, txnDate, bankId);

        // ���������������
        String hmbChkResponse = null;
        try {
            hmbChkResponse = webTxn7003Processor.process(null);
        } catch (Exception e) {
            hmbChkResponse = "9999|������ֶ��˲�ƽ��";
            logger.error("������ֶ��ʴ����쳣", e);
        }

        //����������������ˮ
        try {
            appendLocalActBalRecord(cbsActNo, accountBalance, txnDate, bankId);
        } catch (Exception e) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_NOT_EXIST.getCode());
        }

        if (bytes.length > 54) {
            boolean chktxnResult = false;
            try {
                //��������������ˮ��¼
                appendCbsChkTxnRecord(bytes, cbsActNo, txnDate, bankId);
                //���ӽ��㻧������ϸ����ϸ���˱�
                appendLocalChkTxnRecord(txnDate);
                //У���������
                chktxnResult = hmbSysTxnService.verifyChkTxnData(txnDate, bankId);
            } catch (Exception e) {
                logger.error("�������", e);
                throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
            }
            if (!chktxnResult){
                throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
            }
        }else{
            logger.error("TIA���Ĵ���" + new String(bytes));
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }

        //ע�⣺���ж��ʲ��ԣ�����������������
        if (hmbChkResponse == null || !hmbChkResponse.startsWith("0000")) {
            throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
        }
        return null;
    }

    private void clearTodayChkData(String cbsActNo, String txnDate, String bankId) {
        // ɾ������ΪtxnDate�Ļ���˻������˼�¼
        hmbSysTxnService.deleteCbsChkActByDate(txnDate, cbsActNo, bankId);
        hmbSysTxnService.deleteCbsChkActByDate(txnDate, cbsActNo, "99");
        // ɾ������˺Ž�����ϸ���˼�¼
        hmbSysTxnService.deleteCbsChkTxnByDate(txnDate, cbsActNo, bankId);
        hmbSysTxnService.deleteCbsChkTxnByDate(txnDate, cbsActNo, "99");
    }

    private void appendLocalChkTxnRecord(String txnDate) {
        List<HmTxnStl> hmTxnStlList = hmbActinfoService.qryHmTxnStlForChkAct(txnDate);

        for (HmTxnStl txnStl : hmTxnStlList) {
            HmChkTxn hmChkTxn = new HmChkTxn();
            hmChkTxn.setPkid(UUID.randomUUID().toString());
            hmChkTxn.setTxnDate(txnDate);
            hmChkTxn.setSendSysId("99");
            hmChkTxn.setActno(txnStl.getCbsActno());

            hmChkTxn.setTxnamt(txnStl.getTxnAmt());
            hmChkTxn.setMsgSn(txnStl.getCbsTxnSn());
            hmChkTxn.setDcFlag(txnStl.getDcFlag());
            hmbSysTxnService.insertChkTxnWithNewTx(hmChkTxn);
        }
    }

    private void appendLocalActBalRecord(String cbsActNo, String accountBalance, String txnDate, String bankId) {
        // ���� ����ΪtxnDate�Ļ���˻������˼�¼
        HmChkAct hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(txnDate);
        hmChkAct.setSendSysId(bankId);
        hmChkAct.setActno(cbsActNo);
        hmChkAct.setActbal(new BigDecimal(accountBalance));
        hmbSysTxnService.insertChkActWithNewTx(hmChkAct);
        // DEP ���(����)�˻����
        HmActStl hmActStl = hmbActinfoService.qryHmActstlByCbsactNo(cbsActNo);
        hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(txnDate);
        hmChkAct.setSendSysId("99");
        hmChkAct.setActno(hmActStl.getCbsActno());
        hmChkAct.setActbal(hmActStl.getActBal());
        hmbSysTxnService.insertChkActWithNewTx(hmChkAct);
    }

    private void appendCbsChkTxnRecord(byte[] bytes, String cbsActNo, String txnDate, String bankId) {
        TIA5001 tia5001 = new TIA5001();
        byte[] detailBytes = new byte[bytes.length - 54];
        System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
        String detailStr = new String(detailBytes);
        String[] details = detailStr.split("\n");
        // ����������ϸ����
        for (String detail : details) {
            logger.info("====" + detail);
            String[] fields = detail.split("\\|");
            int recordCnt = fields.length / 3;
            for (int i = 0; i < recordCnt; i++) {
                TIA5001.Body.Record record = new TIA5001.Body.Record();
                record.txnSerialNo = fields[i * 3 + 0].trim();
                record.txnAmt = fields[i * 3 + 1].trim();
                record.txnType = fields[i * 3 + 2].trim();
                tia5001.body.recordList.add(record);
                HmChkTxn hmChkTxn = new HmChkTxn();
                hmChkTxn.setPkid(UUID.randomUUID().toString());
                hmChkTxn.setTxnDate(txnDate);
                hmChkTxn.setSendSysId(bankId);
                hmChkTxn.setActno(cbsActNo);
                hmChkTxn.setTxnamt(new BigDecimal(record.txnAmt));
                hmChkTxn.setMsgSn(record.txnSerialNo);
                hmChkTxn.setDcFlag(record.txnType);
                hmbSysTxnService.insertChkTxnWithNewTx(hmChkTxn);
            }
        }
    }
}
