package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HisMsginLog;
import common.service.HisMsginLogService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1002;
import dep.hmfs.online.cmb.domain.txn.TOA1002;
import dep.hmfs.service.BookkeepingService;
import dep.hmfs.service.TxnCheckService;
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
public class Txn1002Processor extends AbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Txn1002Processor.class);

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private BookkeepingService bookkeepingService;
    @Autowired
    private TxnCheckService txnCheckService;

    private HmbMessageFactory mf = new HmbMessageFactory();

    // 业务平台发起交款交易，发送至房管局，成功响应后取明细发送至业务平台
    @Override
    public TOA process(String txnSerialNo, byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};

        // 查询交易汇总报文记录
        HisMsginLog totalPayInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // 查询交易子报文记录
        List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (txnCheckService.checkMsginTxnCtlSts(totalPayInfo, payInfoList, new BigDecimal(tia1002.body.payAmt))) {
            // 交款交易。
            return handlePayTxn(txnSerialNo, tia1002, payMsgTypes, payInfoList);
        } else {
            // 交易状态已经成功，直接生成成功报文到业务平台
            return getPayInfoDatagram(tia1002, payInfoList);
        }
    }

    /*
      交款交易。
    */
    @Transactional
    private TOA1002 handlePayTxn(String cbsSerialNo, TIA1002 tia1002, String[] payMsgTypes, List<HisMsginLog> payInfoList) throws Exception, IOException {

        // 会计账号记账
        bookkeepingService.cbsActBookkeeping(cbsSerialNo, new BigDecimal(tia1002.body.payAmt), DCFlagCode.TXN_IN.getCode());

        // 批量核算户账户信息更新
        bookkeepingService.fundActBookkeepingByMsgins(payInfoList, DCFlagCode.TXN_IN.getCode());

        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
        // TODO 组装8583 报文
        if (notifyHmb()) {
            return getPayInfoDatagram(tia1002, payInfoList);
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

    private TOA1002 getPayInfoDatagram(TIA1002 tia1002, List<HisMsginLog> payInfoList) {
        TOA1002 toa1002 = new TOA1002();
        toa1002.body.payApplyNo = tia1002.body.payApplyNo;
        if (payInfoList.size() > 0) {
            toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
            for (HisMsginLog hisMsginLog : payInfoList) {
                TOA1002.Body.Record record = new TOA1002.Body.Record();
                record.accountName = hisMsginLog.getInfoName();
                record.txAmt = String.format("%.2f", hisMsginLog.getTxnAmt1());
                record.address = hisMsginLog.getInfoAddr();
                record.houseArea = hisMsginLog.getBuilderArea() == null ? "" : String.format("%.2f", hisMsginLog.getBuilderArea());
                // TODO  待定字段：房屋类型、电话号码、工程造价、缴款比例
                record.houseType = "";
                record.phoneNo = "";
                record.projAmt = "";   // String.format("%.2f", xxx);
                record.payPart = "";
                record.accountNo = hisMsginLog.getFundActno1();  // 业主核算户账号(维修资金账号)
                toa1002.body.recordList.add(record);
            }
        }

        return toa1002;
    }
    
}
