package dep.gateway.service;

import dep.ContainerManager;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.HmbAbstractTxnProcessor;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    //private static final String[] ASYN_RES_TXNCODES = {"5210", "5310"};
    // ��ͬ������
    //private static final String[] SYN_RES_TXNCODES = {"5110", "5120", "5130", "5140", "5150",
    //"5160", "6110", "6210", "6220", "7002"};

    @Override
    public byte[] handleMessage(byte[] bytes) {
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(bytes);
        String txnCode = (String) rtnMap.keySet().toArray()[0];
        logger.info("�����ط����HmbMsgHandleService�����յ������룺" + txnCode);
        logger.info("�����ط����HmbMsgHandleService�����յ����ܱ��ĺ��ӱ���������" + rtnMap.get(txnCode).size());
        logger.info("�����ط����HmbMsgHandleService�����յ����ܱ������ͣ�" + rtnMap.get(txnCode).get(0).getMsgType());
        String msgSn = ((SummaryMsg) rtnMap.get(txnCode).get(0)).msgSn;
        HmbAbstractTxnProcessor hmbAbstractTxnProcessor = null;
        try {
            hmbAbstractTxnProcessor = (HmbAbstractTxnProcessor) ContainerManager.getBean("hmbTxn" + txnCode + "Processor");
        } catch (IOException e) {
            logger.error("HMFS���Ĵ����쳣��", e);
        }
        return hmbAbstractTxnProcessor == null ? null : hmbAbstractTxnProcessor.process(txnCode, msgSn, rtnMap.get(txnCode));

    }
}
