package dep.hmfs.online.service.hmb;

import common.enums.SysCtlSts;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.dao.HisMsgoutLogMapper;
import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.dao.TmpMsginLogMapper;
import common.repository.hmfs.model.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午2:35
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbBaseService {
    private static final Logger logger = LoggerFactory.getLogger(HmbBaseService.class);

    @Resource
    protected HmSctMapper hmSctMapper;
    @Resource
    protected HisMsgoutLogMapper hisMsgoutLogMapper;
    @Resource
    protected HmbTxnsnGenerator hmbTxnsnGenerator;
    @Resource
    protected HmbMessageFactory messageFactory;
    @Resource
    protected TmpMsginLogMapper tmpMsginLogMapper;

    protected XSocketBlockClient socketBlockClient;
    protected static String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected static int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected static int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");
    protected static String SEND_SYS_ID = PropertyManager.getProperty("SEND_SYS_ID");
    protected static String ORIG_SYS_ID = PropertyManager.getProperty("ORIG_SYS_ID");

    @Autowired
    protected HisMsginLogMapper hisMsginLogMapper;

    // 根据申请单号查询汇总报文信息
    public HisMsginLog qryTotalMsgByMsgSn(String msgSn, String msgType) {

        HisMsginLogExample example = new HisMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeEqualTo(msgType)
                .andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        List<HisMsginLog> hisMsginLogList = hisMsginLogMapper.selectByExample(example);

        return hisMsginLogList.size() > 0 ? hisMsginLogList.get(0) : null;
    }

    // 根据申请单号查询所有明细
    public List<HisMsginLog> qrySubMsgsByMsgSnAndTypes(String msgSn, String[] msgTypes) {
        HisMsginLogExample example = new HisMsginLogExample();
        for (String msgType : msgTypes) {
            example.or().andMsgTypeEqualTo(msgType).andMsgSnEqualTo(msgSn)
                    .andTxnCtlStsNotEqualTo(TxnCtlSts.CANCEL.getCode());
        }
        example.setOrderByClause("MSG_SUB_SN");
        return hisMsginLogMapper.selectByExample(example);
    }

    // 更新汇总报文和子报文交易处理状态
    @Transactional
    public int updateMsginsTxnCtlStsByMsgSnAndTypes(String msgSn, String totalMsgType, String[] subMsgTypes, TxnCtlSts txnCtlSts) {
        List<HisMsginLog> msginLogList = qrySubMsgsByMsgSnAndTypes(msgSn, subMsgTypes);
        msginLogList.add(qryTotalMsgByMsgSn(msgSn, totalMsgType));
        for (HisMsginLog record : msginLogList) {
            record.setTxnCtlSts(txnCtlSts.getCode());
            hisMsginLogMapper.updateByPrimaryKey(record);
        }
        return msginLogList.size();
    }

    public HmSct getAppSysStatus() {
        return hmSctMapper.selectByPrimaryKey("1");
    }

    //TODO trans
    public void setAppSysStatus(SysCtlSts sysCtlSts) {
        HmSct hmSct = getAppSysStatus();
        hmSct.setSysSts(sysCtlSts.getCode());

        hmSct.setHostChkDt(new Date());
        hmSctMapper.updateByPrimaryKey(hmSct);
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
        msg.msgEndDate = "#";
    }

    public Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) {
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
     * 接收并保存交易报文
     *
     * @param txnCode
     * @param hmbMsgList
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException    收取报文后，若数据库中已存在此报文，且此报文仍为初始状态（未进行业务处理），则返回成功
     *                                   否则，视为已进入业务处理状态，返回失败 （撤销的报文记录已存入TMP_MSGIN_LOG）
     *                                   若目前无此报文，则新增。
     */
    @Transactional
    public int updateOrInsertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        String msgSn = "";
        HisMsginLogExample example = new HisMsginLogExample();
        example.createCriteria().andMsgSnEqualTo(msgSn).andMsgTypeLike("00%");
        List<HisMsginLog> msginLogList = hisMsginLogMapper.selectByExample(example);
        if (msginLogList.size() > 0) {
            for (HisMsginLog record : msginLogList) {
                if (TxnCtlSts.INIT.getCode().equals(record.getTxnCtlSts())) {
                    return msginLogList.size();
                } else {
                    throw new RuntimeException("该交易已进入处理流程，无法撤销");
                }
            }
        } else {
            return insertMsginsByHmbMsgList(txnCode, hmbMsgList);
        }
        return 1;
    }

    private int insertMsginsByHmbMsgList(String txnCode, List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
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
            msginLog.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            hisMsginLogMapper.insert(msginLog);
        }

        return hmbMsgList.size();
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
            msgoutLog.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            hisMsgoutLogMapper.insert(msgoutLog);
        }
        return index;
    }

}
