package dep.hmfs.online.processor.web;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 维修资金支取.
 * User: zhangxiaobo
 * Date: 12-8-7
 */
@Component
public class WebTxn1006302Processor extends WebAbstractHmbProductBizTxnProcessor {

    @Autowired
    private HmbBaseService hmbBaseService;
    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    public String process(String request) {
        try {
            processRequest(request);
        } catch (Exception e) {
            logger.error("跨行维修资金转入失败", e);
            throw new RuntimeException("跨行维修资金转入失败。", e);
        }
        return "0000|跨行维修资金转入成功";
    }

    @Transactional
    private void processRequest(String request) throws Exception {

        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];
        String deptCode = fields[2];
        String operCode = fields[3];

        String[] payMsgTypes = {"01035", "01045"};

        // 查询交易汇总报文记录
        HmMsgIn totalTxnInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00011");

        // 查询交易子报文记录
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
        logger.info("查询交款交易子报文。查询到笔数：" + payInfoList.size());

        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalTxnInfo, payInfoList, totalTxnInfo.getTxnAmt1())) {
            // 交款交易。
            logger.info("数据检查正确, 记账、发送报文至房管局并等待响应...");
            // List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
            actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                    deptCode, operCode, payInfoList, DCFlagCode.DEPOSIT.getCode(), "6302");
            hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);
        }
        String[] payRtnMsgTypes = {"01033", "01035", "01045"};
        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payRtnMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalTxnInfo.getTxnCode(), hmbClientReqService.createMsg012ByTotalMsgin(totalTxnInfo), detailMsginLogs)) {
            return;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }

}
