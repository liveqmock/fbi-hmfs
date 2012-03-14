package dep.hmfs.online.service.hmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.dao.HisMsgoutLogMapper;
import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmSct;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ÏÂÎç2:35
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractHmbService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHmbService.class);

    @Resource
    protected HmSctMapper hmSctMapper;

    @Resource
    protected HisMsgoutLogMapper hisMsgoutLogMapper;

    @Resource
    protected HisMsginLogMapper hisMsginLogMapper;

    @Resource
    protected HmbTxnsnGenerator hmbTxnsnGenerator;

    protected XSocketBlockClient socketBlockClient;
    protected static String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected static int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected static int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");
    protected  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");
    protected  static String ORIG_SYS_ID =  PropertyManager.getProperty("ORIG_SYS_ID");

    protected HmbMessageFactory messageFactory = new HmbMessageFactory();

    public HmSct getAppSysStatus() {
        return hmSctMapper.selectByPrimaryKey("1");
    }
    
    protected void assembleSummaryMsg(String  txnCode, SummaryMsg msg, int submsgNum, boolean isSync) {
        msg.msgSn = hmbTxnsnGenerator.generateTxnsn(txnCode);
        msg.submsgNum = submsgNum;
        msg.sendSysId = SEND_SYS_ID;
        if (isSync) {
            msg.origSysId = ORIG_SYS_ID;
        }else{
            msg.origSysId = "00";
        }
        msg.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg.msgEndDate = "#";
    }

    protected Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) throws Exception {
        byte[] hmfsDatagram;
        try {
            socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
            hmfsDatagram = socketBlockClient.sendDataUntilRcvToHmb(bytes);
        } finally {
            socketBlockClient.close();
        }
        return messageFactory.unmarshal(hmfsDatagram);
    }

    public int insertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : hmbMsgList) {
            HisMsginLog msginLog = new HisMsginLog();
            BeanUtils.copyProperties(msginLog, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msginLog.setPkid(guid);
            msginLog.setTxnCode(txnCode);
            msginLog.setMsgProcDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            msginLog.setMsgProcTime(SystemService.formatTodayByPattern("HHmmss"));

            index++;
            if (index == 1) {
                msgSn = msginLog.getMsgSn();
            } else {
                msginLog.setMsgSn(msgSn);
            }
            msginLog.setMsgSubSn(StringUtils.leftPad("" + index, 6, '0'));
            msginLog.setTxnCtlSts(TxnCtlSts.TXN_INIT.getCode());

            hisMsginLogMapper.insert(msginLog);
        }
        return hmbMsgList.size();
    }

}
