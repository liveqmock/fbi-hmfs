package dep.hmfs.online.service.hmb;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HmActFundMapper;
import common.repository.hmfs.dao.HmActStlMapper;
import common.repository.hmfs.dao.HmTxnFundMapper;
import common.repository.hmfs.dao.HmTxnStlMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-12
 * Time: 上午10:26
 * To change this template use File | Settings | File Templates.
 */
// 记账业务
@Service
public class ActBookkeepingService {

    private static final Logger logger = LoggerFactory.getLogger(ActBookkeepingService.class);

    @Autowired
    private HmActStlMapper hmActStlMapper;
    @Autowired
    private HmActFundMapper hmActFundMapper;
    @Autowired
    private HmTxnStlMapper hmTxnStlMapper;
    @Autowired
    private HmTxnFundMapper hmTxnFundMapper;
    @Autowired
    private HmbActinfoService hmbActinfoService;


    // 检查汇总报文和子报文信息
    public boolean checkMsginTxnCtlSts(HmMsgIn totalInfo, List<HmMsgIn> detailInfoList, BigDecimal txnTotalAmt) {
        if (totalInfo == null || detailInfoList.size() < 1) {
            throw new RuntimeException(CbsErrorCode.TXN_NOT_EXIST.getCode());
        } else if (!(totalInfo.getTxnAmt1().compareTo(txnTotalAmt) == 0)) {
            throw new RuntimeException(CbsErrorCode.TXN_NO_EQUAL.getCode());
        } else if (TxnCtlSts.CANCEL.getCode().equals(totalInfo.getTxnCtlSts())) {
            throw new RuntimeException(CbsErrorCode.TXN_CANCELED.getCode());
        } else if (TxnCtlSts.INIT.getCode().equals(totalInfo.getTxnCtlSts()) ||
                TxnCtlSts.HANDLING.getCode().equals(totalInfo.getTxnCtlSts())) {
            // 正常进行交易。
            return true;
        } else if (TxnCtlSts.SUCCESS.getCode().equals(totalInfo.getTxnCtlSts())) {
            // 交易已成功
            return false;
        } else {
            throw new RuntimeException(CbsErrorCode.TXN_NOT_KNOWN.getCode());
        }
    }

    // [批量]根据子报文内容更新核算账户信息
    @Transactional
    public int actBookkeepingByMsgins(String cbsTxnSn, String deptCode, String operCode, List<HmMsgIn> msginLogList, String dc, String cbsTxnCode) throws ParseException {
        int cnt = 0;
        for (HmMsgIn msginLog : msginLogList) {
            cnt++;
            actBookkeepingByMsgin(cbsTxnSn, deptCode, operCode, msginLog, cnt, dc, cbsTxnCode);
        }
        return cnt;
    }

    // 根据子报文内容更新账户信息  【FundActno1-项目分户】 【FundActno2-项目核算户】 【SettleActno1-结算账户】
    @Transactional
    private int actBookkeepingByMsgin(String cbsTxnSn, String deptCode, String operCode, HmMsgIn msgin, int txnSubSn, String dc, String cbsTxnCode) throws ParseException {
        int fund1Rlt = fundActBookkeeping(cbsTxnSn, deptCode, operCode, msgin.getMsgSn(), txnSubSn, msgin.getFundActno1(), msgin.getTxnAmt1(),
                dc, cbsTxnCode, msgin.getActionCode());
        int fund2Rlt = fundActBookkeeping(cbsTxnSn, deptCode, operCode, msgin.getMsgSn(), txnSubSn, msgin.getFundActno2(), msgin.getTxnAmt1(),
                dc, cbsTxnCode, msgin.getActionCode());
        int setl1Rlt = stlActBookkeeping(cbsTxnSn, deptCode, operCode, msgin.getMsgSn(), txnSubSn, msgin.getSettleActno1(), msgin.getTxnAmt1(),
                dc, cbsTxnCode);
        return fund1Rlt + fund2Rlt + setl1Rlt;
    }

