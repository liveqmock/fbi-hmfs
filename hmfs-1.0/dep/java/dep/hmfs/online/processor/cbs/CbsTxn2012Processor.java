package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA3002;
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
public class CbsTxn2012Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn2012Processor.class);

    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA3002 tia3002 = new TIA3002();
        tia3002.body.refundApplyNo = new String(bytes, 0, 18).trim();
        tia3002.body.refundAmt = new String(bytes, 18, 16).trim();

        String[] refundSubMsgTypes = {"01039", "01043"};

        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia3002.body.refundApplyNo, "00005");
        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia3002.body.refundApplyNo, refundSubMsgTypes);
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (actBookkeepingService.checkMsginTxnCtlSts(totalRefundInfo, fundInfoList, new BigDecimal(tia3002.body.refundAmt))) {
            // �˿�ס�
            return handleRefundTxn(txnSerialNo, tia3002, totalRefundInfo, refundSubMsgTypes, fundInfoList);
        } else {
            // ����״̬�Ѿ��ɹ���ֱ�����ɳɹ����ĵ�ҵ��ƽ̨
            return null;
        }
    }

    /*
      �˿�ס�
    */
    @Transactional
    private TOA handleRefundTxn(String cbsSerialNo, TIA3002 tia3002, HmMsgIn totalMsginLog, String[] subMsgTypes, List<HmMsgIn> fundInfoList) throws Exception {

        // �������㻧�˻���Ϣ����
        actBookkeepingService.actBookkeepingByMsgins(cbsSerialNo, fundInfoList, DCFlagCode.WITHDRAW.getCode(), "3002");

        String[] updateFundMsgTypes = {"01033", "01051"};
        List<HmMsgIn> updateFundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia3002.body.refundApplyNo, updateFundMsgTypes);
        hmbActinfoService.updateActinfoFundsByMsginList(updateFundInfoList);

        hmbBaseService.updateMsginSts(tia3002.body.refundApplyNo, TxnCtlSts.SUCCESS);

        // 5230 �˿��ӱ������
        String[] refundFundMsgTypes = {"01039", "01043", "01033", "01051"};

        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(totalMsginLog.getMsgSn(), refundFundMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalMsginLog.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalMsginLog), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }

    }


}
