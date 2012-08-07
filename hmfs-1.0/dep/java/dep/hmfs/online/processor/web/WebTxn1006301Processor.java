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
public class WebTxn1006301Processor extends WebAbstractHmbProductBizTxnProcessor {

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
            logger.error("跨行维修资金转出失败", e);
            throw new RuntimeException("跨行维修资金转出失败。", e);
        }
        return "0000|跨行维修资金转出成功";
    }

    @Transactional
    private void processRequest(String request) throws Exception {

        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];

        String[] refundSubMsgTypes = {"01039"};

        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00011");
        // 查询交易子报文记录
        List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, refundSubMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalRefundInfo, fundInfoList, totalRefundInfo.getTxnAmt1())) {
            // 扣款。
            actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                    fundInfoList, DCFlagCode.WITHDRAW.getCode(), "6301");
            List<HmMsgIn> cancelDetailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, new String[]{"01051"});
            hmbActinfoService.updateActinfoFundsByMsginList(cancelDetailMsginLogs);
            hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);
        }
        String[] payRtnMsgTypes = {"01039", "01051"};
        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payRtnMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalRefundInfo.getTxnCode(),
                hmbClientReqService.createMsg012ByTotalMsgin(totalRefundInfo), detailMsginLogs)) {
            return;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }

}
