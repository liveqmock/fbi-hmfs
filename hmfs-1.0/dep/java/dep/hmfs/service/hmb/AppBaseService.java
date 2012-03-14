package dep.hmfs.service.hmb;

import common.enums.SysCtlSts;
import common.repository.hmfs.dao.HisMsgoutLogMapper;
import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.model.HmSct;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.hmb.domain.*;
import dep.util.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 基本服务：签到签退 对帐.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AppBaseService.class);

    @Resource
    private HmSctMapper hmSctMapper;

    @Resource
    private HisMsgoutLogMapper hisMsgoutLogMapper;

    private XSocketBlockClient socketBlockClient;
    private String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    private int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    private int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");
    private HmbMessageFactory mf = new HmbMessageFactory();


    public HmSct getAppSysStatus() {
        return hmSctMapper.selectByPrimaryKey("1");
    }

    /**
     * 向国土局签到
     */
    @Transactional
    public void processSignon() {
        HmSct hmSct = getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHK_SUCCESS)) {
            try {
                doHmbSignonTxn();
                hmSct.setSysSts(SysCtlSts.SIGNON.getCode());
                hmSct.setSignonDt(new Date());
                hmSctMapper.updateByPrimaryKey(hmSct);
            } catch (Exception e) {
                logger.error("签到失败。", e);
                throw new RuntimeException("签到失败。" + e.getMessage());
            }
        } else {
            throw new RuntimeException("系统初始或与国土局对帐完成后方可签到。");
        }
    }

    private void doHmbSignonTxn() throws Exception {
        boolean result = false;
        String txnCode = "7000";
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(msg001, 1);
        msg001.txnType = "1";//单笔批量？
        msg001.bizType = "#"; //????
        msg001.origTxnCode = "7000"; //TODO ????

        Msg096 msg096 = new Msg096();
        msg096.actionCode = "301"; //301:签到

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg001);
        hmbMsgList.add(msg096);
        byte[] txnBuf = mf.marshal(txnCode, hmbMsgList);
        //发送报文
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(txnBuf);

        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            //
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg002.rtnInfo);
        }
    }

    private void assembleSummaryMsg(SummaryMsg msg, int submsgNum) {
        msg.msgSn = "1111";
        msg.submsgNum = submsgNum;
        msg.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg.origSysId = PropertyManager.getProperty("ORIG_SYS_ID");
        msg.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg.msgEndDate = "#";
    }

    @Transactional
    public void processSignout() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                //appMngService.processSignout();
                hmSct.setSysSts(SysCtlSts.SIGNOUT.getCode());
                hmSct.setSignoutDt(new Date());
                hmSctMapper.updateByPrimaryKey(hmSct);
                //MessageUtil.addInfo("向国土局系统签退成功......");
            } catch (Exception e) {
                logger.error("签退失败。请重新发起签退。", e);
                throw new RuntimeException("签退失败。请重新发起签退。" + e.getMessage());
            }
        } else {
            throw new RuntimeException("系统签到完成后方可签退。");
        }
    }

    @Transactional
    public boolean processChkActBal() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())) {
            throw new RuntimeException("系统状态错误，主机对帐成功后方可进行国土局对帐操作。");
        }

        //depService.doSend

        //db

        //check db

        boolean result = false;
        //余额核对无误后，置系统状态
        if (1 == 1) {
            hmSct = getAppSysStatus();
            SysCtlSts currentSysSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
            if (currentSysSts.equals(SysCtlSts.HMB_DETLCHK_SUCCESS)) {
                hmSct.setSysSts(SysCtlSts.HMB_CHK_SUCCESS.getCode());
            } else {
                hmSct.setSysSts(SysCtlSts.HMB_BALCHK_SUCCESS.getCode());
            }
            //TODO
            hmSct.setHostChkDt(new Date());
            hmSctMapper.updateByPrimaryKey(hmSct);
            result = true;
        }

        return result;
    }

    @Transactional
    public boolean processChkActDetl() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())
                && !hmSct.getSysSts().equals(SysCtlSts.HMB_BALCHK_SUCCESS.getCode())) {
            throw new RuntimeException("系统状态错误，主机对帐成功或国土局余额对帐完成后方可进行国土局流水对帐。");
        }

        //depService.doSend

        //db

        //check db

        boolean result = false;
        //余额核对无误后，置系统状态
        if (1 == 1) {
            hmSct = getAppSysStatus();
            SysCtlSts currentSysSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
            if (currentSysSts.equals(SysCtlSts.HMB_BALCHK_SUCCESS)) {
                hmSct.setSysSts(SysCtlSts.HMB_CHK_SUCCESS.getCode());
            } else {
                hmSct.setSysSts(SysCtlSts.HMB_DETLCHK_SUCCESS.getCode());
            }
            //TODO
            hmSct.setHostChkDt(new Date());
            hmSctMapper.updateByPrimaryKey(hmSct);
            result = true;
        }

        return result;
    }


    //=============
    public Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) throws Exception {
        byte[] hmfsDatagram;
        try {
            socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
            hmfsDatagram = socketBlockClient.sendDataUntilRcvToHmb(bytes);
        } finally {
            socketBlockClient.close();
        }
        return mf.unmarshal(hmfsDatagram);
    }


}
