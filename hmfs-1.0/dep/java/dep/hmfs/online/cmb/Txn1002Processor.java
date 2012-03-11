package dep.hmfs.online.cmb;

import common.enums.DCFlagCode;
import common.enums.SystemService;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import common.service.HisMsginLogService;
import common.service.HmActinfoCbsService;
import common.service.HmActinfoFundService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.xsocket.client.impl.XSocketBlockClient;
import dep.hmfs.online.cmb.domain.base.TOA;
import dep.hmfs.online.cmb.domain.txn.TIA1002;
import dep.hmfs.online.cmb.domain.txn.TOA1002;
import dep.hmfs.online.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(Txn1002Processor.class);

    @Autowired
    private HisMsginLogService hisMsginLogService;
    @Autowired
    private HmActinfoCbsService hmActinfoCbsService;
    @Autowired
    private HmActinfoFundService hmActinfoFundService;
    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;
    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;
    @Autowired
    private TxnCbsLogMapper txnCbsLogMapper;
    @Autowired
    private TxnFundLogMapper txnFundLogMapper;

    private HmbMessageFactory mf = new HmbMessageFactory();

    // 业务平台发起交款交易，发送至房管局，成功响应后取明细发送至业务平台
    @Override
    public TOA process(byte[] bytes) throws Exception {
        TIA1002 tia1002 = new TIA1002();
        tia1002.body.payApplyNo = new String(bytes, 0, 18).trim();
        tia1002.body.payAmt = new String(bytes, 18, 16).trim();
        tia1002.body.txnSerialNo = new String(bytes, 34, 16).trim();

        String[] payMsgTypes = {"01035", "01045"};

        // 查询交易汇总报文记录
        HisMsginLog totalPayInfo = hisMsginLogService.qryTotalMsgByMsgSn(tia1002.body.payApplyNo, "00005");
        // 查询交易子报文记录
        List<HisMsginLog> payInfoList = hisMsginLogService.qrySubMsgsByMsgSnAndTypes(tia1002.body.payApplyNo, payMsgTypes);
        // 检查该笔交易汇总报文记录，若该笔报文已撤销或不存在，则返回交易失败信息
        if (totalPayInfo == null || payInfoList.size() < 1) {
            throw new RuntimeException("该笔交易不存在！");
        } else if (!(totalPayInfo.getTxnAmt1().compareTo(new BigDecimal(tia1002.body.payAmt)) == 0)) {
            throw new RuntimeException("实际交款金额和应交款金额不一致！");
        } else if (TxnCtlSts.TXN_CANCEL.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            throw new RuntimeException("该笔交易已撤销！");
        } else if (TxnCtlSts.TXN_INIT.getCode().equals(totalPayInfo.getTxnCtlSts()) ||
                TxnCtlSts.TXN_HANDLING.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            // 交款交易。
            return handlePayTxn(totalPayInfo, tia1002, payMsgTypes, payInfoList);
        } else if (TxnCtlSts.TXN_SUCCESS.getCode().equals(totalPayInfo.getTxnCtlSts())) {
            // 交易状态已经成功，直接生成成功报文到业务平台
            return getPayInfoDatagram(tia1002, payInfoList);
        } else {
            throw new RuntimeException("该笔交易处理状态不明！");
        }
    }

    /*
      交款交易。
    */
    @Transactional
    private TOA1002 handlePayTxn(HisMsginLog totalMsg, TIA1002 tia1002, String[] payMsgTypes, List<HisMsginLog> payInfoList) throws Exception, IOException {

        // CBS会计账户信息
        HmActinfoCbs hmActinfoCbs = hmActinfoCbsService.qryHmActinfoCbsBySettleActNo(totalMsg.getSettleActno1());
        // 结算户账户信息
        HmActinfoFund hmActinfoSettle = hmActinfoFundService.qryHmActinfoFundByFundActNo(totalMsg.getSettleActno1());
        // 项目核算户账户信息
        HmActinfoFund hmActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(totalMsg.getFundActno1());

        // 修改CBS账户\结算账户\项目核算账户信息：账户余额,若上次记账日不是今日，修改昨日余额为当前账户余额，积数+=上次余额*日期差、上次记帐日 YYYY-MM-DD
        String strToday = SystemService.formatTodayByPattern("YYYY-MM-DD");
        if (!strToday.equals(hmActinfoCbs.getLastTxnDt())) {
            long days = SystemService.daysBetween(strToday, hmActinfoCbs.getLastTxnDt(), "YYYY-MM-DD");
            hmActinfoCbs.setIntcPdt(hmActinfoCbs.getIntcPdt()
                    .add(hmActinfoCbs.getLastActBal().multiply(new BigDecimal(days))));
            hmActinfoCbs.setLastActBal(hmActinfoCbs.getActBal());
            hmActinfoCbs.setLastTxnDt(strToday);
            hmActinfoSettle.setIntcPdt(hmActinfoSettle.getIntcPdt()
                    .add(hmActinfoSettle.getLastActBal().multiply(new BigDecimal(days))));
            hmActinfoSettle.setLastActBal(hmActinfoSettle.getActBal());
            hmActinfoSettle.setLastTxnDt(strToday);
            hmActinfoFund.setIntcPdt(hmActinfoFund.getIntcPdt()
                    .add(hmActinfoFund.getLastActBal().multiply(new BigDecimal(days))));
            hmActinfoFund.setLastActBal(hmActinfoFund.getActBal());
            hmActinfoFund.setLastTxnDt(strToday);
        }
        hmActinfoCbs.setActBal(hmActinfoCbs.getActBal().add(new BigDecimal(tia1002.body.payAmt)));
        hmActinfoSettle.setActBal(hmActinfoSettle.getActBal().add(new BigDecimal(tia1002.body.payAmt)));
        hmActinfoFund.setActBal(hmActinfoFund.getActBal().add(new BigDecimal(tia1002.body.payAmt)));

        // 更新会计账号信息
        hmActinfoCbsMapper.updateByPrimaryKey(hmActinfoCbs);
        // 更新结算账号信息
        hmActinfoFundMapper.updateByPrimaryKey(hmActinfoSettle);
        // 更新一级核算账户
        hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);

        // 新增CBS账户交易明细记录
        TxnCbsLog txnCbsLog = new TxnCbsLog();
        txnCbsLog.setPkid(UUID.randomUUID().toString());
        txnCbsLog.setTxnSn(tia1002.body.txnSerialNo);
        txnCbsLog.setTxnSubSn("00001");
        txnCbsLog.setTxnDate(SystemService.formatTodayByPattern("YYYYMMDD"));
        txnCbsLog.setTxnTime(SystemService.formatTodayByPattern("HHMMSS"));
        txnCbsLog.setTxnCode("1002");
        txnCbsLog.setCbsAcctno(hmActinfoCbs.getCbsActno());
        txnCbsLog.setOpacBrid(hmActinfoCbs.getBranchId());
        txnCbsLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
        txnCbsLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
        txnCbsLog.setLastActBal(hmActinfoCbs.getLastActBal());
        txnCbsLogMapper.insertSelective(txnCbsLog);

        // 新增结算账号交易明细记录
        TxnFundLog txnSettleLog = new TxnFundLog();
        txnSettleLog.setPkid(UUID.randomUUID().toString());
        txnSettleLog.setFundActno(hmActinfoSettle.getFundActno1());
        txnSettleLog.setFundActtype(hmActinfoSettle.getFundActtype1());
        txnSettleLog.setTxnSn(totalMsg.getMsgSn());
        txnSettleLog.setTxnSubSn("00001");
        txnSettleLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
        txnSettleLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
        txnSettleLog.setLastActBal(hmActinfoSettle.getLastActBal());
        txnSettleLog.setTxnDate(SystemService.formatTodayByPattern("YYYYMMDD"));
        txnSettleLog.setTxnTime(SystemService.formatTodayByPattern("HHMMSS"));
        txnSettleLog.setTxnCode("1002");
        txnSettleLog.setActionCode("115");
        txnSettleLog.setActionCode("115");
        txnFundLogMapper.insertSelective(txnSettleLog);


        // 新增一级核算账户交易明细记录
        TxnFundLog txnFundLog = new TxnFundLog();
        txnFundLog.setPkid(UUID.randomUUID().toString());
        txnFundLog.setFundActno(hmActinfoFund.getFundActno1());
        txnFundLog.setFundActtype(hmActinfoFund.getFundActtype1());
        txnFundLog.setTxnSn(totalMsg.getMsgSn());
        txnFundLog.setTxnSubSn("00001");
        txnFundLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
        txnFundLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
        txnFundLog.setLastActBal(hmActinfoFund.getLastActBal());
        txnFundLog.setTxnDate(SystemService.formatTodayByPattern("YYYYMMDD"));
        txnFundLog.setTxnTime(SystemService.formatTodayByPattern("HHMMSS"));
        txnFundLog.setTxnCode("1002");
        txnFundLog.setActionCode("115");
        txnFundLog.setActionCode("115");
        txnFundLogMapper.insertSelective(txnFundLog);


        // 业主核算户账户信息更新
        for (HisMsginLog subPayMsg : payInfoList) {
            HmActinfoFund subActinfoFund = hmActinfoFundService.qryHmActinfoFundByFundActNo(subPayMsg.getFundActno1());
            if (!strToday.equals(subActinfoFund.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActinfoCbs.getLastTxnDt(), "YYYY-MM-DD");
                subActinfoFund.setIntcPdt(subActinfoFund.getIntcPdt()
                        .add(subActinfoFund.getLastActBal().multiply(new BigDecimal(days))));
                subActinfoFund.setLastActBal(subActinfoFund.getActBal());
                subActinfoFund.setLastTxnDt(strToday);
            }
            hmActinfoFund.setActBal(hmActinfoFund.getActBal().add(subPayMsg.getTxnAmt1()));
            hmActinfoFundMapper.updateByPrimaryKey(subActinfoFund);

            // 新增业主核算户交易明细记录
            TxnFundLog txnSubFundLog = new TxnFundLog();
            txnSubFundLog.setPkid(UUID.randomUUID().toString());
            txnSubFundLog.setFundActno(subPayMsg.getFundActno1());
            txnSubFundLog.setFundActtype(subPayMsg.getFundActtype1());
            txnSubFundLog.setTxnSn(totalMsg.getMsgSn());
            txnSubFundLog.setTxnSubSn("00001");
            txnSubFundLog.setTxnAmt(new BigDecimal(tia1002.body.payAmt));
            txnSubFundLog.setDcFlag(DCFlagCode.TXN_IN.getCode());
            txnSubFundLog.setLastActBal(subActinfoFund.getLastActBal());
            txnSubFundLog.setTxnDate(SystemService.formatTodayByPattern("YYYYMMDD"));
            txnSubFundLog.setTxnTime(SystemService.formatTodayByPattern("HHMMSS"));
            txnSubFundLog.setTxnCode("1002");
            txnSubFundLog.setActionCode("115");
            txnSubFundLog.setActionCode("115");
            txnFundLogMapper.insertSelective(txnSubFundLog);
        }
        hisMsginLogService.updateMsginsTxnCtlStsByMsgSnAndTypes(tia1002.body.payApplyNo, "00005", payMsgTypes, TxnCtlSts.TXN_SUCCESS);

        // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
        // TODO 组装8583 报文
        byte[] bytes = null;

        socketBlockClient = new XSocketBlockClient(hmfsServerIP, hmfsServerPort, hmfsServerTimeout);
        byte[] hmfsDatagram = socketBlockClient.sendDataUntilRcv(bytes, 7);
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(hmfsDatagram);
        // TODO 解析8583报文
        logger.info((String) rtnMap.keySet().toArray()[0]);
        return getPayInfoDatagram(tia1002, payInfoList);
    }

    // TODO 调用8583接口处理发送报文 发送至房管局并解析返回结果
    private boolean notifyHmb() {
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
                record.houseArea = hisMsginLog.getBuilderArea().toString();
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
