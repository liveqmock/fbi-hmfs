package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA3002;
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
public class CbsTxn3002Processor extends CbsAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CbsTxn3002Processor.class);

    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Override
    @Transactional
    public TOA process(TIAHeader tiaHeader, byte[] bytes) throws Exception {
        TIA3002 tia3002 = new TIA3002();
        tia3002.body.refundApplyNo = new String(bytes, 0, 18).trim();
        tia3002.body.refundAmt = new String(bytes, 18, 16).trim();

        String[] refundSubMsgTypes = {"01039", "01043"};

        HmMsgIn totalRefundInfo = hmbBaseService.qryTotalMsgByMsgSn(tia3002.body.refundApplyNo, "00005");
        // 查询交易子报文记录
        List<HmMsgIn> fundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia3002.body.refundApplyNo, refundSubMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalRefundInfo, fundInfoList, new BigDecimal(tia3002.body.refundAmt))) {
            // 退款交易。
            // 批量核算户账户信息更新
            actBookkeepingService.actBookkeepingByMsgins(tiaHeader.serialNo, tiaHeader.deptCode.trim(),
                    tiaHeader.operCode.trim(), fundInfoList, DCFlagCode.WITHDRAW.getCode(), "3002");
            // 核算户变更、销户
            String[] updateFundMsgTypes = {"01033", "01051"};
            List<HmMsgIn> updateFundInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia3002.body.refundApplyNo, updateFundMsgTypes);
            hmbActinfoService.updateActinfoFundsByMsginList(updateFundInfoList);

            hmbBaseService.updateMsginSts(tia3002.body.refundApplyNo, TxnCtlSts.SUCCESS);
        } else {
            // 2012-8-13 保存 重复交易明细
            hmbActinfoService.insertDblTxnStl(tiaHeader, tia3002.body.refundApplyNo, DCFlagCode.WITHDRAW,
                    "3002", new BigDecimal(tia3002.body.refundAmt.trim()));
        }
        // 5230 退款子报文序号
        String[] refundFundMsgTypes = {"01039", "01043", "01033", "01051"};

        List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(totalRefundInfo.getMsgSn(), refundFundMsgTypes);
        if (hmbClientReqService.communicateWithHmb(totalRefundInfo.getTxnCode(),
                hmbClientReqService.createMsg006ByTotalMsgin(totalRefundInfo), detailMsginLogs)) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }
}
