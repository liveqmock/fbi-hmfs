package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA1002;
import dep.hmfs.online.processor.cbs.domain.txn.TOA1002;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.apache.commons.lang.StringUtils;
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
public class CbsTxn1002Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn1002Processor.class);

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
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        logger.info("��ǰ��ƽ̨�����뵥�ţ�" + tia1002.body.payApplyNo + "  ��" + tia1002.body.payAmt);

        String[] payMsgTypes = {"01035", "01045"};

        // ��ѯ���׻��ܱ��ļ�¼
        HmMsgIn totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");

        // ��ѯ�����ӱ��ļ�¼
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        logger.info("��ѯ������ӱ��ġ���ѯ��������" + payInfoList.size());

        // ���ñʽ��׻��ܱ��ļ�¼�����ñʱ����ѳ����򲻴��ڣ��򷵻ؽ���ʧ����Ϣ
        if (actBookkeepingService.checkMsginTxnCtlSts(totalPayInfo, payInfoList, new BigDecimal(tia1002.body.payAmt))) {
            // ����ס�
            logger.info("���ݼ����ȷ, ���ͱ��������ֲܾ��ȴ���Ӧ...");
            return handlePayTxnAndSendToHmb(txnSerialNo, totalPayInfo, tia1002, payInfoList);
        } else {
            // ����״̬�Ѿ��ɹ���ֱ�����ɳɹ����ĵ�ҵ��ƽ̨
            return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
        }
    }

    /*
      ����ס�
    */
    @Transactional
    private TOA1002 handlePayTxnAndSendToHmb(String cbsSerialNo, HmMsgIn totalPayInfo, TIA1002 tia1002, List<HmMsgIn> payInfoList) throws Exception {

        // �������㻧�˻���Ϣ����
        actBookkeepingService.actBookkeepingByMsgins(cbsSerialNo, payInfoList, DCFlagCode.TXN_IN.getCode(), "1002");

        hmbBaseService.updateMsginSts(tia1002.body.payApplyNo, TxnCtlSts.SUCCESS);

        return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
    }

    private TOA1002 getPayInfoDatagram(String txnCode, HmMsgIn msginLog, TIA1002 tia1002, List<HmMsgIn> payInfoList) throws Exception {

        // ��ѯ�����ӱ���  5150-�������� 5210-����
        // �����5150���ף����� String[] payMsgTypes = {"01033", "01035", "01045"};
        //List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msginLog.getMsgSn(), payMsgTypes);

        if (hmbClientReqService.communicateWithHmb(txnCode, hmbClientReqService.createMsg006ByTotalMsgin(msginLog), payInfoList)) {
            TOA1002 toa1002 = new TOA1002();
            toa1002.body.payApplyNo = tia1002.body.payApplyNo;
            if (payInfoList.size() > 0) {
                toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
                for (HmMsgIn hmMsgIn : payInfoList) {
                    HmActFund actFund = hmbActinfoService.qryHmActfundByActNo(hmMsgIn.getFundActno1());
                    TOA1002.Body.Record record = new TOA1002.Body.Record();
                    record.accountName = hmMsgIn.getInfoName();   //21
                    record.txAmt = String.format("%.2f", hmMsgIn.getTxnAmt1());
                    record.address = hmMsgIn.getInfoAddr();    //22
                    record.houseArea = StringUtils.isEmpty(hmMsgIn.getBuilderArea()) ? "" : hmMsgIn.getBuilderArea();

                    record.houseType = actFund.getHouseDepType();
                    record.phoneNo = actFund.getHouseCustPhone();
                    String field83 = actFund.getDepStandard2();
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
                    record.accountNo = hmMsgIn.getFundActno1();  // ҵ�����㻧�˺�(ά���ʽ��˺�)
                    toa1002.body.recordList.add(record);
                }
            }
            return toa1002;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }
}
