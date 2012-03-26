package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.repository.hmfs.model.HmActStl;
import common.repository.hmfs.model.HmTxnStl;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.hmfs.online.processor.web.WebTxn1007003Processor;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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

        logger.info("��ǰ̨���˺ţ�" + tia5001.body.cbsActNo);
        logger.info("��ǰ̨����" + tia5001.body.accountBalance);
        logger.info("��ǰ̨���������ڣ�" + tia5001.body.txnDate);

        HmActStl hmActStl = hmbActinfoService.qryHmActstlByCbsactNo(tia5001.body.cbsActNo);
        if (hmActStl == null) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_NOT_EXIST.getCode());
        } else if (new BigDecimal(tia5001.body.accountBalance).compareTo(hmActStl.getActBal()) != 0) {
            throw new RuntimeException(CbsErrorCode.CBS_ACT_BAL_ERROR.getCode());
        } else {
            // ��ȡ��ϸ����ʼ��������
            if (bytes.length > 54) {
                byte[] detailBytes = new byte[bytes.length - 54];
                System.arraycopy(bytes, 54, detailBytes, 0, detailBytes.length);
                String detailStr = new String(detailBytes);
                String[] details = detailStr.split("\n");
                for (String detail : details) {
                    TIA5001.Body.Record record = new TIA5001.Body.Record();
                    String[] fields = detail.split("\\|");
                    record.txnSerialNo = fields[0];
                    record.txnAmt = fields[1];
                    record.txnType = fields[2];
                    tia5001.body.recordList.add(record);
                }
            }
            List<HmTxnStl> hmTxnStlList = hmbActinfoService.qryTxnstlsByDate(tia5001.body.txnDate);
            if (hmTxnStlList.size() != tia5001.body.recordList.size()) {
                logger.error("�˻�������ϸ����һ�£������ء���������" + hmTxnStlList.size() + "��ǰ̨����������" + tia5001.body.recordList.size());
                throw new RuntimeException(CbsErrorCode.CBS_ACT_TXNS_ERROR.getCode());
            } else {
                int index = 0;
                for (TIA5001.Body.Record r : tia5001.body.recordList) {
                    logger.info("��ǰ��ҵ��ƽ̨����ˮ�ţ�" + r.txnSerialNo + " ==���׽� " + r.txnAmt + " ==���˷��� " + r.txnType);
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
                // ���ˣ����ض��˽���
                // ���������������
                String res = null;
                try {
                    res = webTxn7003Processor.process(null);
                } catch (Exception e) {
                    logger.error("�����쳣", e);
                    throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
                }
                if (webTxn7003Processor.process(null).startsWith("0000")) {
                    return null;
                } else {
                    throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
                }
                // TODO �����������ϸ���ˡ����޴˽��ס�
            }
        }
    }
}
