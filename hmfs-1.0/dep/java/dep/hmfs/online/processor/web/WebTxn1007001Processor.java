package dep.hmfs.online.processor.web;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSysCtl;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg096;
import org.springframework.stereotype.Component;

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
public class WebTxn1007001Processor extends WebAbstractHmbProductTxnProcessor{

    public String process(String request)  {
        processSignout();
        return "0000|ǩ�˽��׳ɹ�";
    }

    /**
     * ǩ��
     */
    public void processSignout() {
        String txnCode = "7001";
        HmSysCtl hmSysCtl = hmbSysTxnService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                hmSysCtl.setSysSts(SysCtlSts.SIGNOUT.getCode());
                hmSysCtl.setSignoutDt(new Date());
                hmSysCtlMapper.updateByPrimaryKey(hmSysCtl);
                doHmbSignTxn(txnCode, "302");
            } catch (Exception e) {
                logger.error("ǩ��ʧ�ܡ������·���ǩ�ˡ�", e);
                throw new RuntimeException("ǩ��ʧ�ܡ������·���ǩ�ˡ�" + e.getMessage());
            }
        } else {
            throw new RuntimeException("ϵͳǩ����ɺ󷽿�ǩ�ˡ�");
        }
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
}
