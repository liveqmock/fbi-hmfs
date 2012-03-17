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
 * Time: 上午11:47
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

    // 业务平台发起交款交易，发送至房管局，成功响应后取明细发送至业务平台
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};

        // 查询交易汇总报文记录
        HisMsginLog totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // 查询交易子报文记录
        List<HisMsginLog> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (cbsTxnCheckService.checkMsginTxnCtlSts(totalPayInfo, payInfoList, new BigDecimal(tia1002.body.payAmt))) {
            // 交款交易。
            return handlePayTxnAndsendToHmb(txnSerialNo, totalPayInfo, tia1002, payMsgTypes, payInfoList);
        } else {
            // 交易状态已经成功，直接生成成功报文到业务平台
            return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
        }
    }

    /*
      交款交易。
    */
    @Transactional
    private TOA1002 handlePayTxnAndsendToHmb(String cbsSerialNo, HisMsginLog totalPayInfo, TIA1002 tia1002, String[] payMsgTypes, List<HisMsginLog> payInfoList) throws Exception, IOException {

        // 会计账号记账
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia1002.body.payAmt), DCFlagCode.TXN_IN.getCode());

        // 批量核算户账户信息更新
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_IN.getCode());

        hmbBaseService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.SUCCESS);

        return getPayInfoDatagram(totalPayInfo.getTxnCode(), totalPayInfo, tia1002, payInfoList);
    }

    private TOA1002 getPayInfoDatagram(String txnCode, HisMsginLog msginLog, TIA1002 tia1002, List<HisMsginLog> payInfoList) throws Exception {

        // 查询所有子报文
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
                    record.accountNo = hisMsginLog.getFundActno1();  // 业主核算户账号(维修资金账号)
                    toa1002.body.recordList.add(record);
                }
            }
            return toa1002;
        } else {
            throw new RuntimeException("发送报文至房管局交易失败！");
        }
    }
}
