package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.processor.cbs.CbsAbstractTxnProcessor;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.util.PropertyManager;
import dep.util.StringPad;
import org.apache.commons.lang.StringUtils;
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

        byte[] datagramBytes = new byte[bytes.length - 45];
        System.arraycopy(bytes, 45, datagramBytes, 0, datagramBytes.length);

        StringBuilder strBuilder = new StringBuilder();
//        strBuilder.append(StringPad.rightPad4ChineseToByteLength(tiaHeader.serialNo, 16, " "));
        strBuilder.append(StringUtils.rightPad(tiaHeader.serialNo, 16, " "));

        // ========================================================
        // debug
        if ("debug".equalsIgnoreCase(PropertyManager.getProperty("hmfs_sys_status_flag"))) {
            return DebugDatagram.debug(tiaHeader, strBuilder, datagramBytes);
        }

        // =========================================================

        try {
            CbsAbstractTxnProcessor cbsTxnProcessor = (CbsAbstractTxnProcessor) ContainerManager.getBean("cbsTxn" + tiaHeader.txnCode + "Processor");
            toa = cbsTxnProcessor.run(tiaHeader, datagramBytes);
            strBuilder.append("0000");
        } catch (Exception e) {
            if (e.getMessage() == null || e.getMessage().getBytes().length != 4) {
                logger.error("交易处理发生异常！", e);
                if (e.getMessage().startsWith("700")) {
                    throw new RuntimeException(CbsErrorCode.NET_COMMUNICATE_ERROR.getCode());
                } else
                    strBuilder.append(CbsErrorCode.SYSTEM_ERROR.getCode());
            } else {
                strBuilder.append(e.getMessage());
            }
        }

        strBuilder.append(tiaHeader.txnCode);
        strBuilder.append(tiaHeader.deptCode);
        strBuilder.append(tiaHeader.operCode);

        if (toa != null) {
            strBuilder.append(toa.toString());
        }
        String totalLength = StringPad.rightPad4ChineseToByteLength(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, " ");
        return (totalLength + strBuilder.toString()).getBytes();
    }
}
