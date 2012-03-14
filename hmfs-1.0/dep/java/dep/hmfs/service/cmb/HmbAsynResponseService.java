package dep.hmfs.service.cmb;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsgoutLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HisMsgoutLog;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.Msg006;
import dep.hmfs.online.hmb.domain.Msg008;
import dep.hmfs.online.hmb.domain.Msg100;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-13
 * Time: 下午8:52
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbAsynResponseService {

    private XSocketBlockClient socketBlockClient;
    private String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    private int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    private int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");
    @Autowired
    private HmbMessageFactory mf;

    @Autowired
    private HisMsgoutLogMapper hisMsgoutLogMapper;

    public Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) throws Exception {
        socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
        byte[] hmfsDatagram = socketBlockClient.sendDataUntilRcv(bytes, 7);
        return mf.unmarshal(hmfsDatagram);
    }

    // 与房管局通信
    public boolean communicateWithHmb(String txnCode, HmbMsg totalHmbMsg, List<HisMsginLog> msginLogList) throws Exception {

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(totalHmbMsg);
        for (HisMsginLog msginLog : msginLogList) {
            HmbMsg detailMsg = (HmbMsg) Class.forName(HmbMsg.class.getPackage().getName()
                    + ".Msg" + msginLog.getMsgType().substring(2)).newInstance();
            BeanUtils.copyProperties(detailMsg, msginLog);
            hmbMsgList.add(detailMsg);
        }
        Map<String, List<HmbMsg>> outMap = new HashMap<String, List<HmbMsg>>();
        outMap.put(txnCode, hmbMsgList);
        saveMsgoutLogByMap(outMap);
        byte[] txnBuf = mf.marshal(txnCode, hmbMsgList);
        Map<String, List<HmbMsg>> rtnMsgMap = sendDataUntilRcv(txnBuf);
        Msg100 msg100 = (Msg100) rtnMsgMap.get("9999").get(0);
        return "00".equals(msg100.getRtnInfoCode());
    }

    public int saveMsgoutLogByMap(Map<String, List<HmbMsg>> rtnMap) throws InvocationTargetException, IllegalAccessException {
        int index = 0;
        String msgSn = "";
        String txnCode = rtnMap.keySet().iterator().next();
        for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
            HisMsgoutLog msgoutLog = new HisMsgoutLog();
            BeanUtils.copyProperties(msgoutLog, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msgoutLog.setPkid(guid);
            msgoutLog.setTxnCode(txnCode);
            msgoutLog.setMsgProcDate(SystemService.formatTodayByPattern("yyyyMMdd"));
            msgoutLog.setMsgProcTime(SystemService.formatTodayByPattern("HHmmss"));

            index++;
            if (index == 1) {
                msgSn = msgoutLog.getMsgSn();
            } else {
                msgoutLog.setMsgSn(msgSn);
            }
            msgoutLog.setMsgSubSn(StringUtils.leftPad("" + index, 6, '0'));
            msgoutLog.setTxnCtlSts(TxnCtlSts.TXN_INIT.getCode());

            hisMsgoutLogMapper.insert(msgoutLog);
        }
        return index;
    }

    public Msg006 createMsg006ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg006 msg006 = new Msg006();
        BeanUtils.copyProperties(msg006, msginLog);
        // TODO 报文编号生成
        msg006.msgSn = "#";
        msg006.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg006.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg006.setRtnInfoCode("00");
        msg006.setRtnInfo("申请编号【" + msginLog.getMsgSn() + "】交易成功");
        return msg006;
    }

    public Msg008 createMsg008ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg008 msg008 = new Msg008();
        BeanUtils.copyProperties(msg008, msginLog);
        // TODO 报文编号生成
        msg008.setMsgSn("#");
        msg008.setSendSysId(PropertyManager.getProperty("SEND_SYS_ID"));
        msg008.setMsgDt(SystemService.formatTodayByPattern("yyyyMMddHHmmss"));
        msg008.setRtnInfoCode("00");
        msg008.setRtnInfo("申请编号【" + msginLog.getMsgSn() + "】交易成功");
        return msg008;
    }
}
