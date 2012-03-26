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
 * Time: 上午11:47
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

    // 业务平台发起交款交易，发送至房管局，成功响应后取明细发送至业务平台
    @Override
    @Transactional
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        logger.info("【前端平台】申请单号：" + tia1002.body.payApplyNo + "  金额：" + tia1002.body.payAmt);

        String[] payMsgTypes = {"01035", "01045"};

        // 查询交易汇总报文记录
        HmMsgIn totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");

        // 查询交易子报文记录
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        logger.info("查询交款交易子报文。查询到笔数：" + payInfoList.size());

        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalPayInfo, payInfoList, new BigDecimal(tia1002.body.payAmt))) {
            // 交款交易。
            logger.info("数据检查正确, 发送报文至房管局并等待响应...");
            return handlePayTxnAndSendToHmb(txnSerialNo, totalPayInfo, tia1002, payInfoList);
        } else {
            // 交易状态已经成功，直接生成成功报文到业务平台
            return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
        }
    }

    /*
      交款交易。
    */
    @Transactional
    private TOA1002 handlePayTxnAndSendToHmb(String cbsSerialNo, HmMsgIn totalPayInfo, TIA1002 tia1002, List<HmMsgIn> payInfoList) throws Exception {

        // 批量核算户账户信息更新
        actBookkeepingService.actBookkeepingByMsgins(cbsSerialNo, payInfoList, DCFlagCode.TXN_IN.getCode(), "1002");

        hmbBaseService.updateMsginSts(tia1002.body.payApplyNo, TxnCtlSts.SUCCESS);

        return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
    }

    private TOA1002 getPayInfoDatagram(String txnCode, HmMsgIn msginLog, TIA1002 tia1002, List<HmMsgIn> payInfoList) throws Exception {

        // 查询所有子报文  5150-开户交款 5210-交款
        // 如果有5150交易，则需 String[] payMsgTypes = {"01033", "01035", "01045"};
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
                    record.accountNo = hmMsgIn.getFundActno1();  // 业主核算户账号(维修资金账号)
                    toa1002.body.recordList.add(record);
                }
            }
            return toa1002;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }
}
