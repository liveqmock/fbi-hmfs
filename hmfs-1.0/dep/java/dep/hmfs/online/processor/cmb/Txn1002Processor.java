package dep.hmfs.online.processor.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA1002;
import dep.hmfs.online.processor.cmb.domain.txn.TOA1002;
import dep.hmfs.online.service.cmb.BookkeepingService;
import dep.hmfs.online.service.hmb.HmbAsynResponseService;
import dep.hmfs.online.service.cmb.TxnCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
public class Txn1002Processor extends AbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Txn1002Processor.class);

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private TxnCheckService txnCheckService;
    @Autowired
    private HmbAsynResponseService hmbAsynResponseService;

    // ҵ��ƽ̨���𽻿�ף����������ܾ֣��ɹ���Ӧ��ȡ��ϸ������ҵ��ƽ̨
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};

        // ��ѯ���׻��ܱ��ļ�¼
        HisMsginLog totalPayInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // ��ѯ�����ӱ��ļ�¼
        List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (txnCheckService.checkMsginTxnCtlSts(totalPayInfo, payInfoList, new BigDecimal(tia1002.body.payAmt))) {
            // ����ס�
            return handlePayTxnAndsendToHmb(txnSerialNo, totalPayInfo, tia1002, payMsgTypes, payInfoList);
        } else {
            // ����״̬�Ѿ��ɹ���ֱ�����ɳɹ����ĵ�ҵ��ƽ̨
            return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
        }
    }

    /*
      ����ס�
    */
    @Transactional
    private TOA1002 handlePayTxnAndsendToHmb(String cbsSerialNo, HisMsginLog totalPayInfo, TIA1002 tia1002, String[] payMsgTypes, List<HisMsginLog> payInfoList) throws Exception, IOException {

        // ����˺ż���
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia1002.body.payAmt), DCFlagCode.TXN_IN.getCode());

        // �������㻧�˻���Ϣ����
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_IN.getCode());

        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);

        return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
    }

    private TOA1002 getPayInfoDatagram(String txnCode, HisMsginLog msginLog, TIA1002 tia1002, List<HisMsginLog> payInfoList) throws Exception {

        // ��ѯ�����ӱ���
        String[] payMsgTypes = {"01033", "01035", "01045"};
        List<HisMsginLog> detailMsginLogs = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(msginLog.getMsgSn(), payMsgTypes);
        if (hmbAsynResponseService.communicateWithHmb(txnCode, hmbAsynResponseService.createMsg006ByTotalMsgin(msginLog), detailMsginLogs)) {
            TOA1002 toa1002 = new TOA1002();
            toa1002.body.payApplyNo = tia1002.body.payApplyNo;
            if (payInfoList.size() > 0) {
                toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
                for (HisMsginLog hisMsginLog : payInfoList) {
                    TOA1002.Body.Record record = new TOA1002.Body.Record();
                    // TODO  �����ֶΣ��˻�������ַ���������͡��绰���롢������ۡ��ɿ����
                    record.accountName = hisMsginLog.getInfoName();
                    record.txAmt = String.format("%.2f", hisMsginLog.getTxnAmt1());
                    record.address = hisMsginLog.getInfoAddr();
                    record.houseArea = hisMsginLog.getBuilderArea() == null ? "" : String.format("%.2f", hisMsginLog.getBuilderArea());
                    record.houseType = "";
                    record.phoneNo = "";
                    record.projAmt = "";   // String.format("%.2f", xxx);
                    record.payPart = "";
                    record.accountNo = hisMsginLog.getFundActno1();  // ҵ�����㻧�˺�(ά���ʽ��˺�)
                    toa1002.body.recordList.add(record);
                }
            }
            return toa1002;
        } else {
            throw new RuntimeException("���ͱ��������ֽܾ���ʧ�ܣ�");
        }
    }
}
