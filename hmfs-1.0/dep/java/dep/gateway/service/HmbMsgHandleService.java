package dep.gateway.service;

import common.service.SystemService;
import dep.ContainerManager;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.HmbAbstractTxnProcessor;
import dep.hmfs.online.processor.hmb.HmbAsyncAbstractTxnProcessor;
import dep.hmfs.online.processor.hmb.HmbSyncSubAbstractTxnProcessor;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.util.PropertyManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-18
 * Time: ����2:27
 * To change this template use File | Settings | File Templates.
 */

@Service
public class HmbMsgHandleService implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(CbsMsgHandleService.class);
    @Autowired
    private HmbMessageFactory mf;

    /*// ���պ󷵻�9999����
    private static final String[] ASYN_RES_TXNCODES = {"5210", "5310"};
    // ��ͬ������ ��ʱȥ�� 5110��5150
    private static final String[] SYN_RES_TXNCODES = {"5120", "5130", "5140", "5160",
            "6110", "6210", "6220", "7002"};*/

    @Override
    public byte[] handleMessage(byte[] bytes) {
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(bytes);
        String txnCode = (String) rtnMap.keySet().toArray()[0];
        logger.info("�����ط����HmbMsgHandleService�����յ������룺" + txnCode);
        logger.info("�����ط����HmbMsgHandleService�����յ����ܱ��ĺ��ӱ���������" + rtnMap.get(txnCode).size());
        SummaryMsg summaryMsg = (SummaryMsg) rtnMap.get(txnCode).get(0);
        String msgType = summaryMsg.msgType;
        String msgSn = summaryMsg.msgSn;
        HmbAbstractTxnProcessor processor = null;
        processor = (HmbAbstractTxnProcessor) ContainerManager.getBean("hmbTxn" + txnCode + "Processor");
        if (processor instanceof HmbAsyncAbstractTxnProcessor) {
            return asyncHandleMessage(processor, txnCode, msgSn, rtnMap.get(txnCode));
        } else {
            return syncHandleMessage(processor, txnCode, msgType, msgSn, rtnMap.get(txnCode));
        }
    }

    private byte[] asyncHandleMessage(HmbAbstractTxnProcessor processor, String txnCode, String msgSn, List<HmbMsg> hmbMsgList) {
        Msg100 msg100 = createRtnMsg100(msgSn);
        try {
            int cnt = processor.run(txnCode, msgSn, hmbMsgList);
            msg100.rtnInfo = "��" + cnt + "�ʱ��Ľ��ճɹ�";
        } catch (Exception e) {
            logger.error("���Ľ��ձ���ʧ�ܣ�", e);
            msg100.rtnInfoCode = "99";
            msg100.rtnInfo = "����ʧ��,ԭ��" + e.getMessage();
        }
        // ��Ӧ
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(msg100);
        return mf.marshal("9999", rtnHmbMsgList);
    }

    private byte[] syncHandleMessage(HmbAbstractTxnProcessor processor, String txnCode, String msgType, String msgSn, List<HmbMsg> hmbMsgList) {
        logger.info("MsgType:" + msgType);
        String rtnMsgType = StringUtils.leftPad(String.valueOf(Integer.parseInt(msgType.substring(2)) + 1), 3, "0");
        logger.info("rtnMsgType:" + rtnMsgType);
        SummaryResponseMsg summaryMsg = null;
        try {
            summaryMsg = (SummaryResponseMsg) Class.forName(SummaryMsg.class.getPackage().getName() + ".Msg" + rtnMsgType).newInstance();
            HmbMsg hmbMsg = hmbMsgList.get(0);
            BeanUtils.copyProperties(summaryMsg, hmbMsg);
            summaryMsg.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
            summaryMsg.origSysId = "00";
            summaryMsg.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
            summaryMsg.rtnInfoCode = "00";
            int cnt = processor.run(txnCode, msgSn, hmbMsgList);
            summaryMsg.rtnInfo = "��" + cnt + "��" + txnCode + "���ױ��Ĵ���ɹ�";
        } catch (Exception e) {
            logger.error("����ʧ��.������:" + txnCode, e);
            summaryMsg.rtnInfoCode = "99";
            summaryMsg.rtnInfo = "����ʧ��,ԭ��" + e.getMessage();
        }
        // ͬ��������Ӧʱ ���ӱ���
        List<HmbMsg> rtnHmbMsgList = new ArrayList<HmbMsg>();
        rtnHmbMsgList.add(summaryMsg);
        try {
            if (processor instanceof HmbSyncSubAbstractTxnProcessor) {
//                HmbMsg hmbMsg = hmbMsgList.get(0);
                List<HmbMsg> rtnSubHmbMsgList = hmbMsgList.subList(1, hmbMsgList.size());
                //��ӱ���i�����������ӱ���2012-11-08
                int i=1;
                for (HmbMsg msg : rtnSubHmbMsgList) {
                    //���ص��ӱ������ݵ��ڻ�ȡ���ӱ������� 2012-11-08
                    HmbMsg hmbMsg = hmbMsgList.get(i);
                    SubMsg subMsg = null;
                    logger.info("msg.msgType:" + msg.getMsgType());
                    String rtnSubMsgType = StringUtils.leftPad(String.valueOf(Integer.parseInt(msg.getMsgType().substring(2)) + 1), 3, "0");
                    logger.info("rtnSubMsgType:" + rtnSubMsgType);
                    subMsg = (SubMsg) Class.forName(SubMsg.class.getPackage().getName() + ".Msg" + rtnSubMsgType).newInstance();
                    BeanUtils.copyProperties(subMsg, hmbMsg);
                    rtnHmbMsgList.add(subMsg);
                    i++;
                }
            }
        } catch (Exception e) {
            logger.error("����ʧ��.������:" + txnCode, e);
            throw new RuntimeException(e);
        }
        return mf.marshal(txnCode, rtnHmbMsgList);
    }

    private Msg100 createRtnMsg100(String msgSn) {
        if (StringUtils.isEmpty(msgSn)) {
            throw new RuntimeException("��Ӧ���ı�Ų���Ϊ�գ�");
        }
        Msg100 msg100 = new Msg100();
        msg100.msgSn = msgSn;
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfo = "���Ĵ���ɹ�";
        return msg100;
    }

    private Msg004 createRtnMsg004(String msgSn) {
        if (StringUtils.isEmpty(msgSn)) {
            throw new RuntimeException("��Ӧ���ı�Ų���Ϊ�գ�");
        }
        Msg004 msg004 = new Msg004();
        msg004.msgSn = msgSn;
        msg004.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg004.origSysId = "00";
        msg004.rtnInfoCode = "00";
        msg004.rtnInfo = "���Ĵ���ɹ�";
        msg004.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg004.msgEndDate = "#";
        msg004.origMsgSn = msgSn;
        return msg004;
    }
}
