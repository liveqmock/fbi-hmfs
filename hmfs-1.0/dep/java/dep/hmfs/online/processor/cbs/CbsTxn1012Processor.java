package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA1012;
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
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CbsTxn1012Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn1012Processor.class);

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
        TIA1012 tia1012 = new TIA1012();
        tia1012.body.txnApplyNo = new String(bytes, 0, 18).trim();
        tia1012.body.txnAmt = new String(bytes, 18, 16).trim();

        logger.info("【前端平台】申请单号：" + tia1012.body.txnApplyNo + "  金额：" + tia1012.body.txnAmt);

        String[] payMsgTypes = {"01035", "01045"};

        // 查询交易汇总报文记录
        HmMsgIn totalTxnInfo = hmbBaseService.qryTotalMsgByMsgSn(tia1012.body.txnApplyNo, "00011");

        // 查询交易子报文记录
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1012.body.txnApplyNo, payMsgTypes);
        logger.info("查询交款交易子报文。查询到笔数：" + payInfoList.size());

        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalTxnInfo, payInfoList, new BigDecimal(tia1012.body.txnAmt))) {
            // 交款交易。
            logger.info("数据检查正确, 记账、发送报文至房管局并等待响应...");
           // List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1012.body.txnApplyNo, payMsgTypes);
            actBookkeepingService.actBookkeepingByMsgins(txnSerialNo, payInfoList, DCFlagCode.DEPOSIT.getCode(), "1012");
            hmbBaseService.updateMsginSts(tia1012.body.txnApplyNo, TxnCtlSts.SUCCESS);
        }
        String[] payRtnMsgTypes = {"01033", "01035", "01045"};
        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia1012.body.txnApplyNo, payRtnMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalTxnInfo.getTxnCode(), hmbClientReqService.createMsg012ByTotalMsgin(totalTxnInfo), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }

}
