package dep.hmfs.online.processor.web;

import common.repository.hmfs.dao.HmSctMapper;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import dep.hmfs.online.service.hmb.HmbSysTxnService;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class WebAbstractHmbTxnProcessor extends WebAbstractTxnProcessor{
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbTxnProcessor.class);

    @Resource
    protected HmbMessageFactory messageFactory;

    @Resource
    protected HmbTxnsnGenerator hmbTxnsnGenerator;

    @Resource
    protected HmSctMapper hmSctMapper;

    @Resource
    protected HmbSysTxnService hmbSysTxnService;

    protected XSocketBlockClient socketBlockClient;
    protected String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");

    protected static String SEND_SYS_ID = PropertyManager.getProperty("SEND_SYS_ID");
    protected static String ORIG_SYS_ID = PropertyManager.getProperty("ORIG_SYS_ID");

    @Transactional
    public abstract String run(String request);

    protected Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) {
        byte[] hmfsDatagram;
        try {
            socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
            hmfsDatagram = socketBlockClient.sendDataUntilRcvToHmb(bytes);
            return messageFactory.unmarshal(hmfsDatagram);
        } catch (Exception e) {
            throw new RuntimeException("通讯或解包错误.", e);
        } finally {
            try {
                socketBlockClient.close();
            } catch (IOException e) {
                //
            }
        }
    }

    /**
     * @param txnCode
     * @param msg
     * @param submsgNum
     * @param isInitStart 是否是交易的第一个发起方
     */
    public void assembleSummaryMsg(String txnCode, SummaryMsg msg, int submsgNum, boolean isInitStart) {
        msg.msgSn = hmbTxnsnGenerator.generateTxnsn(txnCode);
        msg.submsgNum = submsgNum;
        msg.sendSysId = SEND_SYS_ID;
        if (isInitStart) {
            msg.origSysId = ORIG_SYS_ID;
        } else {
            msg.origSysId = "00";
        }
        msg.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg.msgEndDate = SystemService.formatTodayByPattern("yyyyMMdd");
    }

}
