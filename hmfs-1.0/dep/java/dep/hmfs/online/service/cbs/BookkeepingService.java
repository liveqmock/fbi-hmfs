package dep.hmfs.online.service.cbs;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.repository.hmfs.dao.HmActStlMapper;
import common.repository.hmfs.dao.HmActFundMapper;
import common.repository.hmfs.dao.HmTxnStlMapper;
import common.repository.hmfs.dao.HmTxnFundMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.hmfs.online.service.hmb.HmbActinfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
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
public class BookkeepingService {

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

    // 更新会计账号信息
    @Transactional
    public int cbsActBookkeeping(String cbsSerialNo, BigDecimal amt, String dc, String cbsTxnCode) throws ParseException {
        HmActStl hmActStl = hmbActinfoService.getFirstHmActinfoCbs();
        return cbsActUpdate(hmActStl, amt, dc) + addTxnCbsLog(cbsSerialNo, hmActStl, amt, dc, cbsTxnCode);
    }

    // [批量]根据子报文内容更新核算账户信息
    @Transactional
    public int fundActBookkeepingByMsgins(List<HmMsgIn> msginLogList, String dc, String cbsTxnCode) throws ParseException {
        int cnt = 0;
        for (HmMsgIn msginLog : msginLogList) {
            cnt++;
            fundActBookkeepingByMsgin(msginLog, cnt, dc, cbsTxnCode);
        }
        return cnt;
    }

    // 根据子报文内容更新核算账户信息  【FundActno1-项目分户】 【FundActno2-项目核算户】 【SettleActno1-结算账户】
    @Transactional
    private int fundActBookkeepingByMsgin(HmMsgIn msginLog, int txnSubSn, String dc, String cbsTxnCode) throws ParseException {
        int fund1Rlt = fundActBookkeeping(msginLog.getMsgSn(), txnSubSn, msginLog.getFundActno1(), msginLog.getTxnAmt1(),
                dc, cbsTxnCode, msginLog.getActionCode());
        int fund2Rlt = fundActBookkeeping(msginLog.getMsgSn(), txnSubSn, msginLog.getFundActno2(), msginLog.getTxnAmt1(),
                dc, cbsTxnCode, msginLog.getActionCode());
        int setl1Rlt = fundActBookkeeping(msginLog.getMsgSn(), txnSubSn, msginLog.getSettleActno1(), msginLog.getTxnAmt1(),
                dc, cbsTxnCode, msginLog.getActionCode());
        return fund1Rlt + fund2Rlt + setl1Rlt;
    }

    //核算户记帐 处理余额及流水
    @Transactional
    public int fundActBookkeeping(String msgSn, int txnSubSn, String fundActno, BigDecimal amt, String dc, String cbsTxnCode, String actionCode) throws ParseException {
        HmActFund hmActFund = hmbActinfoService.qryHmActinfoFundByFundActNo(fundActno);
        if (hmActFund == null) {
            return 0;
        }
        return fundActUpdate(hmActFund, amt, dc) + addTxnFundLog(msgSn, txnSubSn, hmActFund, amt, dc, cbsTxnCode, actionCode);
    }

    @Transactional
    private int cbsActUpdate(HmActStl hmActStl, BigDecimal amt, String dc) throws ParseException {

        String strToday = SystemService.formatTodayByPattern("yyyyMMdd");
        if (!strToday.equals(hmActStl.getLastTxnDt())) {
            if (!StringUtils.isEmpty(hmActStl.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActStl.getLastTxnDt(), "yyyyMMdd");
                hmActStl.setIntcPdt(hmActStl.getIntcPdt()
                        .add(hmActStl.getLastActBal().multiply(new BigDecimal(days))));
            } else {
                hmActStl.setIntcPdt(new BigDecimal(0));
            }
            hmActStl.setLastActBal(hmActStl.getActBal());
            hmActStl.setLastTxnDt(strToday);
        }
        if (DCFlagCode.TXN_IN.getCode().equalsIgnoreCase(dc)) {
            hmActStl.setActBal(hmActStl.getActBal().add(amt));
        } else if (DCFlagCode.TXN_OUT.getCode().equalsIgnoreCase(dc)) {
            hmActStl.setActBal(hmActStl.getActBal().subtract(amt));
            if (hmActStl.getActBal().compareTo(new BigDecimal(0)) < 0) {
                throw new RuntimeException(CbsErrorCode.CBS_ACT_BAL_LESS.getCode());
            }
        }
        // 更新会计账号信息
        return hmActStlMapper.updateByPrimaryKey(hmActStl);
    }

