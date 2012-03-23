package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.processor.cmb.CmbAbstractTxnProcessor;
import dep.hmfs.online.processor.cmb.domain.base.TIAHeader;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
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
        strBuilder.append("0000");
        strBuilder.append(tiaHeader.txnCode);
        if ("1001".equals(tiaHeader.txnCode)) {
            strBuilder.append("12031900484352100001000.00         ");
            String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
            return (totalLength + strBuilder.toString()).getBytes();
        } else if ("1002".equals(tiaHeader.txnCode)) {
            strBuilder.append("1203190048435210002   张三|500.00|深圳路|98.88|110|0|100|30|11111111\n李四|500.00|深圳路|98.88|110|0|100|30|11111110");
            String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
            return (totalLength + strBuilder.toString()).getBytes();
        }
        if ("2001".equals(tiaHeader.txnCode)) {
            strBuilder.append("12031900484352100001000.00         ");
            String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
            return (totalLength + strBuilder.toString()).getBytes();
        } else if ("2002".equals(tiaHeader.txnCode)) {
            strBuilder.append("1203190048435210002   张三|500.00|深圳路|98.88|110|0|100|30|11111111\n李四|500.00|深圳路|98.88|110|0|100|30|11111110");
            String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
            return (totalLength + strBuilder.toString()).getBytes();
        }
        // ======================================
        try {
            CmbAbstractTxnProcessor txnProcessorCmb = (CmbAbstractTxnProcessor) ContainerManager.getBean("cmbTxn" + tiaHeader.txnCode + "Processor");
            toa = txnProcessorCmb.process(tiaHeader.serialNo, datagramBytes);
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
        String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");

        return (totalLength + strBuilder.toString()).getBytes();
    }
}
