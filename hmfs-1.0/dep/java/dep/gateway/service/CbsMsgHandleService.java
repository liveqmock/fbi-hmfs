package dep.gateway.service;

import common.enums.CbsErrorCode;
import dep.ContainerManager;
import dep.hmfs.online.processor.cbs.CbsAbstractTxnProcessor;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.util.PropertyManager;
import dep.util.StringPad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        strBuilder.append(StringPad.rightPad4ChineseToByteLength(tiaHeader.serialNo, 16, " "));

        // ========================================================
        // debug
        if ("debug".equalsIgnoreCase(PropertyManager.getProperty("hmfs_sys_status_flag"))) {
            return DebugDatagram.debug(tiaHeader, strBuilder, datagramBytes);
        }

        // =========================================================

        try {
            CbsAbstractTxnProcessor cbsTxnProcessor = (CbsAbstractTxnProcessor) ContainerManager.getBean("cbsTxn" + tiaHeader.txnCode + "Processor");
            toa = cbsTxnProcessor.run(tiaHeader.txnCode, tiaHeader.serialNo, datagramBytes);
            strBuilder.append("0000");
        } catch (Exception e) {
            if (e.getMessage() == null || e.getMessage().getBytes().length != 4) {
                logger.error("���״������쳣��", e);
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