    //核算户记帐 处理余额及流水
    @Transactional
    public int fundActBookkeeping(String cbsTxnSn, String deptCode, String operCode, String msgSn, int txnSubSn, String fundActno, BigDecimal amt, String dc, String cbsTxnCode, String actionCode) throws ParseException {
        HmActFund hmActFund = hmbActinfoService.qryHmActfundByActNo(fundActno);
        if (hmActFund == null) {
            return 0;
        }
        return fundActUpdate(hmActFund, amt, dc) + addTxnFund(cbsTxnSn, deptCode, operCode, msgSn, txnSubSn, hmActFund, amt, dc, cbsTxnCode, actionCode);
    }

    @Transactional
    public int stlActBookkeeping(String cbsTxnSn, String deptCode, String operCode, String msgSn, int txnSubSn, String stlActno, BigDecimal amt, String dc, String cbsTxnCode) throws ParseException {
        HmActStl hmActStl = hmbActinfoService.qryHmActstlBystlactNo(stlActno);
        if (hmActStl == null) {
            return 0;
        }
        return stlActUpdate(hmActStl, amt, dc) + addTxnStl(cbsTxnSn, deptCode, operCode, msgSn, txnSubSn, hmActStl, amt, dc, cbsTxnCode);
    }

    @Transactional
    private int stlActUpdate(HmActStl hmActStl, BigDecimal amt, String dc) throws ParseException {

        String strToday = SystemService.formatTodayByPattern("yyyyMMdd");
        if (!strToday.equals(hmActStl.getLastTxnDt())) {
            hmActStl.setLastActBal(hmActStl.getActBal());
            if (!StringUtils.isEmpty(hmActStl.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActStl.getLastTxnDt(), "yyyyMMdd");
                hmActStl.setIntcPdt(hmActStl.getIntcPdt()
                        .add(hmActStl.getLastActBal().multiply(new BigDecimal(days))));
            } else {
                hmActStl.setIntcPdt(new BigDecimal(0));
            }
            hmActStl.setLastTxnDt(strToday);
        }
        if (DCFlagCode.DEPOSIT.getCode().equalsIgnoreCase(dc)) {
            hmActStl.setActBal(hmActStl.getActBal().add(amt));
        } else if (DCFlagCode.WITHDRAW.getCode().equalsIgnoreCase(dc)) {
            hmActStl.setActBal(hmActStl.getActBal().subtract(amt));
            if (hmActStl.getActBal().compareTo(new BigDecimal("0.00")) < 0) {
                throw new RuntimeException(CbsErrorCode.CBS_ACT_BAL_LESS.getCode());
            }
        }
        // 更新结算户账号信息
        return updateHmActStl(hmActStl);
//        return hmActStlMapper.updateByPrimaryKey(hmActStl);
    }

    @Transactional
    private int addTxnStl(String cbsTxnSn, String deptCode, String operCode, String msgSn, int subSn, HmActStl hmActStl, BigDecimal amt, String dc, String cbsTxnCode) {
        // 新增结算户账户交易明细记录
        HmTxnStl hmTxnStl = new HmTxnStl();
        hmTxnStl.setPkid(UUID.randomUUID().toString());
        hmTxnStl.setCbsTxnSn(cbsTxnSn);
        hmTxnStl.setTxnSn(msgSn);
        hmTxnStl.setStlActno(hmActStl.getSettleActno1());
        hmTxnStl.setTxnSubSn(subSn);
        hmTxnStl.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        hmTxnStl.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        hmTxnStl.setTxnCode(cbsTxnCode);
        hmTxnStl.setCbsActno(hmActStl.getCbsActno());
        hmTxnStl.setOpacBrid(hmActStl.getBranchId());
        hmTxnStl.setTxnAmt(amt);
        hmTxnStl.setDcFlag(dc);
        hmTxnStl.setReverseFlag("0");
        hmTxnStl.setLastActBal(hmActStl.getLastActBal());

        // 新增网点号和柜员号
        hmTxnStl.setTxacBrid(deptCode);
        hmTxnStl.setOpr1No(operCode);
        hmTxnStl.setOpr2No(operCode);
        return hmTxnStlMapper.insertSelective(hmTxnStl);
    }

