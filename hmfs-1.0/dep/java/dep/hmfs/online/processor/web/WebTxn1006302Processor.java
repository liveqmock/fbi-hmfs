package dep.hmfs.online.processor.web;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ά���ʽ�֧ȡ.
 * User: zhangxiaobo
 * Date: 12-8-7
 */
@Component
public class WebTxn1006302Processor extends WebAbstractHmbProductBizTxnProcessor {

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
            logger.error("����ά���ʽ�ת��ʧ��", e);
            throw new RuntimeException("����ά���ʽ�ת��ʧ�ܡ�", e);
        }
        return "0000|����ά���ʽ�ת��ɹ�";
    }

    @Transactional
    private void processRequest(String request) throws Exception {

        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];
        String deptCode = fields[2];
        String operCode = fields[3];

        String[] payMsgTypes = {"01035", "01045"};

        // ��ѯ���׻��ܱ��ļ�¼
        HmMsgIn totalTxnInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00011");

        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
        logger.info("��ѯ������ӱ��ġ���ѯ��������" + payInfoList.size());

        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (actBookkeepingService.checkMsginTxnCtlSts(totalTxnInfo, payInfoList, totalTxnInfo.getTxnAmt1())) {
            // ����ס�
            logger.info("���ݼ����ȷ, ���ˡ����ͱ��������ֲܾ��ȴ���Ӧ...");
            // List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
            actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                    deptCode, operCode, payInfoList, DCFlagCode.DEPOSIT.getCode(), "6302");
            hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);
        }
        String[] payRtnMsgTypes = {"01033", "01035", "01045"};
        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payRtnMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalTxnInfo.getTxnCode(), hmbClientReqService.createMsg012ByTotalMsgin(totalTxnInfo), detailMsginLogs)) {
            return;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }

}
