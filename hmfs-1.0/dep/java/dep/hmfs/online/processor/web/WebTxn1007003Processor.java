package dep.hmfs.online.processor.web;

import common.repository.hmfs.dao.HmChkActMapper;
import common.repository.hmfs.dao.hmfs.HmfsCmnMapper;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import common.repository.hmfs.model.HmChkAct;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.hmfs.online.service.hmb.HmbSysTxnService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ������.
 * User: zhanrui
 * Date: 12-3-15
 * Time: ����1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1007003Processor extends WebAbstractHmbProductTxnProcessor{
    @Resource
    private HmbSysTxnService hmbSysTxnService;

    @Resource
    private HmfsCmnMapper hmfsCmnMapper;

    @Resource
    private HmChkActMapper hmChkActMapper;

    private  String txnDate;


    @Override
    public String process(String request)  {

        String txnCode = "7003";
        //���ͱ���
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(getRequestBuf(txnCode));

        txnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

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
            processChkBalResponse(msgList);
        }

        //���ݺ˶Դ���
        if (verifyActBalData()) {
            return "0000|�����ʽ��׳ɹ�";
        }else{
            return "9999|�����ʽ���ʧ��";
        }

    }

    private byte[] getRequestBuf(String txnCode){
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        //���ܱ��Ĵ���
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, 1, false);
        msg001.txnType = "1";//����������
        msg001.bizType = "#"; //?
        msg001.origTxnCode = "#"; //TODO ????
        hmbMsgList.add(msg001);

        //�ӱ��Ĵ���  098 094
        List<HmActinfoFund> actinfoFundList = hmbSysTxnService.selectFundActinfo();
        for (HmActinfoFund hmActinfoFund : actinfoFundList) {
            Msg098 msg098 = new Msg098();
            msg098.actionCode = "304"; //304:���ն���
            msg098.infoId1 = hmActinfoFund.getInfoId1();
            msg098.infoIdType1 = hmActinfoFund.getInfoIdType1();
            msg098.cellNum = hmActinfoFund.getCellNum();
            msg098.builderArea = hmActinfoFund.getBuilderArea();
            msg098.fundActno1 = hmActinfoFund.getFundActno1();
            msg098.fundActtype1 = hmActinfoFund.getFundActtype1();
            msg098.actBal = hmActinfoFund.getActBal();
            hmbMsgList.add(msg098);

            //���淢����ʵ����ݵ��������ݿ�
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg098.fundActno1);
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setActbal(msg098.actBal);
            hmChkActMapper.insert(hmChkAct);
        }
        List<HmActinfoCbs> actinfoCbsList = hmbSysTxnService.selectCbsActinfo();
        for (HmActinfoCbs hmActinfoCbs : actinfoCbsList) {
            Msg094 msg094 = new Msg094();
            msg094.actionCode = "304"; //304:���ն���
            msg094.orgId = hmActinfoCbs.getOrgId();
            msg094.orgType = hmActinfoCbs.getOrgType();
            msg094.settleActno1 = hmActinfoCbs.getSettleActno1();
            msg094.settleActtype1 = hmActinfoCbs.getSettleActtype1();
            hmbMsgList.add(msg094);

            //���淢����ʵ����ݵ��������ݿ�
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg094.settleActno1);
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setActbal(msg094.actBal);
            hmChkActMapper.insert(hmChkAct);
        }
        return  messageFactory.marshal(txnCode, hmbMsgList);
    }


    /**
     * ��������ַ��ص���������Ϣ
     */
    private void processChkBalResponse(List<HmbMsg> msgList){
        Msg002 msg002 = (Msg002) msgList.get(0);
        //TODO
        // String txnDate = msg002.msgDt.substring(0,8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setTxnDate(this.txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg098) {
                hmChkAct.setActno(((Msg098) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg098) hmbMsg).getActBal());
            }else{
                hmChkAct.setActno(((Msg094) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg094) hmbMsg).getActBal());
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * У������������
     * @return
     */
    private boolean verifyActBalData(){
        //SEND_SYS_ID
        int successNumber = 0;
        int failNumber = 0;
        successNumber = hmfsCmnMapper.verifyChkaclResult_0(txnDate, SEND_SYS_ID);
        logger.info(txnDate + "���ʳɹ�������" + successNumber);

        failNumber = hmfsCmnMapper.verifyChkaclResult_11(txnDate, SEND_SYS_ID);
        failNumber += hmfsCmnMapper.verifyChkaclResult_12(txnDate, SEND_SYS_ID);
        failNumber += hmfsCmnMapper.verifyChkaclResult_2(txnDate, SEND_SYS_ID);
        logger.info(txnDate + "����ʧ�ܱ�����" + failNumber);

        return failNumber == 0;
    }

}
