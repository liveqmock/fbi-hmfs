package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import dep.hmfs.online.processor.cbs.domain.base.TIAHeader;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.processor.cbs.domain.txn.TIA2002;
import dep.hmfs.online.service.hmb.ActBookkeepingService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbClientReqService;
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
public class CbsTxn2002Processor extends CbsAbstractTxnProcessor {

    @Autowired
    private ActBookkeepingService actBookkeepingService;
    @Autowired
    private HmbClientReqService hmbClientReqService;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    @Override
    @Transactional
    public TOA process(TIAHeader tiaHeader, byte[] bytes) throws Exception {
        TIA2002 tia2002 = new TIA2002();
        tia2002.header.deptCode = tiaHeader.deptCode;
        tia2002.header.operCode = tiaHeader.operCode;
        tia2002.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2002.body.drawAmt = new String(bytes, 18, 16).trim();

        // 查询交易汇总报文记录
        HmMsgIn totalDrawInfo = hmbBaseService.qryTotalMsgByMsgSn(tia2002.body.drawApplyNo, "00007");

        String[] drawSubMsgTypes = {"01041"};
        // 查询交易子报文记录
        List<HmMsgIn> drawInfoList = hmbBaseService.qrySubMsgsByMsgSnAndTypes(tia2002.body.drawApplyNo, drawSubMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (actBookkeepingService.checkMsginTxnCtlSts(totalDrawInfo, drawInfoList, new BigDecimal(tia2002.body.drawAmt))) {
            // 支取交易。
            return handleDrawTxn(tiaHeader.serialNo, tia2002, totalDrawInfo, drawSubMsgTypes, drawInfoList);
        } else {
            // 交易状态已经成功，直接生成成功报文到业务平台
            // 2012-8-13 保存 重复交易明细
            hmbActinfoService.insertDblTxnStl(tiaHeader, tia2002.body.drawApplyNo, DCFlagCode.WITHDRAW,
                    "2002", new BigDecimal(tia2002.body.drawAmt.trim()));
            return null;
        }
    }

    /*
      支取交易。
    */
    @Transactional
    private TOA handleDrawTxn(String cbsSerialNo, TIA2002 tia2002, HmMsgIn totalMsginLog, String[] subMsgTypes, List<HmMsgIn> drawInfoList) throws Exception {

        // 批量核算户账户信息更新
        actBookkeepingService.actBookkeepingByMsgins(cbsSerialNo, tia2002.header.deptCode.trim(),
                tia2002.header.operCode.trim(), drawInfoList, DCFlagCode.WITHDRAW.getCode(), "2002");

        hmbBaseService.updateMsginSts(tia2002.body.drawApplyNo, TxnCtlSts.SUCCESS);

        //String[] payMsgTypes = {"01041"};
        //List<HmMsgIn> detailMsginLogs = hmbBaseService.qrySubMsgsByMsgSnAndTypes(totalMsginLog.getMsgSn(), payMsgTypes);

        if (hmbClientReqService.communicateWithHmb(totalMsginLog.getTxnCode(),
                hmbClientReqService.createMsg008ByTotalMsgin(totalMsginLog), hmbClientReqService.changeToMsg042ByMsginList(drawInfoList))) {
            return null;
        } else {
            throw new RuntimeException(CbsErrorCode.SYSTEM_ERROR.getCode());
        }
    }

}
