package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.SystemService;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import common.repository.hmfs.model.TxnCbsLog;
import common.service.HisMsginLogService;
import common.service.HmActinfoCbsService;
import common.service.HmActinfoFundService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1002;
import dep.hmfs.online.cmb.domain.txn.TOA1002;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
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
    private HmActinfoFundService hmActinfoFundService;
    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;
    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;
    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;
    @Autowired
    private TxnCbsLogMapper txnCbsLogMapper;
    @Autowired
    private TxnFundLogMapper txnFundLogMapper;

    // ҵ��ƽ̨���𽻿�ף����������ܾ֣��ɹ���Ӧ��ȡ��ϸ������ҵ��ƽ̨
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();
        tia1002.body.txnSerialNo = new String(bytes, 34, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};

        // TODO ����8583�ӿڴ����ͱ��� ���������ֲܾ��������ؽ��
        //hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // ��ѯ���׻��ܱ��ļ�¼
        HisMsginLog totalPayInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // ��ѯ�����ӱ��ļ�¼
        List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (totalPayInfo == null || payInfoList.size() < 1) {
            throw new RuntimeException("�ñʽ��ײ����ڣ�");
        } else if (TxnCtlSts.TXN_INIT.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            throw new RuntimeException("���ȷ����ѯ���ף�");
        } else if (TxnCtlSts.TXN_CANCEL.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            throw new RuntimeException("�ñʽ����ѳ�����");
        } else if (TxnCtlSts.TXN_HANDLING.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            // ����ס�
            return handlePayTxn(totalPayInfo, tia1002, payMsgTypes, payInfoList);
        } else {
            // ����״̬�Ѿ��ɹ���ֱ�����ɳɹ����ĵ�ҵ��ƽ̨
            return getPayInfoDatagram(tia1002, payInfoList);
        }
    }

    /*
      ����ס�
    */
    @Transactional
    private TOA1002 handlePayTxn(HisMsginLog totalMsg, TIA1002 tia1002, String[] payMsgTypes, List<HisMsginLog> payInfoList) throws ParseException {

        // CBS ����˻���Ϣ
        HmActinfoCbs hmActinfoCbs = hmActinfoCbsService.qryHmActinfoCbsBySettleActNo(totalMsg.getSettleActno1());
        // ���㻧�˻���Ϣ
        HmActinfoFund hmActinfoSettle = hmActinfoFundService.qryHmActinfoFundByFundActNo(totalMsg.getSettleActno1());
        // ��Ŀ���㻧�˻���Ϣ
        HmActinfoFund hmActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(totalMsg.getFundActno1());

        TxnCbsLog txnCbsLog = new TxnCbsLog();
        txnCbsLog.setPkid(UUID.randomUUID().toString());
        txnCbsLog.setTxnSn(tia1002.body.txnSerialNo);
        txnCbsLog.setTxnDate(SystemService.formatTodayByPattern("YYYYMMDD"));
        txnCbsLog.setTxnTime(SystemService.formatTodayByPattern("HHMMSS"));
        txnCbsLog.setFundActno(totalMsg.getSettleActno1());
        txnCbsLog.setTxnCode("1002");
        txnCbsLog.setMesgNo(tia1002.body.txnSerialNo);
        txnCbsLog.setCbsAcctno(hmActinfoCbs.getCbsActno());
        txnCbsLog.setOpacBrid(hmActinfoCbs.getBranchId());
        txnCbsLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
        txnCbsLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
        // ����CBS�˻�������ϸ��¼
        txnCbsLogMapper.insertSelective(txnCbsLog);
        // TODO ����Fund�˻�������ϸ��¼

        // �޸�CBS�˻�\�����˻�\��Ŀ�����˻���Ϣ���˻����,���ϴμ����ղ��ǽ��գ��޸��������Ϊ��ǰ�˻�������+=�ϴ����*���ڲ�ϴμ����� YYYY-MM-DD
        String strToday = SystemService.formatTodayByPattern("YYYY-MM-DD");
        if (!strToday.equals(hmActinfoCbs.getLastTxnDt())) {
            long days = SystemService.daysBetween(strToday, hmActinfoCbs.getLastTxnDt(), "YYYY-MM-DD");
            hmActinfoCbs.setIntcPdt(hmActinfoCbs.getIntcPdt()
                    .add(hmActinfoCbs.getLastActBal().multiply(new BigDecimal(days))));
            hmActinfoCbs.setLastActBal(hmActinfoCbs.getActBal());
            hmActinfoCbs.setLastTxnDt(strToday);
            hmActinfoSettle.setIntcPdt(hmActinfoSettle.getIntcPdt()
                    .add(hmActinfoSettle.getLastActBal().multiply(new BigDecimal(days))));
            hmActinfoSettle.setLastActBal(hmActinfoSettle.getActBal());
            hmActinfoSettle.setLastTxnDt(strToday);
            hmActinfoFund.setIntcPdt(hmActinfoFund.getIntcPdt()
                    .add(hmActinfoFund.getLastActBal().multiply(new BigDecimal(days))));
            hmActinfoFund.setLastActBal(hmActinfoFund.getActBal());
            hmActinfoFund.setLastTxnDt(strToday);
        }
        hmActinfoCbs.setActBal(hmActinfoCbs.getActBal().add(new BigDecimal(tia1002.body.payAmt)));
        hmActinfoSettle.setActBal(hmActinfoSettle.getActBal().add(new BigDecimal(tia1002.body.payAmt)));
        hmActinfoFund.setActBal(hmActinfoFund.getActBal().add(new BigDecimal(tia1002.body.payAmt)));
        hmActinfoCbsMapper.updateByPrimaryKey(hmActinfoCbs);
        hmActinfoFundMapper.updateByPrimaryKey(hmActinfoSettle);
        hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);

        // ҵ�����㻧�˻���Ϣ����
        for (HisMsginLog subPayMsg : payInfoList) {
            HmActinfoFund subActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(subPayMsg.getFundActno1());
            if (!strToday.equals(subActinfoFund.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActinfoCbs.getLastTxnDt(), "YYYY-MM-DD");
                subActinfoFund.setIntcPdt(subActinfoFund.getIntcPdt()
                        .add(subActinfoFund.getLastActBal().multiply(new BigDecimal(days))));
                subActinfoFund.setLastActBal(subActinfoFund.getActBal());
                subActinfoFund.setLastTxnDt(strToday);
            }
            hmActinfoFund.setActBal(hmActinfoFund.getActBal().add(subPayMsg.getTxnAmt1()));
            hmActinfoFundMapper.updateByPrimaryKey(subActinfoFund);
        }
        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);
        // TODO ����8583�ӿڴ����ͱ��� ���������ֲܾ��������ؽ��


        return getPayInfoDatagram(tia1002, payInfoList);
    }

    // TODO ����8583�ӿڴ����ͱ��� ���������ֲܾ��������ؽ��
    private boolean notifyHmb() {
        return true;
    }

    private TOA1002 getPayInfoDatagram(TIA1002 tia1002, List<HisMsginLog> payInfoList) {
        TOA1002 toa1002 = new TOA1002();
        toa1002.body.payApplyNo = tia1002.body.payApplyNo;
        if (payInfoList.size() > 0) {
            toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
            for (HisMsginLog hisMsginLog : payInfoList) {
                TOA1002.Body.Record record = new TOA1002.Body.Record();
                record.accountName = hisMsginLog.getInfoName();
                record.txAmt = String.format("%.2f", hisMsginLog.getTxnAmt1());
                record.address = hisMsginLog.getInfoAddr();
                record.houseArea = hisMsginLog.getBuilderArea().toString();
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
}
