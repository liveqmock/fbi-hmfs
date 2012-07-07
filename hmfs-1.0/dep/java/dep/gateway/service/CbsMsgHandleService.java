package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.processor.cbs.CbsAbstractTxnProcessor;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA5001;
import dep.util.PropertyManager;
import dep.util.StringPad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
A2流水号	（16位）
A3错误码	（4位）
A4服务类型	（4位）
 */

@Service
public class CbsMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CbsMsgHandleService.class);

    @Override
    public byte[] handleMessage(byte[] bytes) {

        TOA toa = null;
        TIAHeader tiaHeader = new TIAHeader();
        tiaHeader.initFields(bytes);
        logger.info("【报文长度】：" + bytes.length + "【流水号】：" + tiaHeader.serialNo + " 【错误码】：" + tiaHeader.errorCode);
        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(StringPad.rightPad4ChineseToByteLength(tiaHeader.serialNo, 16, " "));

        // =======================================

        if ("debug".equalsIgnoreCase(PropertyManager.getProperty("hmfs_sys_status_flag"))) {
            logger.info("【当前系统状态】【debug】");
            strBuilder.append("0000");
            strBuilder.append(tiaHeader.txnCode);
            if ("1001".equals(tiaHeader.txnCode)) {
                if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {
                    strBuilder.append("120319004843521000010.00           2   " +
                            StringPad.rightPad4ChineseToByteLength("张三", 60, " ") +
                            StringPad.rightPad4ChineseToByteLength("500.00", 16, " ") +
                            StringPad.rightPad4ChineseToByteLength("深圳路", 80, " ") +
                            StringPad.rightPad4ChineseToByteLength("98.88", 16, " ") +
                            StringPad.rightPad4ChineseToByteLength("89901111", 20, " ") +
                            StringPad.rightPad4ChineseToByteLength("1", 2, " ") +
                            StringPad.rightPad4ChineseToByteLength("10000", 20, " ") +
                            StringPad.rightPad4ChineseToByteLength("30", 20, " ") +
                            StringPad.rightPad4ChineseToByteLength("119090909090", 12, " ") +
                            StringPad.rightPad4ChineseToByteLength("李四", 60, " ") +
                            StringPad.rightPad4ChineseToByteLength("500.00", 16, " ") +
                            StringPad.rightPad4ChineseToByteLength("深圳路", 80, " ") +
                            StringPad.rightPad4ChineseToByteLength("98.78", 16, " ") +
                            StringPad.rightPad4ChineseToByteLength("89901110", 20, " ") +
                            StringPad.rightPad4ChineseToByteLength("4", 2, " ") +
                            StringPad.rightPad4ChineseToByteLength("13000", 20, " ") +
                            StringPad.rightPad4ChineseToByteLength("30", 20, " ") +
                            StringPad.rightPad4ChineseToByteLength("119090909091", 12, " "));
                } else {
                    strBuilder.append("120319004843521000010.00           ");
                }
            } else if ("1002".equals(tiaHeader.txnCode)) {
                strBuilder.append("1203190048435210002   " +
                        StringPad.rightPad4ChineseToByteLength("张三", 60, " ") +
                        StringPad.rightPad4ChineseToByteLength("500.00", 16, " ") +
                        StringPad.rightPad4ChineseToByteLength("深圳路", 80, " ") +
                        StringPad.rightPad4ChineseToByteLength("98.88", 16, " ") +
                        StringPad.rightPad4ChineseToByteLength("89901111", 20, " ") +
                        StringPad.rightPad4ChineseToByteLength("1", 2, " ") +
                        StringPad.rightPad4ChineseToByteLength("10000", 20, " ") +
                        StringPad.rightPad4ChineseToByteLength("30", 20, " ") +
                        StringPad.rightPad4ChineseToByteLength("119090909090", 12, " ") +
                        StringPad.rightPad4ChineseToByteLength("李四", 60, " ") +
                        StringPad.rightPad4ChineseToByteLength("500.00", 16, " ") +
                        StringPad.rightPad4ChineseToByteLength("深圳路", 80, " ") +
                        StringPad.rightPad4ChineseToByteLength("98.78", 16, " ") +
                        StringPad.rightPad4ChineseToByteLength("89901110", 20, " ") +
                        StringPad.rightPad4ChineseToByteLength("4", 2, " ") +
                        StringPad.rightPad4ChineseToByteLength("13000", 20, " ") +
                        StringPad.rightPad4ChineseToByteLength("30", 20, " ") +
                        StringPad.rightPad4ChineseToByteLength("119090909091", 12, " "));
            } else if ("2001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100002.00            ");
            } else if ("3001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100003.00            ");
            } else if ("5001".equals(tiaHeader.txnCode)) {
                TIA5001 tia5001 = new TIA5001();
                tia5001.body.cbsActNo = new String(datagramBytes, 0, 30).trim();
                tia5001.body.accountBalance = new String(datagramBytes, 30, 16).trim();
                tia5001.body.txnDate = new String(datagramBytes, 46, 8).trim();

                logger.info("【前台】账号：" + tia5001.body.cbsActNo);
                logger.info("【前台】余额：" + tia5001.body.accountBalance);
                logger.info("【前台】交易日期：" + tia5001.body.txnDate);

                // 获取明细，开始主机对账
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
                logger.error("账户交易明细【前台】交易数：" + tia5001.body.recordList.size());
            }
            if (true) {
                String totalLength = StringPad.rightPad4ChineseToByteLength(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
                return (totalLength + strBuilder.toString()).getBytes();
            }
        }

        // =========================================================

        try {
            CbsAbstractTxnProcessor cbsTxnProcessor = (CbsAbstractTxnProcessor) ContainerManager.getBean("cbsTxn" + tiaHeader.txnCode + "Processor");
            toa = cbsTxnProcessor.run(tiaHeader.txnCode, tiaHeader.serialNo, datagramBytes);
            strBuilder.append("0000");
        } catch (Exception e) {
            logger.error("交易处理发生异常！", e);
            if (e.getMessage() == null || e.getMessage().getBytes().length != 4) {
                strBuilder.append(CbsErrorCode.SYSTEM_ERROR.getCode());
            } else {
                strBuilder.append(e.getMessage());
            }
        }

        strBuilder.append(tiaHeader.txnCode);
        if (toa != null) {
            strBuilder.append(toa.toString());
        }
        String totalLength = StringPad.rightPad4ChineseToByteLength(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
        return (totalLength + strBuilder.toString()).getBytes();
    }
}
