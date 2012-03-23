package dep.hmfs.online.service.hmb;

import common.enums.VouchStatus;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoFund;
import common.service.SystemService;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-13
 * Time: 下午8:52
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbClientReqService extends HmbBaseService {

    protected XSocketBlockClient socketBlockClient;
    protected static String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    protected static int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    protected static int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");

    @Autowired
    private HmbActinfoService hmbActinfoService;

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
        byte[] txnBuf = messageFactory.marshal(txnCode, hmbMsgList);
        Map<String, List<HmbMsg>> rtnMsgMap = sendDataUntilRcv(txnBuf);
        List<HmbMsg> rtnMsgList = rtnMsgMap.get("9999");
        // 重复发送时，返回发送的报文 rtnMsgList应为null
        if (rtnMsgList == null) {
            return true;
        } else {
            // 当且仅当首次发送交易信息时，返回9999报文
            Msg100 msg100 = (Msg100) rtnMsgList.get(0);
            return "00".equals(msg100.getRtnInfoCode());
        }
    }

    public boolean sendVouchsToHmb(String msgSn, long startNo, long endNo, String txnApplyNo, String vouchStatus) throws InvocationTargetException, IllegalAccessException {
        Msg005 msg005 = new Msg005();
        msg005.msgSn = msgSn;
        msg005.submsgNum = (int) (endNo - startNo + 1);
        msg005.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg005.origSysId = msg005.sendSysId;
        msg005.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg005);
        // 领用
        if (VouchStatus.RECEIVED.getCode().equals(vouchStatus) || VouchStatus.CANCEL.getCode().equals(vouchStatus)) {
            for (long i = startNo; i <= endNo; i++) {
                Msg049 msg049 = new Msg049();
                msg049.actionCode = "144";
                //59:单位ID
                msg049.orgId = PropertyManager.getProperty("hmfs_bank_unit_id");
                //60:单位类型
                msg049.orgType = PropertyManager.getProperty("hmfs_bank_unit_type");

                msg049.receiptNo = StringUtils.leftPad(String.valueOf(i), 12, "0");
                msg049.voucherSts = vouchStatus;
                msg049.voucherType = "00";   // 00-商品住宅
                hmbMsgList.add(msg049);
            }
        } else if (VouchStatus.USED.getCode().equals(vouchStatus)) {
            String[] payMsgTypes = {"01035", "01045"};
            List<HisMsginLog> payInfoList = qrySubMsgsByMsgSnAndTypes(txnApplyNo, payMsgTypes);
            HisMsginLog totalPayInfo = qryTotalMsgByMsgSn(txnApplyNo, "00005");
            for (long i = startNo; i <= endNo; i++) {
                HisMsginLog msginLog = payInfoList.get((int) (i - startNo));
                HmActinfoFund actinfoFund = hmbActinfoService.qryHmActinfoFundByFundActNo(msginLog.getFundActno1());

                Msg037 msg037 = new Msg037();
                BeanUtils.copyProperties(msg037, actinfoFund);

                msg037.actionCode = "141";
                msg037.txnAmt1 = msginLog.getTxnAmt1();
                msg037.receiptNo = StringUtils.leftPad(String.valueOf(i), 12, "0");
                msg037.payinActno = actinfoFund.getCbsActno();
                // TODO 房屋交存类型 1-商品房 ===== 票据类型 00-商品住宅
                msg037.voucherType = "00";
                /*if ("1".equals(totalPayInfo.getHouseDepType())) {
                                    msg037.voucherType = "00";
                                }*/

                msg037.depType = totalPayInfo.getHouseDepType();
                //80:交存人       21 信息名称
                msg037.depPerson = msginLog.getInfoName();
                //59:单位ID
                msg037.orgId = PropertyManager.getProperty("hmfs_bank_unit_id");
                //60:单位类型
                msg037.orgType = PropertyManager.getProperty("hmfs_bank_unit_type");
                //61:单位名称
                msg037.orgName = PropertyManager.getProperty("hmfs_bank_unit_name");
                msg037.linkMsgSn = msgSn;
                hmbMsgList.add(msg037);
            }
        } else {
            throw new RuntimeException("票据状态错误: " + vouchStatus);
        }
        byte[] txnBuf = messageFactory.marshal("5610", hmbMsgList);
        Map<String, List<HmbMsg>> rtnMsgMap = sendDataUntilRcv(txnBuf);
        List<HmbMsg> rtnMsgList = rtnMsgMap.get("5610");
        if (rtnMsgList != null) {
            SummaryResponseMsg msg006 = (SummaryResponseMsg) rtnMsgList.get(0);
            return "00".equals(msg006.rtnInfoCode);
        } else {
            rtnMsgList = rtnMsgMap.get("9999");
            throw new RuntimeException(((Msg100) rtnMsgList.get(0)).rtnInfo);
        }
        // 当且仅当首次发送交易信息时，返回9999报文
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
                if (socketBlockClient != null) {
                    socketBlockClient.close();
                    socketBlockClient = null;
                }
            } catch (IOException e) {
                //
            }
        }
    }

    public Msg006 createMsg006ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg006 msg006 = new Msg006();
        BeanUtils.copyProperties(msg006, msginLog);
        msg006.submsgNum = 0;
        msg006.sendSysId = SEND_SYS_ID;
        msg006.origSysId = "00";
        msg006.msgEndDate = "#";
        msg006.origMsgSn = msginLog.getMsgSn();
        msg006.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg006.setRtnInfoCode("00");
        msg006.setRtnInfo("申请编号【" + msginLog.getMsgSn() + "】交易成功");
        return msg006;
    }

    public Msg008 createMsg008ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg008 msg008 = new Msg008();
        BeanUtils.copyProperties(msg008, msginLog);
        msg008.submsgNum = 0;
        msg008.sendSysId = SEND_SYS_ID;
        msg008.origSysId = "00";
        msg008.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg008.msgEndDate = "#";
        msg008.setRtnInfoCode("00");
        msg008.setRtnInfo("申请编号【" + msginLog.getMsgSn() + "】交易成功");
        return msg008;
    }

    public List<HisMsginLog> changeToMsg042ByMsginList(List<HisMsginLog> detailMsginLogs) {
        for (HisMsginLog msginLog : detailMsginLogs) {
            msginLog.setMsgType("01042");
        }
        return detailMsginLogs;
    }
}
