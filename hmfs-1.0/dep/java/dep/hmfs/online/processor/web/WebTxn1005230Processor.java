package dep.hmfs.online.processor.web;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 维修资金退款.
 * User: zhangxiaobo
 * Date: 12-3-26
 * Time: 21:40
 */
@Component
public class WebTxn1005230Processor extends WebAbstractHmbProductTxnProcessor {

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
            logger.error("退款失败", e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return "0000|退款成功";
    }

    @Transactional
    private void processRequest(String request) throws Exception {
        //发送报文
        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];

        // 查询交易汇总报文记录
        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00005");
        String[] refundMsgTypes = {"01039", "01043"};
        // 查询交易子报文记录
        List<HmMsgIn> refundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, refundMsgTypes);
        logger.info("查询退款交易子报文。查询到笔数：" + refundInfoList.size());

        // 批量核算户账户信息更新
        actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                refundInfoList, DCFlagCode.TXN_OUT.getCode(), "5230");

        String[] updateFundMsgTypes = {"01033", "01051"};
        List<HmMsgIn> updateFundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, updateFundMsgTypes);
        hmbActinfoService.updateActinfoFundsByMsginList(updateFundInfoList);

        hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);

        String[] refundFundMsgTypes = {"01039", "01043", "01033", "01051"};

        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, refundFundMsgTypes);
        List<HmbMsg> rtnMsgList = hmbClientReqService.communicateWithHmbRtnMsgList(totalRefundInfo.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalRefundInfo), detailMsginLogs).get("9999");

        // 重复发送时，返回发送的报文 rtnMsgList应为null 交易成功
        if (rtnMsgList != null) {
            // 当且仅当首次发送交易信息时，返回9999报文
            Msg100 msg100 = (Msg100) rtnMsgList.get(0);
            if (!"00".equals(msg100.getRtnInfoCode())) {
                throw new RuntimeException("国土局返回错误信息：" + msg100.rtnInfo);
            }
        }
    }

}
