package dep.gateway.service;

import dep.ContainerManager;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.HmbAbstractTxnProcessor;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ����2:27
 * To change this template use File | Settings | File Templates.
 */

@Service
public class HmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);
    @Autowired
    private HmbMessageFactory mf;

    // ���պ󷵻�9999����
    private static final String[] ASYN_RES_TXNCODES = {"5150", "5210", "5230", "5310"};
    // ��ͬ������
    private static final String[] SYN_RES_TXNCODES = {"5110", "5120", "5130", "5140", "5160", "5610",
            "6110", "6210", "6220", "7002"};

    @Override
    public byte[] handleMessage(byte[] bytes) {
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(bytes);
        String txnCode = (String) rtnMap.keySet().toArray()[0];
        logger.info("�����ط����HmbMsgHandleService�����յ������룺" + txnCode);
        if (Arrays.asList(ASYN_RES_TXNCODES).contains(txnCode)) {
            // �첽���浽���ݿ⣬����9999����
            logger.info("�����ط����HmbMsgHandleService�������׷�ʽ�����첽�����浽���ݿ⡣");
            return handleAsynMessage(txnCode, rtnMap.get(txnCode));
        } else {
            logger.info("�����ط����HmbMsgHandleService�������׷�ʽ����ͬ������HmbTxnProcessor����");
            return handleSynMessage(txnCode, rtnMap.get(txnCode));
        }
    }

    // ��ͬ�������浽���ݿ�
    public byte[] handleSynMessage(String txnCode, List<HmbMsg> hmbMsgList) {
        try {
            HmbAbstractTxnProcessor hmbAbstractTxnProcessor = (HmbAbstractTxnProcessor) ContainerManager.getBean("hmbTxn" + txnCode + "Processor");
            if (hmbAbstractTxnProcessor != null) {
                return hmbAbstractTxnProcessor.process(txnCode, hmbMsgList);
            }
        } catch (IOException e) {
            logger.error("HMFS����ͬ�������쳣��", e);
        }
        return null;
    }

    // ���첽�����浽���ݿ�
    public byte[] handleAsynMessage(String txnCode, List<HmbMsg> hmbMsgList) {
        try {
            HmbAbstractTxnProcessor hmbAbstractTxnProcessor = (HmbAbstractTxnProcessor) ContainerManager.getBean("hmbAsynTxnProcessor");
            if (hmbAbstractTxnProcessor != null) {
                return hmbAbstractTxnProcessor.process(txnCode, hmbMsgList);
            }
        } catch (IOException e) {
            logger.error("HMFS�����첽�����쳣��", e);
        }
        return null;
    }
}
