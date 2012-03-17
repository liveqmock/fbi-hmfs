package dep.hmfs.online.processor.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoFund;
import dep.hmfs.online.service.cbs.BookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.processor.cmb.domain.base.TOA;
import dep.hmfs.online.processor.cmb.domain.txn.TIA1002;
import dep.hmfs.online.processor.cmb.domain.txn.TOA1002;
import dep.hmfs.online.service.cbs.CbsTxnCheckService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.apache.commons.lang.StringUtils;
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
public class CmbTxn1002Processor extends CmbAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CmbTxn1002Processor.class);

    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private CbsTxnCheckService cbsTxnCheckService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    // ҵ��ƽ̨���𽻿�ף����������ܾ֣��ɹ���Ӧ��ȡ��ϸ������ҵ��ƽ̨
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};

        // ��ѯ���׻��ܱ��ļ�¼
        HisMsginLog totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // ��ѯ�����ӱ��ļ�¼
        List<HisMsginLog> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (cbsTxnCheckService.checkMsginTxnCtlSts(totalPayInfo, payInfoList, new BigDecimal(tia1002.body.payAmt))) {
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

        hmbBaseService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.SUCCESS);

        return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
    }

    private TOA1002 getPayInfoDatagram(String txnCode, HisMsginLog msginLog, TIA1002 tia1002, List<HisMsginLog> payInfoList) throws Exception {

        // ��ѯ�����ӱ���
        String[] payMsgTypes = {"01033", "01035", "01045"};
        List<HisMsginLog> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msginLog.getMsgSn(), payMsgTypes);
        if (hmbClientReqService.communicateWithHmb(txnCode, hmbClientReqService.createMsg006ByTotalMsgin(msginLog), detailMsginLogs)) {
            TOA1002 toa1002 = new TOA1002();
            toa1002.body.payApplyNo = tia1002.body.payApplyNo;
            if (payInfoList.size() > 0) {
                toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
                for (HisMsginLog hisMsginLog : payInfoList) {
                    HmActinfoFund actinfoFund = hmbActinfoService.qryHmActinfoFundByFundActNo(hisMsginLog.getFundActno1());
                    TOA1002.Body.Record record = new TOA1002.Body.Record();
                    record.accountName = hisMsginLog.getInfoName();   //21
                    record.txAmt = String.format("%.2f", hisMsginLog.getTxnAmt1());
                    record.address = hisMsginLog.getInfoAddr();    //22
                    // 24
                    record.houseArea = StringUtils.isEmpty(hisMsginLog.getBuilderArea()) ? "" : hisMsginLog.getBuilderArea();

                    record.houseType = actinfoFund.getHouseDepType();
                    record.phoneNo = actinfoFund.getHouseCustPhone();
                    String field83 = actinfoFund.getDepStandard2();
                    if (field83 == null) {
                        record.projAmt = "";
                        record.payPart = "";
                    } else if (field83.endsWith("|") || !field83.contains("|")) {
                        record.projAmt = new StringBuilder(field83).deleteCharAt(field83.length() - 1).toString();
                        record.payPart = "";
                    } else {
                        String[] fields83 = field83.split("\\|");
                        record.projAmt = fields83[0];
                        record.payPart = fields83[1];
                    }
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
