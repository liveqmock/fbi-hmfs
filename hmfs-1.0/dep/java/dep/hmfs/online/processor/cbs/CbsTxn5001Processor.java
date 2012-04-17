package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.repository.hmfs.model.HmActStl;
import common.repository.hmfs.model.HmChkAct;
import common.repository.hmfs.model.HmChkTxn;
import common.repository.hmfs.model.HmTxnStl;
import common.service.SystemService;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.hmfs.online.processor.web.WebTxn1007003Processor;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn5001Processor extends CbsAbstractTxnProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CbsTxn5001Processor.class);

    @Autowired
    private HmbActinfoService hmbActinfoService;
    @Autowired
    private WebTxn1007003Processor webTxn7003Processor;

    @Override
    @Transactional
    public TOA process(String txnSerialNo, byte[] bytes) {
        /*
        �����˺�	30	ά���ʽ��ܲ����˺�
        �˻����	16	�˺ŵ������
        ����	8	yyyyMMdd
         */
        TIA5001 tia5001 = new TIA5001();
        tia5001.body.cbsActNo = new String(bytes, 0, 30).trim();
        tia5001.body.accountBalance = new String(bytes, 30, 16).trim();
        tia5001.body.txnDate = new String(bytes, 46, 8).trim();

        // ����������ڲ���ϵͳ��ǰ���ڣ��򷵻ش���
        if (!SystemService.formatTodayByPattern("yyyyMMdd").equals(tia5001.body.txnDate)) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_CHK_DATE_ERROR.getCode());
        }

        logger.info("���������˺ţ�" + tia5001.body.cbsActNo);
        logger.info("����������" + tia5001.body.accountBalance);
        logger.info("���������������ڣ�" + tia5001.body.txnDate);

        // ɾ������ΪtxnDate�Ļ���˻������˼�¼
        hmbActinfoService.deleteCbsChkActByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "05");
        // ���� ����ΪtxnDate�Ļ���˻������˼�¼
        HmChkAct hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(tia5001.body.txnDate);
        hmChkAct.setSendSysId("05");
        hmChkAct.setActno(tia5001.body.cbsActNo);
        hmChkAct.setActbal(new BigDecimal(tia5001.body.accountBalance));
        hmbActinfoService.insertChkAct(hmChkAct);

        hmbActinfoService.deleteCbsChkActByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "99");
        // DEP ���(����)�˻����
        HmActStl hmActStl = hmbActinfoService.qryHmActstlByCbsactNo(tia5001.body.cbsActNo);

        if (hmActStl == null) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_NOT_EXIST.getCode());
        }

        // TODO �������Ϣ���⣬�������ս���ƽ���������
        /*else if (new BigDecimal(tia5001.body.accountBalance).compareTo(hmActStl.getActBal()) != 0) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_BAL_ERROR.getCode());
        }*/

        hmChkAct = new HmChkAct();
        hmChkAct.setPkid(UUID.randomUUID().toString());
        hmChkAct.setTxnDate(tia5001.body.txnDate);
        hmChkAct.setSendSysId("99");
        hmChkAct.setActno(hmActStl.getCbsActno());
        hmChkAct.setActbal(hmActStl.getActBal());
        hmbActinfoService.insertChkAct(hmChkAct);

        // ��ȡ��ϸ����ʼ��������
        // ɾ������˺Ž�����ϸ���˼�¼
        hmbActinfoService.deleteCbsChkTxnByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "05");
        hmbActinfoService.deleteCbsChkTxnByDate(tia5001.body.txnDate, tia5001.body.cbsActNo, "99");

        // ����ϸ����
        if (bytes.length > 54) {
            byte[] detailBytes = new byte[bytes.length - 54];
            System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
            String detailStr = new String(detailBytes);
            String[] details = detailStr.split("\n");
            // ����������ϸ����
            for (String detail : details) {
                TIA5001.Body.Record record = new TIA5001.Body.Record();
                String[] fields = detail.split("\\|");
                record.txnSerialNo = fields[0];
                record.txnAmt = fields[1];
                record.txnType = fields[2];
                tia5001.body.recordList.add(record);

                HmChkTxn hmChkTxn = new HmChkTxn();
                hmChkTxn.setPkid(UUID.randomUUID().toString());
                hmChkTxn.setTxnDate(tia5001.body.txnDate);
                hmChkTxn.setSendSysId("05");
                hmChkTxn.setActno(tia5001.body.cbsActNo);
                hmChkTxn.setTxnamt(new BigDecimal(record.txnAmt));
                hmChkTxn.setMsgSn(record.txnSerialNo);
                hmChkTxn.setDcFlag(record.txnType);
                hmbActinfoService.insertChkTxn(hmChkTxn);
            }
            // ��ѯ���㻧������ϸ����ϸ���˱�
            List<HmTxnStl> hmTxnStlList = hmbActinfoService.qryTxnstlsByDate(tia5001.body.txnDate);
            for (HmTxnStl txnStl : hmTxnStlList) {
                HmChkTxn hmChkTxn = new HmChkTxn();
                hmChkTxn.setPkid(UUID.randomUUID().toString());
                hmChkTxn.setTxnDate(tia5001.body.txnDate);
                hmChkTxn.setSendSysId("99");
                hmChkTxn.setActno(txnStl.getCbsActno());
                hmChkTxn.setTxnamt(txnStl.getTxnAmt());
                hmChkTxn.setMsgSn(txnStl.getTxnSn());
                hmChkTxn.setDcFlag(txnStl.getDcFlag());
                hmbActinfoService.insertChkTxn(hmChkTxn);
            }

            // ��ϸ����
            if (hmTxnStlList.size() != tia5001.body.recordList.size()) {
                logger.error("�˻�������ϸ����һ�£������ء���������" + hmTxnStlList.size() + "����������������" + tia5001.body.recordList.size());
                throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
            }

            int index = 0;
            for (TIA5001.Body.Record r : tia5001.body.recordList) {
                logger.info("�������ˡ���ˮ�ţ�" + r.txnSerialNo + " ==���׽� " + r.txnAmt + " ==���˷��� " + r.txnType);
                HmTxnStl hmTxnStl = hmTxnStlList.get(index);
                logger.info("�����ػ���˻�����ˮ�ţ�" + hmTxnStl.getTxnSn() + " ==���׽� " + hmTxnStl.getTxnAmt() +
                        " ==���˷��� " + hmTxnStl.getDcFlag());

                if (!r.txnSerialNo.equals(hmTxnStl.getTxnSn())
                        || hmTxnStl.getTxnAmt().compareTo(new BigDecimal(r.txnAmt)) != 0
                        || !r.txnType.equals(hmTxnStl.getDcFlag())) {
                    logger.error("�˻�������ϸ���ݲ�һ�£�");
                    throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
                }
                index++;
            }
            // ����-���ض��˽���
        }

        // ���������������
        // TODO �����������ϸ���ˡ����޴˽��ס�
        if (true) {
            return null;
        }
        String res = null;
        try {
            res = webTxn7003Processor.process(null);
        } catch (Exception e) {
            logger.error("�����쳣", e);
            throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
        }
        if (res != null && res.startsWith("0000")) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
        }
    }
}
