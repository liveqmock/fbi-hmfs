package dep.gateway.service;

import dep.ContainerManager;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.hmb.AbstractHmbTxnProcessor;
import dep.hmfs.online.hmb.domain.HmbMsg;
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
 * Time: 上午2:27
 * To change this template use File | Settings | File Templates.
 */

@Service
public class HmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CmbMsgHandleService.class);
    @Autowired
    private HmbMessageFactory mf;

    // 异步【1】
    private static final String[] ASYN_RES_TXNCODES = {"5110", "5150", "5210", "5230", "5310"};
    // 同步【0】
    private static final String[] SYN_RES_TXNCODES = {"5120", "5130", "5140", "5160", "5610",
            "6110", "6210", "6220", "7002"};

    @Override
    public byte[] handleMessage(byte[] bytes) {
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(bytes);
        String txnCode = (String) rtnMap.keySet().toArray()[0];
        logger.info("【本地服务端HmbMsgHandleService】接收到交易码：" + txnCode);
        if (Arrays.asList(ASYN_RES_TXNCODES).contains(txnCode)) {
            // 异步保存到数据库，返回9999报文
            logger.info("【本地服务端HmbMsgHandleService】处理交易方式：【异步】保存到数据库。");
            return handleAsynMessage(txnCode, rtnMap.get(txnCode));
        } else {
            logger.info("【本地服务端HmbMsgHandleService】处理交易方式：【同步】由TxnProcessor处理。");
            return handleSynMessage(txnCode, rtnMap.get(txnCode));
        }
    }

    // 【同步】保存到数据库
    public byte[] handleSynMessage(String txnCode, List<HmbMsg> hmbMsgList) {
        try {
            AbstractHmbTxnProcessor hmbTxnProcessor = (AbstractHmbTxnProcessor) ContainerManager.getBean("txn" + txnCode + "Processor");
            if (hmbTxnProcessor != null) {
                return hmbTxnProcessor.process(txnCode, hmbMsgList);
            }
        } catch (IOException e) {
            logger.error("HMFS报文同步处理异常！", e);
        }
        return null;
    }

    // 【异步】保存到数据库
    public byte[] handleAsynMessage(String txnCode, List<HmbMsg> hmbMsgList) {
        try {
            AbstractHmbTxnProcessor hmbTxnProcessor = (AbstractHmbTxnProcessor) ContainerManager.getBean("asynHmbTxnProcessor");
            if (hmbTxnProcessor != null) {
                return hmbTxnProcessor.process(txnCode, hmbMsgList);
            }
        } catch (IOException e) {
            logger.error("HMFS报文异步处理异常！", e);
        }
        return null;
    }
}
