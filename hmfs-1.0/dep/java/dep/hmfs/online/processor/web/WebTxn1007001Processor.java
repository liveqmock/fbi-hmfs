package dep.hmfs.online.processor.web;

import common.enums.CbsErrorCode;
import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSysCtl;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg096;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * �򷿲���ǩ��.
 * User: zhanrui
 * Date: 12-3-15
 */
@Component
public class WebTxn1007001Processor extends WebAbstractHmbProductTxnProcessor {
    @Autowired
    private WebTxn1007003Processor webTxn7003Processor;
    @Autowired
    private HmbActinfoService hmbActinfoService;


    public String process(String request) {
        processSignout();
        return "0000|ǩ�˽��׳ɹ�";
    }

    /**
     * ǩ��
     */
    public void processSignout() {
        String txnCode = "7001";
        try {
            doHmbSignTxn(txnCode, "302");
            hmbSysTxnService.updateSysCTlForSignout();
        } catch (Exception e) {
            logger.error("ǩ��ʧ�ܡ������·���ǩ�ˡ�", e);
            throw new RuntimeException("ǩ��ʧ�ܡ������·���ǩ�ˡ�" + e.getMessage());
        }

        //=====20150105 zr �޸Ľ��ж�������===
        if ("05".equals(PropertyManager.getProperty("SEND_SYS_ID"))) {//����
            String hmbChkResponse = null;
            // ���������������
            try {
                String txnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                HmSysCtl hmSysCtl = hmbActinfoService.getSysCtl();
                String bankId = hmSysCtl.getBankId();
                String cbsActno = PropertyManager.getProperty("CBS_ACTNO");
                if (StringUtils.isEmpty(cbsActno)) {
                    cbsActno = "37101986106051016701";
                }
                clearTodayChkData(cbsActno, txnDate, bankId);

                hmbChkResponse = webTxn7003Processor.process(null);
            } catch (Exception e) {
                hmbChkResponse = "9999|������ֶ��˲�ƽ��";
                throw new RuntimeException(hmbChkResponse, e);
            }

            //ע�⣺���ж��ʲ��ԣ�����������������
            if (hmbChkResponse != null && hmbChkResponse.startsWith(CbsErrorCode.NET_COMMUNICATE_TIMEOUT.getCode())) {
                throw new RuntimeException(CbsErrorCode.NET_COMMUNICATE_TIMEOUT.getCode());
            }

            if (hmbChkResponse == null || !hmbChkResponse.startsWith("0000")) {
                throw new RuntimeException(CbsErrorCode.FUND_ACT_CHK_ERROR.getCode());
            }
        }
        //========================================
    }

    private void doHmbSignTxn(String txnCode, String actionCode) throws Exception {
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, 1, true);
        msg001.txnType = "1";//����������
        msg001.bizType = "3"; //????
        msg001.origTxnCode = "#"; //TODO ????

        Msg096 msg096 = new Msg096();
        msg096.actionCode = actionCode;

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg001);
        hmbMsgList.add(msg096);
        byte[] txnBuf = messageFactory.marshal(txnCode, hmbMsgList);
        //���ͱ���
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(txnBuf);

        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            throw new RuntimeException("���չ����ֱ��ĳ�������Ϊ��");
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg002.rtnInfo);
        }
    }

    //20150105
    private void clearTodayChkData(String cbsActNo, String txnDate, String bankId) {
        // ɾ������ΪtxnDate�Ļ���˻������˼�¼
        hmbSysTxnService.deleteCbsChkActByDate(txnDate, cbsActNo, bankId);
        hmbSysTxnService.deleteCbsChkActByDate(txnDate, cbsActNo, "99");
        // ɾ������˺Ž�����ϸ���˼�¼
        hmbSysTxnService.deleteCbsChkTxnByDate(txnDate, cbsActNo, bankId);
        hmbSysTxnService.deleteCbsChkTxnByDate(txnDate, cbsActNo, "99");
    }

}
