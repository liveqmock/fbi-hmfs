package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.SystemService;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.dao.TxnCbsLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.TxnCbsLog;
import common.service.HisMsginLogService;
import common.service.HmActinfoCbsService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1002;
import dep.hmfs.online.cmb.domain.txn.TOA1002;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: ����11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn1002Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private HmActinfoCbsService hmActinfoCbsService;
    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;
    @Autowired
    private TxnCbsLogMapper txnCbsLogMapper;
    
    private static Map<String, HmActinfoCbs> settleCbsActinfoMaps;
    
    public Txn1002Processor() {

    }

    // ҵ��ƽ̨���𽻿�ף����������ܾ֣��ɹ���Ӧ��ȡ��ϸ������ҵ��ƽ̨����ӡ�վ�ƾ֤��Ȼ����Ʊ�ݹ����ס�
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();
        tia1002.body.txnSerialNo = new String(bytes, 34, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};
        /*
        1�����ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ��
        2���жϸñʽ����Ƿ�����ɣ�������ɣ���ֱ�ӷ��ͽ��������ܾ֣��������ݿⲻ�ٸ���
        3����һ�������У���a�����±������ݿ� ��b�����������ܾ֣���ʧ�ܣ���ع����񣬷��ؽ���ʧ��
           ��a����1�����»��ܱ��ļ�¼���ӱ��ļ�¼�Ľ��״���״̬Ϊ�ɹ���
                 2������ TXN_CBS_LOG ��¼
           ��b����1����ѯ��ǰ���뵥��Ŷ�Ӧ�����б�����Ϣ��
                 2��������Ӧ���Ķ���
         4�����ز�ѯ������ϸ��Ŀ��
         */

        // TODO ����8583�ӿڴ����ͱ��� ���������ֲܾ��������ؽ��
        //hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // ��ѯ���׻��ܱ��ļ�¼
        HisMsginLog totalPayInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (totalPayInfo == null) {
            throw new RuntimeException("�ñʽ��ײ����ڣ�");
        } else if (TxnCtlSts.TXN_CANCEL.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            throw new RuntimeException("�ñʽ����ѳ�����");
        } else if (TxnCtlSts.TXN_HANDLING.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            // ���� TXN_CBS_LOG ��¼ �����»��ܱ��ļ�¼���ӱ��ļ�¼�Ľ��״���״̬Ϊ�ɹ���
            handlePayTxn(totalPayInfo, tia1002, payMsgTypes);
        }


        //  ��ѯ�����ӱ��ļ�¼
        List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);

        TOA1002 toa1002 = new TOA1002();
        toa1002.body.payApplyNo = tia1002.body.payApplyNo;
        if (payInfoList.size() > 0) {
            toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
            for (HisMsginLog hisMsginLog : payInfoList) {
                TOA1002.Body.Record record = new TOA1002.Body.Record();
                record.accountName = hisMsginLog.getInfoName();
                record.txAmt = String.format("%.2f", hisMsginLog.getTxnAmt1());
                record.address = hisMsginLog.getInfoAddr();
                record.houseArea = hisMsginLog.getBuilderArea();
                // TODO  �����ֶΣ��������͡��绰���롢������ۡ��ɿ����
                record.houseType = "";
                record.phoneNo = "";
                record.projAmt = "";   // String.format("%.2f", xxx);
                record.payPart = "";
                record.accountNo = hisMsginLog.getFundActno1();  // ҵ�����㻧�˺�(ά���ʽ��˺�)
                toa1002.body.recordList.add(record);
            }
        }

        return toa1002;
    }

    /*
      ���� TXN_CBS_LOG ��¼ �����»��ܱ��ļ�¼���ӱ��ļ�¼�Ľ��״���״̬Ϊ�ɹ���
    */
    @Transactional
    private void handlePayTxn(HisMsginLog totalMsg, TIA1002 tia1002, String[] payMsgTypes) {
        TxnCbsLog txnCbsLog = new TxnCbsLog();
        txnCbsLog.setPkid(UUID.randomUUID().toString());
        txnCbsLog.setTxnSn(tia1002.body.txnSerialNo);
        txnCbsLog.setTxnDate(SystemService.formatDateByPattern("YYYYMMDD"));
        txnCbsLog.setTxnTime(SystemService.formatDateByPattern("HHMMSSsss"));
        txnCbsLog.setFundActno(totalMsg.getSettleActno1());
        txnCbsLog.setTxnCode("1002");
        txnCbsLog.setMesgNo(tia1002.body.txnSerialNo);
        HmActinfoCbs hmActinfoCbs = hmActinfoCbsService.qryHmActinfoCbsBySettleActNo(totalMsg.getSettleActno1());
        txnCbsLog.setCbsAcctno(hmActinfoCbs.getCbsActno());
        txnCbsLog.setOpacBrid(hmActinfoCbs.getBranchId());
        txnCbsLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
        txnCbsLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
        txnCbsLogMapper.insertSelective(txnCbsLog);
        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);
        // TODO ����8583�ӿڴ����ͱ��� ���������ֲܾ��������ؽ��
    }

    // TODO ����8583�ӿڴ����ͱ��� ���������ֲܾ��������ؽ��
    private byte[] reqAndResFromHmb() {
        return null;
    }
}
