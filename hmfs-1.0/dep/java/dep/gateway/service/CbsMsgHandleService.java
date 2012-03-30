package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.processor.cbs.CbsAbstractTxnProcessor;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ����2:27
 * To change this template use File | Settings | File Templates.
 */
/*
A2��ˮ��	��16λ��
A3������	��4λ��
A4��������	��4λ��
 */

@Service
public class CbsMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CbsMsgHandleService.class);

    @Override
    public byte[] handleMessage(byte[] bytes) {

        TOA toa = null;
        TIAHeader tiaHeader = new TIAHeader();
        tiaHeader.initFields(bytes);
        logger.info("�����ĳ��ȡ���" + bytes.length + "����ˮ�š���" + tiaHeader.serialNo + " �������롿��" + tiaHeader.errorCode);
        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(StringUtils.rightPad(tiaHeader.serialNo, 16, " "));
        // =======================================
        if ("debug".equalsIgnoreCase(PropertyManager.getProperty("hmfs_sys_status_flag"))) {
            logger.info("����ǰϵͳ״̬����Debug��");
            strBuilder.append("0000");
            strBuilder.append(tiaHeader.txnCode);
            if ("1001".equals(tiaHeader.txnCode)) {
                strBuilder.append("120319004843521000010.00           ");
                if("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                    strBuilder.append("120319004843521000010.00           2   ����|500.00|����·|98.88|110|0|100|30|11111111\n����|500.00|����·|98.88|110|0|100|30|11111110");
                }
            } else if ("1002".equals(tiaHeader.txnCode)) {
                strBuilder.append("1203190048435210002   ����|500.00|����·|98.88|110|0|100|30|11111111\n����|500.00|����·|98.88|110|0|100|30|11111110");
            } else if ("2001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100002.00            ");
            } else if ("3001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100003.00            ");
            } else if ("5001".equals(tiaHeader.txnCode)) {
                TIA5001 tia5001 = new TIA5001();
                tia5001.body.cbsActNo = new String(datagramBytes, 0, 30).trim();
                tia5001.body.accountBalance = new String(datagramBytes, 30, 16).trim();
                tia5001.body.txnDate = new String(datagramBytes, 46, 8).trim();

                logger.info("��ǰ̨���˺ţ�" + tia5001.body.cbsActNo);
                logger.info("��ǰ̨����" + tia5001.body.accountBalance);
                logger.info("��ǰ̨���������ڣ�" + tia5001.body.txnDate);

                // ��ȡ��ϸ����ʼ��������
                if (datagramBytes.length > 54) {
                    byte[] detailBytes = new byte[datagramBytes.length - 54];
                    System.arraycopy(datagramBytes, 54, detailBytes, 0, detailBytes.length);
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
                logger.error("�˻�������ϸ��ǰ̨����������" + tia5001.body.recordList.size());
            }
            if (true) {
                String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
                return (totalLength + strBuilder.toString()).getBytes();
            }
        }

        // ======================================
        try {
            CbsAbstractTxnProcessor txnProcessorCbs = (CbsAbstractTxnProcessor) ContainerManager.getBean("cbsTxn" + tiaHeader.txnCode + "Processor");
            toa = txnProcessorCbs.run(tiaHeader.serialNo, datagramBytes);
            strBuilder.append("0000");
        } catch (Exception e) {
            logger.error("���״������쳣��", e);
            if (e.getMessage() == null || e.getMessage().getBytes().length != 4) {
                strBuilder.append(CbsErrorCode.SYSTEM_ERROR.getCode());
            } else {
                strBuilder.append(e.getMessage());
            }
        }

        strBuilder.append(tiaHeader.txnCode);
        if (toa != null)

        {
            strBuilder.append(toa.toString());
        }

        String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");

        return (totalLength + strBuilder.toString()).

                getBytes();
    }
}
