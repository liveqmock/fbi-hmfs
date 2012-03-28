package dep.hmfs.online.processor.web;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ά���ʽ𽻿�.
 * User: zhangxiaobo
 * Date: 12-3-26
 * Time: 21:01
 */
@Component
public class WebTxn1005210Processor extends WebAbstractHmbProductBizTxnProcessor {

    @Autowired
    private HmbBaseService hmbBaseService;
    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;

    public String process(String request) {
        try {
            processRequest(request);
        } catch (Exception e) {
            logger.error("����ʧ��", e);
            throw new RuntimeException("����ʧ�ܡ�", e);
        }
        return "0000|�ɿ�ɹ�";
    }

    @Transactional
    private void processRequest(String request) throws Exception {
        //���ͱ���
        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];

        // ��ѯ���׻��ܱ��ļ�¼
        HmMsgIn totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00005");
        String[] payMsgTypes = {"01035", "01045"};
        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
        logger.info("��ѯ������ӱ��ġ���ѯ��������" + payInfoList.size());

        // �������㻧�˻���Ϣ����
        actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                payInfoList, DCFlagCode.DEPOSIT.getCode(), "5210");

        hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);

        List<HmbMsg> rtnMsgList = hmbClientReqService.communicateWithHmbRtnMsgList(totalPayInfo.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalPayInfo), payInfoList).get("9999");

        // �ظ�����ʱ�����ط��͵ı��� rtnMsgListӦΪnull ���׳ɹ�
        if (rtnMsgList != null) {
            // ���ҽ����״η��ͽ�����Ϣʱ������9999����
            Msg100 msg100 = (Msg100) rtnMsgList.get(0);
            if (!"00".equals(msg100.getRtnInfoCode())) {
                throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg100.rtnInfo);
            }
        }

        // =============================
        /*Msg100 msg100 = new Msg100();
        msg100.setRtnInfoCode("00");
        if (!msg100.rtnInfoCode.equals("00")) {
            throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg100.rtnInfo);
        }*/
    }

}
