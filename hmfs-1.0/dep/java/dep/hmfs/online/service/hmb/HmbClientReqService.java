package dep.hmfs.online.service.hmb;

import common.enums.VouchStatus;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoFund;
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
 * Time: ����8:52
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbClientReqService extends HmbBaseService {

    @Autowired
    private HmbActinfoService hmbActinfoService;

    // �뷿�ܾ�ͨ��
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
        // �ظ�����ʱ�����ط��͵ı��� rtnMsgListӦΪnull
        if (rtnMsgList == null) {
            return true;
        } else {
            // ���ҽ����״η��ͽ�����Ϣʱ������9999����
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
        // ����
        if (VouchStatus.RECEIVED.getCode().equals(vouchStatus) || VouchStatus.CANCEL.getCode().equals(vouchStatus)) {
            for (long i = startNo; i <= endNo; i++) {
                Msg049 msg049 = new Msg049();
                msg049.actionCode = "144";
                msg049.receiptNo = String.valueOf(i);
                msg049.voucherSts = vouchStatus;
                msg049.voucherType = "#";   // 00-��Ʒסլ
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
                msg037.receiptNo = String.valueOf(i);
                msg037.payinActno = actinfoFund.getCbsActno();
                // TODO ���ݽ������� 1-��Ʒ�� ===== Ʊ������ 00-��Ʒסլ
                if ("1".equals(totalPayInfo.getHouseDepType())) {
                    msg037.voucherType = "00";
                }
                msg037.depType = totalPayInfo.getHouseDepType();
                //80:������       21 ��Ϣ����
                msg037.depPerson = msginLog.getInfoName();
                //59:��λID
                msg037.orgId = PropertyManager.getProperty("hmfs_bank_unit_id");
                //60:��λ����
                msg037.orgType = PropertyManager.getProperty("hmfs_bank_unit_type");
                //61:��λ����
                msg037.orgName = PropertyManager.getProperty("hmfs_bank_unit_name");
                msg037.linkMsgSn = msgSn;
                hmbMsgList.add(msg037);
            }
        } else {
            throw new RuntimeException("Ʊ��״̬����: " + vouchStatus);
        }
        byte[] txnBuf = messageFactory.marshal("5610", hmbMsgList);
        Map<String, List<HmbMsg>> rtnMsgMap = sendDataUntilRcv(txnBuf);
        List<HmbMsg> rtnMsgList = rtnMsgMap.get("5610");
        // ���ҽ����״η��ͽ�����Ϣʱ������9999����
        SummaryResponseMsg msg006 = (SummaryResponseMsg) rtnMsgList.get(0);
        return "00".equals(msg006.rtnInfoCode);
    }

    public Msg006 createMsg006ByTotalMsgin(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException {
        Msg006 msg006 = new Msg006();
        BeanUtils.copyProperties(msg006, msginLog);
        msg006.submsgNum = 0;
        msg006.sendSysId = SEND_SYS_ID;
        msg006.origSysId = "00";
        msg006.msgEndDate = "#";
        msg006.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg006.setRtnInfoCode("00");
        msg006.setRtnInfo("�����š�" + msginLog.getMsgSn() + "�����׳ɹ�");
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
        msg008.setRtnInfo("�����š�" + msginLog.getMsgSn() + "�����׳ɹ�");
        return msg008;
    }
}
