package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA2012;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class CbsTxn2012Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn2012Processor.class);

    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA2012 tia2012 = new TIA2012();
        tia2012.body.txnApplyNo = new String(bytes, 0, 18).trim();
        tia2012.body.txnAmt = new String(bytes, 18, 16).trim();

        String[] refundSubMsgTypes = {"01039", "01051"};

        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia2012.body.txnApplyNo, "00011");
        // 查询交易子报文记录
        List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia2012.body.txnApplyNo, refundSubMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalRefundInfo, fundInfoList, new BigDecimal(tia2012.body.txnAmt))) {
            // 退款交易。
            actBookkeepingService.actBookkeepingByMsgins(txnSerialNo, fundInfoList, DCFlagCode.WITHDRAW.getCode(), "3002");
            hmbActinfoService.updateActinfoFundsByMsginList(fundInfoList);
            hmbBaseService.updateMsginSts(tia2012.body.txnApplyNo, TxnCtlSts.SUCCESS);
        }
        if (hmbClientReqService.communicateWithHmb(totalRefundInfo.getTxnCode(),
                hmbClientReqService.createMsg012ByTotalMsgin(totalRefundInfo), fundInfoList)) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }
}