    @Transactional
    private int addTxnCbsLog(String cbsSerialNo, HmActStl hmActStl, BigDecimal amt, String dc, String cbsTxnCode) {
        // 新增CBS账户交易明细记录
        HmTxnStl hmTxnStl = new HmTxnStl();
        hmTxnStl.setPkid(UUID.randomUUID().toString());
        hmTxnStl.setTxnSn(cbsSerialNo);
        hmTxnStl.setTxnSubSn("00001");
        hmTxnStl.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        hmTxnStl.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        hmTxnStl.setTxnCode(cbsTxnCode);
        hmTxnStl.setCbsAcctno(hmActStl.getCbsActno());
        hmTxnStl.setOpacBrid(hmActStl.getBranchId());
        hmTxnStl.setTxnAmt(amt);
        hmTxnStl.setDcFlag(dc);
        hmTxnStl.setReverseFlag("0");
        hmTxnStl.setLastActBal(hmActStl.getLastActBal());
        return hmTxnStlMapper.insertSelective(hmTxnStl);
    }

    // 核算户信息变更
    @Transactional
    private int fundActUpdate(HmActFund hmActFund, BigDecimal amt, String dc) throws ParseException {

        String strToday = SystemService.formatTodayByPattern("yyyyMMdd");
        if (!strToday.equals(hmActFund.getLastTxnDt())) {
            if (!StringUtils.isEmpty(hmActFund.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActFund.getLastTxnDt(), "yyyyMMdd");
                hmActFund.setIntcPdt(hmActFund.getIntcPdt()
                        .add(hmActFund.getLastActBal().multiply(new BigDecimal(days))));
            } else {
                hmActFund.setIntcPdt(new BigDecimal(0));
            }
            hmActFund.setLastActBal(hmActFund.getActBal());
            hmActFund.setLastTxnDt(strToday);
        }
        if (DCFlagCode.TXN_IN.getCode().equalsIgnoreCase(dc)) {
            hmActFund.setActBal(hmActFund.getActBal().add(amt));
        } else if (DCFlagCode.TXN_OUT.getCode().equalsIgnoreCase(dc)) {
            hmActFund.setActBal(hmActFund.getActBal().subtract(amt));
            if (hmActFund.getActBal().compareTo(new BigDecimal(0)) < 0) {
                throw new RuntimeException(CbsErrorCode.FUND_ACT_BAL_LESS.getCode());
            }
        }
        // 更新核算账户信息
        return hmActFundMapper.updateByPrimaryKey(hmActFund);
    }

    // 新增核算账户交易明细记录
    @Transactional
    private int addTxnFundLog(String msgSn, int txnSubSn, HmActFund hmActFund, BigDecimal amt, String dc, String cbsTxnCode, String actionCode) {

        HmTxnFund hmTxnFund = new HmTxnFund();
        hmTxnFund.setPkid(UUID.randomUUID().toString());
        hmTxnFund.setFundActno(hmActFund.getFundActno1());
        hmTxnFund.setFundActtype(hmActFund.getFundActtype1());
        hmTxnFund.setTxnSn(msgSn);
        hmTxnFund.setTxnSubSn(String.valueOf(txnSubSn));
        hmTxnFund.setTxnAmt(amt);
        hmTxnFund.setDcFlag(dc);
        hmTxnFund.setLastActBal(hmActFund.getLastActBal());
        hmTxnFund.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        hmTxnFund.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        hmTxnFund.setTxnCode(cbsTxnCode);
        hmTxnFund.setReverseFlag("0");
        hmTxnFund.setActionCode(actionCode);
        return hmTxnFundMapper.insertSelective(hmTxnFund);
    }
}
