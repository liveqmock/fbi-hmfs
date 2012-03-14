package dep.hmfs.online.service.hmb;

import common.enums.VouchStatus;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoFund;
import common.service.HisMsginLogService;
import common.service.HmActinfoFundService;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private HmActinfoFundService hmActinfoFundService;
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
        Msg100 msg100 = (Msg100) rtnMsgMap.get("9999").get(0);
        return "00".equals(msg100.getRtnInfoCode());
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
                msg049.receiptNo = String.valueOf(i);
                msg049.voucherSts = vouchStatus;
                msg049.voucherType = "#";   // 00-商品住宅
                hmbMsgList.add(msg049);
            }
        } else if (VouchStatus.USED.getCode().equals(vouchStatus)) {
            String[] payMsgTypes = {"01035", "01045"};
            List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(txnApplyNo, payMsgTypes);            
            for (long i = startNo; i <= endNo; i++) {
                HisMsginLog msginLog = payInfoList.get((int)(i - startNo));
                HmActinfoFund actinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(msginLog.getFundActno1());
                
                Msg037 msg037 = new Msg037();
                BeanUtils.copyProperties(msg037, actinfoFund);

                msg037.actionCode = "141";
                msg037.receiptNo = String.valueOf(i);
                msg037.txnAmt1 = msginLog.getTxnAmt1();
                msg037.payinActno = actinfoFund.getCbsActno();
                msg037.voucherType = actinfoFund.getHouseDepType();
                // TODO
                //59:单位ID
                //60:单位类型
                //61:单位名称
                //77:收据编号
                //78:交存标准
                //79:交存类型
                //80:交存人
                //81:户卡号
                //82:购房合同号
                //90:票据类型

                msg037.linkMsgSn = msgSn;
                hmbMsgList.add(msg037);
            }
        } else {
            throw new RuntimeException("票据状态错误: " + vouchStatus);
        }
        return true;
    }

    public Msg006 createMsg006ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg006 msg006 = new Msg006();
        BeanUtils.copyProperties(msg006, msginLog);
        assembleSummaryMsg(msginLog.getTxnCode(), msg006, 0, false);
        msg006.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg006.setRtnInfoCode("00");
        msg006.setRtnInfo("申请编号【" + msginLog.getMsgSn() + "】交易成功");
        return msg006;
    }

    public Msg008 createMsg008ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg008 msg008 = new Msg008();
        BeanUtils.copyProperties(msg008, msginLog);
        assembleSummaryMsg(msginLog.getTxnCode(), msg008, 0, false);
        msg008.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg008.setRtnInfoCode("00");
        msg008.setRtnInfo("申请编号【" + msginLog.getMsgSn() + "】交易成功");
        return msg008;
    }
}
