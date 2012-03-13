package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA3002;
import dep.hmfs.service.BookkeepingService;
import dep.hmfs.service.SynTxnResponseService;
import dep.hmfs.service.TxnCheckService;
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
public class Txn3002Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private TxnCheckService txnCheckService;
    @Autowired
    private SynTxnResponseService synTxnResponseService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA3002 tia3002 = new TIA3002();
        tia3002.body.refundApplyNo = new String(bytes, 0, 18).trim();
        tia3002.body.refundAmt = new String(bytes, 18, 16).trim();

        String[] refundSubMsgTypes = {"01039", "01043"};

        HisMsginLog totalRefundInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia3002.body.refundApplyNo, "00005");
        // ��ѯ�����ӱ��ļ�¼
        List<HisMsginLog> fundInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia3002.body.refundApplyNo, refundSubMsgTypes);
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (txnCheckService.checkMsginTxnCtlSts(totalRefundInfo, fundInfoList, new BigDecimal(tia3002.body.refundAmt))) {
            // �˿�ס�
            return handleRefundTxn(txnSerialNo, tia3002, totalRefundInfo, refundSubMsgTypes, fundInfoList);
        } else {
            // ����״̬�Ѿ��ɹ���ֱ�����ɳɹ����ĵ�ҵ��ƽ̨
            return null;
        }
    }

    /*
      ֧ȡ���ס�
    */
    @Transactional
    private TOA handleRefundTxn(String cbsSerialNo, TIA3002 tia3002, HisMsginLog totalMsginLog, String[] subMsgTypes, List<HisMsginLog> payInfoList) throws Exception {

        // ����˺ż���
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia3002.body.refundAmt), DCFlagCode.TXN_OUT.getCode());
        // �������㻧�˻���Ϣ����
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_OUT.getCode());

        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia3002.body.refundApplyNo, "00005", subMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // 5230 �˿��ӱ������
        String[] payMsgTypes = {"01039", "01043", "01033", "01051"};
        List<HisMsginLog> detailMsginLogs = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(totalMsginLog.getMsgSn(), payMsgTypes);
        if (synTxnResponseService.communicateWithHmb(totalMsginLog.getTxnCode(),
                synTxnResponseService.createMsg006ByTotalMsgin(totalMsginLog), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException("3002���ͱ��������ֽܾ���ʧ�ܣ�");
        }

    }


}
