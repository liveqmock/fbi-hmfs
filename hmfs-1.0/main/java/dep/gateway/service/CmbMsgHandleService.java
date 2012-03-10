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
 * Time: ����2:27
 * To change this template use File | Settings | File Templates.
 */
/*
A2��ˮ��	��16λ��
A3������	��4λ��
A4��������	��4λ��
A5�����	��4λ��
A6���һ����־	��1λ��
 */

@Service
public class CmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);

    public CmbMsgHandleService() {

    }

    @Override
    public byte[] handleMessage(byte[] bytes) {

        TOAHeader toaHeader = null;
        TOA toa = null;
        TIAHeader tiaHeader = new TIAHeader();
        tiaHeader.initFields(bytes);

        byte[] datagramBytes = new byte[bytes.length - 24];
        System.arraycopy(bytes, 24, datagramBytes, 0, datagramBytes.length);

        try {
            // ���ɷ��ر���ͷ
            toaHeader = new TOAHeader();
            toaHeader.serialNo = tiaHeader.serialNo;
            toaHeader.errorCode = "0000";
            toaHeader.txnCode = tiaHeader.txnCode;

            AbstractTxnProcessor txnProcessor = (AbstractTxnProcessor) ContainerManager.getBean("txn" + tiaHeader.txnCode + "Processor");
            toa = txnProcessor.process(datagramBytes);
        } catch (Exception e) {
            logger.error("���״������쳣��", e);
            toaHeader.errorCode = CbsErrorCode.SYSTEM_ERROR.getCode();
        }
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(StringUtils.rightPad(toaHeader.serialNo, 18, " "));
        strBuilder.append(toaHeader.errorCode).append(toaHeader.txnCode);
        if (toa != null) {
            strBuilder.append(toa.toString());
        }
        String totalLength = StringUtils.rightPad(String.valueOf(strBuilder.toString().getBytes().length + 6), 6, "");

        return (totalLength + strBuilder.toString()).getBytes();
    }
}
