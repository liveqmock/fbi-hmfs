package dep.hmfs.online.processor.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA2002;
import dep.hmfs.online.service.cbs.BookkeepingService;
import dep.hmfs.online.service.cbs.CbsTxnCheckService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
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
public class CmbTxn2002Processor extends CmbAbstractTxnProcessor {

    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private CbsTxnCheckService cbsTxnCheckService;
    @Autowired
    private HmbClientReqService hmbClientReqService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA2002 tia2002 = new TIA2002();
        tia2002.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2002.body.drawAmt = new String(bytes, 18, 16).trim();

        // ��ѯ���׻��ܱ��ļ�¼
        HisMsginLog totalDrawInfo = hmbBaseService.qryTotalMsgByMsgSn(tia2002.body.drawApplyNo, "00007");

        String[] drawSubMsgTypes = {"01041"};
        // ��ѯ�����ӱ��ļ�¼
        List<HisMsginLog> drawInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia2002.body.drawApplyNo, drawSubMsgTypes);
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (cbsTxnCheckService.checkMsginTxnCtlSts(totalDrawInfo, drawInfoList, new BigDecimal(tia2002.body.drawAmt))) {
            // ֧ȡ���ס�
            return handleDrawTxn(txnSerialNo, tia2002, totalDrawInfo, drawSubMsgTypes, drawInfoList);
        } else {
            // ����״̬�Ѿ��ɹ���ֱ�����ɳɹ����ĵ�ҵ��ƽ̨
            return null;
        }
    }

    /*
      ֧ȡ���ס�
    */
    @Transactional
    private TOA handleDrawTxn(String cbsSerialNo, TIA2002 tia2002, HisMsginLog totalMsginLog, String[] subMsgTypes, List<HisMsginLog> payInfoList) throws Exception {

        // ����˺ż���
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia2002.body.drawAmt), DCFlagCode.TXN_OUT.getCode(), "2002");

        // �������㻧�˻���Ϣ����
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_OUT.getCode(), "2002");

        hmbBaseService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia2002.body.drawApplyNo, "00007", subMsgTypes, TxnCtlSts.SUCCESS);

        String[] payMsgTypes = {"01042"};
        List<HisMsginLog> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(totalMsginLog.getMsgSn(), payMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalMsginLog.getTxnCode(),
                hmbClientReqService.createMsg008ByTotalMsgin(totalMsginLog), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException("2002���ͱ��������ֽܾ���ʧ�ܣ�");
        }
    }

}
