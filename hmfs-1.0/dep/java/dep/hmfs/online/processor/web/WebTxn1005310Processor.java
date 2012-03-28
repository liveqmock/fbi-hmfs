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
 * ά���ʽ�֧ȡ.
 * User: zhangxiaobo
 * Date: 12-3-26
 * Time: 22:01
 */
@Component
public class WebTxn1005310Processor extends WebAbstractHmbProductBizTxnProcessor {

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
            logger.error("�ʽ�֧ȡʧ��", e);
            throw new RuntimeException("�ʽ�֧ȡʧ�ܡ�", e);
        }
        return "0000|֧ȡ�ɹ�";
    }

    @Transactional
    private void processRequest(String request) throws Exception {
        //���ͱ���
        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];

        // ��ѯ���׻��ܱ��ļ�¼
        HmMsgIn totalDrawInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00007");
        String[] payMsgTypes = {"01041"};
        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> drawInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
        logger.info("��ѯ֧ȡ�����ӱ��ġ���ѯ��������" + drawInfoList.size());

        // �������㻧�˻���Ϣ����
        actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                drawInfoList, DCFlagCode.WITHDRAW.getCode(), "5310");

        hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);

        List<HmbMsg> rtnMsgList = hmbClientReqService.communicateWithHmbRtnMsgList(totalDrawInfo.getTxnCode(),
                hmbClientReqService.createMsg008ByTotalMsgin(totalDrawInfo), hmbClientReqService.changeToMsg042ByMsginList(drawInfoList)).get("9999");

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
