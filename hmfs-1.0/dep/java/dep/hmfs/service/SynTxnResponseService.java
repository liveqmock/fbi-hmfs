package dep.hmfs.service;

import common.repository.hmfs.model.HisMsginLog;
import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.Msg006;
import dep.hmfs.online.hmb.domain.Msg100;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class SynTxnResponseService {

    private XSocketBlockClient socketBlockClient;
    private String hmfsServerIP = PropertyManager.getProperty("socket_server_ip_hmfs");
    private int hmfsServerPort = PropertyManager.getIntProperty("socket_server_port_hmfs");
    private int hmfsServerTimeout = PropertyManager.getIntProperty("socket_server_timeout");
    private HmbMessageFactory mf = new HmbMessageFactory();

    public Map<String, List<HmbMsg>> sendDataUntilRcv(byte[] bytes) throws Exception {
        if (socketBlockClient == null) {
            socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
        }
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
        byte[] txnBuf = mf.marshal(txnCode, hmbMsgList);
        Map<String, List<HmbMsg>> rtnMsgMap = sendDataUntilRcv(txnBuf);
        Msg100 msg100 = (Msg100) rtnMsgMap.get("9999").get(0);
        return "00".equals(msg100.getRtnInfoCode());
    }
    
    public Msg006 initMsg006ByTotalMsgin(HisMsginLog totalPayInfo) {
            Msg006 msg006 = new Msg006();
            // TODO 报文编号生成
            msg006.setMsgSn("#");
            msg006.setSubmsgNum(totalPayInfo.getSubmsgNum());
            msg006.setSendSysId(PropertyManager.getProperty("SEND_SYS_ID"));
            msg006.setOrigSysId(totalPayInfo.getOrigSysId());
            msg006.setMsgDt(SystemService.formatTodayByPattern("yyyyMMddHHmmss"));
            msg006.setMsgEndDate("#");
            msg006.setOrigMsgSn(totalPayInfo.getMsgSn());
            msg006.setRtnInfoCode("00");
            msg006.setRtnInfo("申请编号【" + totalPayInfo.getMsgSn() + "】交款交易成功");
            msg006.setInfoId1(totalPayInfo.getInfoId1());
            msg006.setInfoIdType1(totalPayInfo.getInfoIdType1());
            msg006.setDistrictId(totalPayInfo.getDistrictId());
            msg006.setFundActno1(totalPayInfo.getFundActno1());
            msg006.setFundActtype1(totalPayInfo.getFundActtype1());
            msg006.setSettleActno1(totalPayInfo.getSettleActno1());
            msg006.setSettleActtype1(totalPayInfo.getSettleActtype1());
            msg006.setTxnAmt1(totalPayInfo.getTxnAmt1());
            
            return msg006;
        }
}
