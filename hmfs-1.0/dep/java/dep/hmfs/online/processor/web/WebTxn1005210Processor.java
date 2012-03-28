package dep.hmfs.online.processor.web;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 维修资金交款.
 * User: zhangxiaobo
 * Date: 12-3-26
 * Time: 21:01
 */
@Component
public class WebTxn1005210Processor extends WebAbstractHmbProductBizTxnProcessor {

    @Autowired
    private HmbBaseService hmbBaseService;
    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;

    public String process(String request) {
        try {
            processRequest(request);
        } catch (Exception e) {
            logger.error("交款失败", e);
            throw new RuntimeException("交款失败。", e);
        }
        return "0000|缴款成功";
    }

    @Transactional
    private void processRequest(String request) throws Exception {
        //发送报文
        String[] fields = request.split("\\|");
        //String txnCode = fields[0];
        String msgSn = fields[1];

        // 查询交易汇总报文记录
        HmMsgIn totalPayInfo = hmbBaseService.qryTotalMsgByMsgSn(msgSn, "00005");
        String[] payMsgTypes = {"01035", "01045"};
        // 查询交易子报文记录
        List<HmMsgIn> payInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(msgSn, payMsgTypes);
        logger.info("查询交款交易子报文。查询到笔数：" + payInfoList.size());

        // 批量核算户账户信息更新
        actBookkeepingService.actBookkeepingByMsgins(SystemService.formatTodayByPattern("yyMMddHHMMSSsss"),
                payInfoList, DCFlagCode.DEPOSIT.getCode(), "5210");

        hmbBaseService.updateMsginSts(msgSn, TxnCtlSts.SUCCESS);

        List<HmbMsg> rtnMsgList = hmbClientReqService.communicateWithHmbRtnMsgList(totalPayInfo.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalPayInfo), payInfoList).get("9999");

        // 重复发送时，返回发送的报文 rtnMsgList应为null 交易成功
        if (rtnMsgList != null) {
            // 当且仅当首次发送交易信息时，返回9999报文
            Msg100 msg100 = (Msg100) rtnMsgList.get(0);
            if (!"00".equals(msg100.getRtnInfoCode())) {
                throw new RuntimeException("国土局返回错误信息：" + msg100.rtnInfo);
            }
        }

        // =============================
        /*Msg100 msg100 = new Msg100();
        msg100.setRtnInfoCode("00");
        if (!msg100.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg100.rtnInfo);
        }*/
    }

}
