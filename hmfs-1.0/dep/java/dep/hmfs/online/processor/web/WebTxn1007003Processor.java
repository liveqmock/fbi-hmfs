package dep.hmfs.online.processor.web;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmActStl;
import common.repository.hmfs.model.HmChkAct;
import dep.hmfs.online.processor.hmb.domain.*;
import org.springframework.stereotype.Component;

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
public class WebTxn1007003Processor extends WebAbstractHmbProductTxnProcessor {

    @Override
    public String process(String request) {

        String txnCode = "7003";
        String txnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //�����վ�����
        deleteOldActChkDataByTxnDate(txnDate, "99");
        deleteOldActChkDataByTxnDate(txnDate, "00");
        deleteOldTxnChkDataByTxnDate(txnDate, "99");
        deleteOldTxnChkDataByTxnDate(txnDate, "00");

        //���ͱ���
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(getRequestBuf(txnCode, txnDate));

        //�����ر���
        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            Msg100 msg100 = (Msg100) responseMap.get("9999").get(0);
            throw new RuntimeException(msg100.rtnInfo);
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg002.rtnInfo);
        } else {
            //��������ֵ��������ݿ�
            processChkBalResponse(msgList, txnDate);
        }

        //���ݺ˶Դ���
        if (verifyActBalData(txnDate)) {
            //��ϵͳ���Ʊ�״̬
            updateSysCtlStatus(SysCtlSts.HMB_CHK_SUCCESS);
            return "0000|�����ʽ��׳ɹ�";
        } else {
            return "9999|�����ʽ���ʧ��";
        }

    }

    private byte[] getRequestBuf(String txnCode, String txnDate) {
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        List<HmActFund> actFundList = hmbSysTxnService.selectFundActinfo();
        List<HmActStl> actStlList = hmbSysTxnService.selectCbsActinfo();

        //���ܱ��Ĵ���
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, actFundList.size() + actStlList.size(), true);
        msg001.txnType = "1";//����������
        msg001.bizType = "3"; //?
        msg001.origTxnCode = "5110"; //TODO ????
        hmbMsgList.add(msg001);

        //�ӱ��Ĵ���  098 094
        for (HmActFund hmActFund : actFundList) {
            Msg098 msg098 = new Msg098();
            msg098.actionCode = "304"; //304:���ն���
            msg098.infoId1 = hmActFund.getInfoId1();
            msg098.infoIdType1 = hmActFund.getInfoIdType1();
            msg098.cellNum = hmActFund.getCellNum();
            msg098.builderArea = hmActFund.getBuilderArea();
            msg098.fundActno1 = hmActFund.getFundActno1();
            msg098.fundActtype1 = hmActFund.getFundActtype1();
            msg098.actBal = hmActFund.getActBal();
            hmbMsgList.add(msg098);

            //���淢����ʵ����ݵ��������ݿ�
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg098.fundActno1);
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(msg098.actBal);
            hmChkActMapper.insert(hmChkAct);
        }

        for (HmActStl hmActStl : actStlList) {
            Msg094 msg094 = new Msg094();
            msg094.actionCode = "304"; //304:���ն���
            msg094.orgId = hmActStl.getOrgId();
            msg094.orgType = hmActStl.getOrgType();
            msg094.settleActno1 = hmActStl.getSettleActno1();
            msg094.settleActtype1 = hmActStl.getSettleActtype1();
            msg094.actBal = hmActStl.getActBal();
            hmbMsgList.add(msg094);

            //���淢����ʵ����ݵ��������ݿ�
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg094.settleActno1);
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(msg094.actBal);
            hmChkActMapper.insert(hmChkAct);
        }

        return messageFactory.marshal(txnCode, hmbMsgList);
    }

    /**
     * ��������ַ��ص���������Ϣ
     */
    private void processChkBalResponse(List<HmbMsg> msgList, String txnDate) {
        Msg002 msg002 = (Msg002) msgList.get(0);
        //TODO
        // String txnDate = msg002.msgDt.substring(0,8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg098) {
                hmChkAct.setActno(((Msg098) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg098) hmbMsg).getActBal());
            } else {
                hmChkAct.setActno(((Msg094) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg094) hmbMsg).getActBal());
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * У������������
     *
     * @return
     */
    private boolean verifyActBalData(String txnDate) {
        int successNumber = 0;
        int failNumber = 0;
        successNumber = hmCmnMapper.verifyChkaclResult_0(txnDate, "99", "00");
        logger.info(txnDate + "���ʳɹ�������" + successNumber);

        failNumber = hmCmnMapper.verifyChkaclResult_11(txnDate, "99", "00");
        failNumber += hmCmnMapper.verifyChkaclResult_12(txnDate, "99", "00");
        failNumber += hmCmnMapper.verifyChkaclResult_2(txnDate, "99", "00");
        logger.info(txnDate + "����ʧ�ܱ�����" + failNumber);

        return failNumber == 0;
    }

}
