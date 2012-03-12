package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA2002;
import dep.hmfs.service.BookkeepingService;
import dep.hmfs.service.TxnCheckService;
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
public class Txn2002Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private TxnCheckService txnCheckService;

    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA2002 tia2002 = new TIA2002();
        tia2002.body.drawApplyNo = new String(bytes, 0, 18).trim();
        tia2002.body.drawAmt = new String(bytes, 18, 16).trim();

        // 查询交易汇总报文记录
        HisMsginLog totalDrawInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia2002.body.drawApplyNo, "00007");

        String[] drawSubMsgTypes = {"01041"};
        // 查询交易子报文记录
        List<HisMsginLog> drawInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia2002.body.drawApplyNo, drawSubMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (txnCheckService.checkMsginTxnCtlSts(totalDrawInfo, drawInfoList, new BigDecimal(tia2002.body.drawAmt))) {
            // 支取交易。
            return handleDrawTxn(txnSerialNo, tia2002, drawSubMsgTypes, drawInfoList);
        } else {
            // 交易状态已经成功，直接生成成功报文到业务平台
            return null;
        }
    }

    /*
      支取交易。
    */
    @Transactional
    private TOA handleDrawTxn(String cbsSerialNo, TIA2002 tia2002, String[] subMsgTypes, List<HisMsginLog> payInfoList) throws Exception {

        // 会计账号记账
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia2002.body.drawAmt), DCFlagCode.TXN_OUT.getCode());

        // 批量核算户账户信息更新
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_OUT.getCode());

        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia2002.body.drawApplyNo, "00007", subMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
        // TODO 组装8583 报文
        if (notifyHmb()) {
            return null;
        } else {
            throw new RuntimeException("通讯异常！");
        }
    }

    // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
    private boolean notifyHmb() throws Exception {
        byte[] bytes = null;
        //Map<String, List<HmbMsg>> rtnMap = sendDataUntilRcv(bytes, mf);
        // TODO 解析8583报文
        //logger.info((String) rtnMap.keySet().toArray()[0]);
        return true;
    }
}
