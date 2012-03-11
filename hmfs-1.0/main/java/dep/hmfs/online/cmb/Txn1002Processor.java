package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.SystemService;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.dao.TxnCbsLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.TxnCbsLog;
import common.service.HisMsginLogService;
import common.service.HmActinfoCbsService;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1002;
import dep.hmfs.online.cmb.domain.txn.TOA1002;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
@Component
public class Txn1002Processor extends AbstractTxnProcessor {

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private HmActinfoCbsService hmActinfoCbsService;
    @Autowired
    private HisMsginLogMapper hisMsginLogMapper;
    @Autowired
    private TxnCbsLogMapper txnCbsLogMapper;
    
    private static Map<String, HmActinfoCbs> settleCbsActinfoMaps;
    
    public Txn1002Processor() {

    }

    // 业务平台发起交款交易，发送至房管局，成功响应后取明细发送至业务平台，打印收据凭证。然后发起票据管理交易。
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();
        tia1002.body.txnSerialNo = new String(bytes, 34, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};
        /*
        1、检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败
        2、判断该笔交易是否已完成，若已完成，则直接发送交易至房管局，本地数据库不再更新
        3、在一个事务中，【a】更新本地数据库 【b】发送至房管局，若失败，则回滚事务，返回交易失败
           【a】：1）更新汇总报文记录和子报文记录的交易处理状态为成功。
                 2）新增 TXN_CBS_LOG 记录
           【b】：1）查询当前申请单编号对应的所有报文信息。
                 2）生成响应报文对象。
         4、返回查询交易明细条目。
         */

        // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
        //hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // 查询交易汇总报文记录
        HisMsginLog totalPayInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (totalPayInfo == null) {
            throw new RuntimeException("该笔交易不存在！");
        } else if (TxnCtlSts.TXN_CANCEL.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            throw new RuntimeException("该笔交易已撤销！");
        } else if (TxnCtlSts.TXN_HANDLING.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            // 新增 TXN_CBS_LOG 记录 ，更新汇总报文记录和子报文记录的交易处理状态为成功。
            handlePayTxn(totalPayInfo, tia1002, payMsgTypes);
        }


        //  查询交易子报文记录
        List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);

        TOA1002 toa1002 = new TOA1002();
        toa1002.body.payApplyNo = tia1002.body.payApplyNo;
        if (payInfoList.size() > 0) {
            toa1002.body.payDetailNum = String.valueOf(payInfoList.size());
            for (HisMsginLog hisMsginLog : payInfoList) {
                TOA1002.Body.Record record = new TOA1002.Body.Record();
                record.accountName = hisMsginLog.getInfoName();
                record.txAmt = String.format("%.2f", hisMsginLog.getTxnAmt1());
                record.address = hisMsginLog.getInfoAddr();
                record.houseArea = hisMsginLog.getBuilderArea();
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

    /*
      新增 TXN_CBS_LOG 记录 ，更新汇总报文记录和子报文记录的交易处理状态为成功。
    */
    @Transactional
    private void handlePayTxn(HisMsginLog totalMsg, TIA1002 tia1002, String[] payMsgTypes) {
        TxnCbsLog txnCbsLog = new TxnCbsLog();
        txnCbsLog.setPkid(UUID.randomUUID().toString());
        txnCbsLog.setTxnSn(tia1002.body.txnSerialNo);
        txnCbsLog.setTxnDate(SystemService.formatDateByPattern("YYYYMMDD"));
        txnCbsLog.setTxnTime(SystemService.formatDateByPattern("HHMMSSsss"));
        txnCbsLog.setFundActno(totalMsg.getSettleActno1());
        txnCbsLog.setTxnCode("1002");
        txnCbsLog.setMesgNo(tia1002.body.txnSerialNo);
        HmActinfoCbs hmActinfoCbs = hmActinfoCbsService.qryHmActinfoCbsBySettleActNo(totalMsg.getSettleActno1());
        txnCbsLog.setCbsAcctno(hmActinfoCbs.getCbsActno());
        txnCbsLog.setOpacBrid(hmActinfoCbs.getBranchId());
        txnCbsLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
        txnCbsLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
        txnCbsLogMapper.insertSelective(txnCbsLog);
        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);
        // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
    }

    // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
    private byte[] reqAndResFromHmb() {
        return null;
    }
}