    // 核算户信息变更
    @Transactional
    private int fundActUpdate(HmActFund hmActFund, BigDecimal amt, String dc) throws ParseException {

        String strToday = SystemService.formatTodayByPattern("yyyyMMdd");
        if (!strToday.equals(hmActFund.getLastTxnDt())) {
            hmActFund.setLastActBal(hmActFund.getActBal());
            if (!StringUtils.isEmpty(hmActFund.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActFund.getLastTxnDt(), "yyyyMMdd");
                hmActFund.setIntcPdt(hmActFund.getIntcPdt()
                        .add(hmActFund.getLastActBal().multiply(new BigDecimal(days))));
            } else {
                hmActFund.setIntcPdt(new BigDecimal(0));
            }
            hmActFund.setLastTxnDt(strToday);
        }
        if (DCFlagCode.DEPOSIT.getCode().equalsIgnoreCase(dc)) {
            hmActFund.setActBal(amt.add(hmActFund.getActBal()));
        } else if (DCFlagCode.WITHDRAW.getCode().equalsIgnoreCase(dc)) {
            logger.info("核算账户【" + hmActFund.getFundActno1() + "】余额：" + String.format("%.2f", hmActFund.getActBal()) + " 支取金额：" +
                    String.format("%.2f", amt));
            if (hmActFund.getActBal().compareTo(amt) < 0) {
                throw new RuntimeException(CbsErrorCode.FUND_ACT_BAL_LESS.getCode());
            }
            hmActFund.setActBal(hmActFund.getActBal().subtract(amt));
            /*if (hmActFund.getActBal().compareTo(new BigDecimal("0.00")) < 0) {
                throw new RuntimeException(CbsErrorCode.FUND_ACT_BAL_LESS.getCode());
            }*/
        }
        // 更新核算账户信息
        return updateHmActFund(hmActFund);
//        return hmActFundMapper.updateByPrimaryKey(hmActFund);
    }

    // 新增核算账户交易明细记录
    @Transactional
    private int addTxnFund(String cbsTxnSn, String deptCode, String operCode, String msgSn, int txnSubSn, HmActFund hmActFund, BigDecimal amt, String dc, String cbsTxnCode, String actionCode) {

        HmTxnFund hmTxnFund = new HmTxnFund();
        hmTxnFund.setPkid(UUID.randomUUID().toString());
        hmTxnFund.setCbsTxnSn(cbsTxnSn);
        hmTxnFund.setFundActno(hmActFund.getFundActno1());
        hmTxnFund.setFundActtype(hmActFund.getFundActtype1());
        hmTxnFund.setTxnSn(msgSn);
        hmTxnFund.setTxnSubSn(txnSubSn);
        hmTxnFund.setTxnAmt(amt);
        hmTxnFund.setDcFlag(dc);
        hmTxnFund.setLastActBal(hmActFund.getLastActBal());
        hmTxnFund.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        hmTxnFund.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        hmTxnFund.setTxnCode(cbsTxnCode);
        hmTxnFund.setReverseFlag("0");
        hmTxnFund.setActionCode(actionCode);
        // 新增网点号和柜员号
        hmTxnFund.setTxacBrid(deptCode);
        hmTxnFund.setOpr1No(operCode);
        hmTxnFund.setOpr2No(operCode);

        return hmTxnFundMapper.insertSelective(hmTxnFund);
    }

    private int updateHmActFund(HmActFund actfund) {
        HmActFund originRecord = hmActFundMapper.selectByPrimaryKey(actfund.getPkid());
        if (!originRecord.getRecversion().equals(actfund.getRecversion())) {
            logger.info("记录更新版本号：" + actfund.getRecversion());
            logger.info("记录原版本号：" + originRecord.getRecversion());
            throw new RuntimeException("记录并发更新冲突，请重试！");
        } else {
            // 备注中保存更新时间
            actfund.setRemark(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
            actfund.setRecversion(actfund.getRecversion() + 1);
            return hmActFundMapper.updateByPrimaryKey(actfund);
        }
    }

    private int updateHmActStl(HmActStl actstl) {
        HmActStl originRecord = hmActStlMapper.selectByPrimaryKey(actstl.getPkid());
        if (!originRecord.getRecversion().equals(actstl.getRecversion())) {
            logger.info("记录更新版本号：" + actstl.getRecversion());
            logger.info("记录原版本号：" + originRecord.getRecversion());
            throw new RuntimeException("记录并发更新冲突，请重试！");
        } else {
            // 备注中保存更新时间
            actstl.setRemark(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
            actstl.setRecversion(actstl.getRecversion() + 1);
            return hmActStlMapper.updateByPrimaryKey(actstl);
        }
    }
}
