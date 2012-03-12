package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.cmb.AbstractTxnProcessor;
import dep.hmfs.online.cmb.domain.base.TIAHeader;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.base.TOAHeader;
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
public class WebMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebMsgHandleService.class);

    @Override
    public byte[] handleMessage(byte[] bytes) {

        TOAHeader toaHeader = null;
        TOA toa = null;
        TIAHeader tiaHeader = new TIAHeader();
        tiaHeader.initFields(bytes);

        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        try {
            // TODO 生成返回报文头
            toaHeader = new TOAHeader();
            toaHeader.serialNo = tiaHeader.serialNo;
            toaHeader.errorCode = "0000";
            toaHeader.txnCode = tiaHeader.txnCode;

            AbstractTxnProcessor txnProcessor = (AbstractTxnProcessor) ContainerManager.getBean("txn" + tiaHeader.txnCode + "Processor");
            toa = txnProcessor.process(tiaHeader.serialNo, datagramBytes);
        } catch (Exception e) {
            logger.error("交易处理发生异常！", e);
            toaHeader.errorCode = CbsErrorCode.SYSTEM_ERROR.getCode();
        }
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(StringUtils.rightPad(toaHeader.serialNo, 16, " "));
        strBuilder.append(toaHeader.errorCode).append(toaHeader.txnCode);
        if (toa != null) {
            strBuilder.append(toa.toString());
        }
        String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, "");

        return (totalLength + strBuilder.toString()).getBytes();
    }
}
