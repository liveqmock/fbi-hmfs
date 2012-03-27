package dep.hmfs.online.processor.web;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ά���ʽ��˿�.
 * User: zhangxiaobo
 * Date: 12-3-26
 * Time: 21:40
 */
@Component
public class WebTxn1005230Processor extends WebAbstractHmbProductTxnProcessor {

    @Autowired
    private HmbBaseService hmbBaseService;
    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    public String process(String request) {
        try {
            processRequest(request);
        } catch (Exception e) {
            logger.error("�˿�ʧ��", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return "0000|�˿�ɹ�";
    }

    @Transactional
    private void processRequest(String request) throws Exception {
        //���ͱ���
        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];

        // ��ѯ���׻��ܱ��ļ�¼
        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00005");
        String[] refundMsgTypes = {"01039", "01043"};
        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> refundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, refundMsgTypes);
        logger.info("��ѯ�˿���ӱ��ġ���ѯ��������" + refundInfoList.size());

        // �������㻧�˻���Ϣ����
        actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                refundInfoList, DCFlagCode.TXN_OUT.getCode(), "5230");

        String[] updateFundMsgTypes = {"01033", "01051"};
        List<HmMsgIn> updateFundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, updateFundMsgTypes);
        hmbActinfoService.updateActinfoFundsByMsginList(updateFundInfoList);

        hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);

        String[] refundFundMsgTypes = {"01039", "01043", "01033", "01051"};

        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, refundFundMsgTypes);
        List<HmbMsg> rtnMsgList = hmbClientReqService.communicateWithHmbRtnMsgList(totalRefundInfo.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalRefundInfo), detailMsginLogs).get("9999");

        // �ظ�����ʱ�����ط��͵ı��� rtnMsgListӦΪnull ���׳ɹ�
        if (rtnMsgList != null) {
            // ���ҽ����״η��ͽ�����Ϣʱ������9999����
            Msg100 msg100 = (Msg100) rtnMsgList.get(0);
            if (!"00".equals(msg100.getRtnInfoCode())) {
                throw new RuntimeException("�����ַ��ش�����Ϣ��" + msg100.rtnInfo);
            }
        }
    }

}
