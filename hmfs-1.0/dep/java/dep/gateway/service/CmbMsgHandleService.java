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
        logger.info("【报文长度】：" + bytes.length);
        logger.info("【流水号】：" + tiaHeader.serialNo + " 【错误码】：" + tiaHeader.errorCode);
        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(StringUtils.rightPad(tiaHeader.serialNo, 16, " "));
        try {
            CmbAbstractTxnProcessor txnProcessorCmb = (CmbAbstractTxnProcessor) ContainerManager.getBean("cmbTxn" + tiaHeader.txnCode + "Processor");
            toa = txnProcessorCmb.process(tiaHeader.serialNo, datagramBytes);
            strBuilder.append("0000");
        } catch (Exception e) {
            logger.error("交易处理发生异常！", e);
            strBuilder.append(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
        strBuilder.append(tiaHeader.txnCode);
        if (toa != null) {
            strBuilder.append(toa.toString());
        }
        String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");

        return (totalLength + strBuilder.toString()).getBytes();
    }
}
