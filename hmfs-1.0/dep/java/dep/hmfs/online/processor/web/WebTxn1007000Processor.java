package dep.hmfs.online.processor.web;

import common.enums.SysCtlSts;
import common.repository.hmfs.dao.hmfs.HmCmnMapper;
import common.repository.hmfs.model.HmSysCtl;
import dep.hmfs.online.processor.hmb.domain.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * �򷿲���ǩ��.
 * User: zhanrui
 * Date: 12-3-15
 * Time: ����1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1007000Processor extends WebAbstractHmbProductTxnProcessor{

    @Resource
    HmCmnMapper cmnMapper;

    public String process(String request)  {
        processSignon();
        return "0000|ǩ�����׳ɹ�";
    }

    /**
     * �������ǩ��
     */
    public void processSignon() {
        String txnCode = "7000";
        HmSysCtl hmSysCtl = hmbSysTxnService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        //if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHK_OVER)) {
            try {
                updateHmSctRecord(hmSysCtl);
                doHmbSignTxn(txnCode, "301");
                hmSysCtl.setSysSts(SysCtlSts.SIGNON.getCode());
                hmSysCtl.setSignonDt(new Date());
                hmSysCtlMapper.updateByPrimaryKey(hmSysCtl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        //} else {
        //    throw new RuntimeException("ϵͳ��ʼ��������ֶ�����ɺ󷽿�ǩ����");
        //}
    }

    private void doHmbSignTxn(String txnCode, String actionCode) throws Exception {
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, 1, true);
        msg001.txnType = "1";//����������
        msg001.bizType = "3"; //????
        msg001.origTxnCode = "#"; //TODO ????

        Msg096 msg096 = new Msg096();
        msg096.actionCode = actionCode; //301:ǩ��

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg001);
        hmbMsgList.add(msg096);
        byte[] txnBuf = messageFactory.marshal(txnCode, hmbMsgList);
        //���ͱ���
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(txnBuf);

        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            Msg100 msg100 = (Msg100) responseMap.get("9999").get(0);
            throw new RuntimeException(msg100.rtnInfo);
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg002.rtnInfo);
        }
    }

    private void updateHmSctRecord(HmSysCtl hmSysCtl){
        String date8 = new SimpleDateFormat("yyyyMMdd").format(new Date());

        if (!date8.equals(hmSysCtl.getTxnDate())) {
            hmSysCtl.setTxnDate(date8);
            hmSysCtl.setTxnseq(1);
        }

    }
}
