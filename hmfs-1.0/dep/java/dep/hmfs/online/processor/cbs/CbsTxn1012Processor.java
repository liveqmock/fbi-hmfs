package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA1012;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn1012Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn1012Processor.class);

    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    // ҵ��ƽ̨���𽻿�ף����������ܾ֣��ɹ���Ӧ��ȡ��ϸ������ҵ��ƽ̨
    @Override
    @Transactional
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA1012 tia1012 = new TIA1012();
        tia1012.body.txnApplyNo = new String(bytes, 0, 18).trim();
        tia1012.body.txnAmt = new String(bytes, 18, 16).trim();

        logger.info("��ǰ��ƽ̨�����뵥�ţ�" + tia1012.body.txnApplyNo + "  ��" + tia1012.body.txnAmt);

        String[] payMsgTypes = {"01035", "01045"};

        // ��ѯ���׻��ܱ��ļ�¼
        HmMsgIn totalTxnInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1012.body.txnApplyNo, "00011");

        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1012.body.txnApplyNo, payMsgTypes);
        logger.info("��ѯ������ӱ��ġ���ѯ��������" + payInfoList.size());

        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (actBookkeepingService.checkMsginTxnCtlSts(totalTxnInfo, payInfoList, new BigDecimal(tia1012.body.txnAmt))) {
            // ����ס�
            logger.info("���ݼ����ȷ, ���ˡ����ͱ��������ֲܾ��ȴ���Ӧ...");
           // List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1012.body.txnApplyNo, payMsgTypes);
            actBookkeepingService.actBookkeepingByMsgins(txnSerialNo, payInfoList, DCFlagCode.DEPOSIT.getCode(), "1012");
            hmbBaseService.updateMsginSts(tia1012.body.txnApplyNo, TxnCtlSts.SUCCESS);
        }
        String[] payRtnMsgTypes = {"01033", "01035", "01045"};
        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1012.body.txnApplyNo, payRtnMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalTxnInfo.getTxnCode(), hmbClientReqService.createMsg012ByTotalMsgin(totalTxnInfo), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }

}
