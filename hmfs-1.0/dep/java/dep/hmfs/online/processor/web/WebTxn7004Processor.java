package dep.hmfs.online.processor.web;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.service.hmb.HmbCmnTxnService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ������ˮ����.
 * User: zhanrui
 * Date: 12-3-15
 * Time: ����1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn7004Processor extends WebAbstractTxnProcessor{
    @Resource
    private  HmbCmnTxnService hmbCmnTxnService;

    @Override
    public String process(String request)  {
        //TODO
        //HmSct hmSct = hmbCmnTxnService.getAppSysStatus();
        //if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())) {
        //    throw new RuntimeException("ϵͳ״̬�����������ʳɹ��󷽿ɽ��й����ֶ��ʲ�����");
        //}

        String txnCode = "7004";
        //���ͱ���
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(getRequestBuf(txnCode));

        //�����ر���
        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            throw new RuntimeException("���չ����ֱ��ĳ�������Ϊ��");
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg002.rtnInfo);
        }else{
            //���浽�������ݿ�
            hmbCmnTxnService.processChkBalResponse(msgList);
        }

        return null;
    }

    private byte[] getRequestBuf(String txnCode){
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        //���ܱ��Ĵ���
        Msg001 msg001 = new Msg001();
        hmbCmnTxnService.assembleSummaryMsg(txnCode, msg001, 1, false);
        msg001.txnType = "1";//����������
        msg001.bizType = "#"; //?
        msg001.origTxnCode = "#"; //TODO ????
        hmbMsgList.add(msg001);

        //�ӱ��Ĵ���  095-���㻧 092-���㻧
        return  messageFactory.marshal(txnCode, hmbMsgList);
    }
}
