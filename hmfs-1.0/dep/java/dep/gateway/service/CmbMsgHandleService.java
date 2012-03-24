package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.processor.cmb.CmbAbstractTxnProcessor;
import dep.hmfs.online.processor.cmb.domain.base.TIAHeader;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA5001;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
 */
/*
A2流水号	（16位）
A3错误码	（4位）
A4服务类型	（4位）
 */

@Service
public class CmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);

    @Override
    public byte[] handleMessage(byte[] bytes) {

        TOA toa = null;
        TIAHeader tiaHeader = new TIAHeader();
        tiaHeader.initFields(bytes);
        logger.info("【报文长度】：" + bytes.length + "【流水号】：" + tiaHeader.serialNo + " 【错误码】：" + tiaHeader.errorCode);
        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(StringUtils.rightPad(tiaHeader.serialNo, 16, " "));
        // =======================================
        if ("debug".equalsIgnoreCase(PropertyManager.getProperty("hmfs_sys_status_flag"))) {
            logger.info("【当前系统状态】【Debug】");
            strBuilder.append("0000");
            strBuilder.append(tiaHeader.txnCode);
            if ("1001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100001000.00         ");
            } else if ("1002".equals(tiaHeader.txnCode)) {
                strBuilder.append("1203190048435210002   张三|500.00|深圳路|98.88|110|0|100|30|11111111\n李四|500.00|深圳路|98.88|110|0|100|30|11111110");
            } else if ("2001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100002000.00         ");
            } else if ("3001".equals(tiaHeader.txnCode)) {
                strBuilder.append("12031900484352100003000.00         ");
            } else if ("5001".equals(tiaHeader.txnCode)) {
                TIA5001 tia5001 = new TIA5001();
                tia5001.body.cbsActNo = new String(bytes, 0, 30).trim();
                tia5001.body.accountBalance = new String(bytes, 30, 16).trim();
                tia5001.body.txnDate = new String(bytes, 46, 8).trim();

                logger.info("【前台】账号：" + tia5001.body.cbsActNo);
                logger.info("【前台】余额：" + tia5001.body.accountBalance);
                logger.info("【前台】交易日期：" + tia5001.body.txnDate);

                // 获取明细，开始主机对账
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
                logger.error("账户交易明细【前台】交易数：" + tia5001.body.recordList.size());
            }
            if (true) {
                String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
                return (totalLength + strBuilder.toString()).getBytes();
            }
        }

        // ======================================
        try

        {
            CmbAbstractTxnProcessor txnProcessorCmb = (CmbAbstractTxnProcessor) ContainerManager.getBean("cmbTxn" + tiaHeader.txnCode + "Processor");
            toa = txnProcessorCmb.process(tiaHeader.serialNo, datagramBytes);
            strBuilder.append("0000");
        } catch (
                Exception e
                )

        {
            logger.error("交易处理发生异常！", e);
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
