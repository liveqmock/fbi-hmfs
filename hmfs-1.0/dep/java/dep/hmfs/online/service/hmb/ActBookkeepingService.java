package dep.hmfs.online.service.hmb;

import common.enums.CbsErrorCode;
import common.enums.DCFlagCode;
import common.repository.hmfs.dao.HmActFundMapper;
import common.repository.hmfs.dao.HmActStlMapper;
import common.repository.hmfs.dao.HmTxnFundMapper;
import common.repository.hmfs.dao.HmTxnStlMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
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
 * Time: ����10:26
 * To change this template use File | Settings | File Templates.
 */
// ����ҵ��
@Service
public class ActBookkeepingService {

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

    // [����]�����ӱ������ݸ��º����˻���Ϣ
    @Transactional
    public int actBookkeepingByMsgins(String cbsTxnSn, List<HmMsgIn> msginLogList, String dc, String cbsTxnCode) throws ParseException {
        int cnt = 0;
        for (HmMsgIn msginLog : msginLogList) {
            cnt++;
            actBookkeepingByMsgin(cbsTxnSn, msginLog, cnt, dc, cbsTxnCode);
        }
        return cnt;
    }

    // �����ӱ������ݸ����˻���Ϣ  ��FundActno1-��Ŀ�ֻ��� ��FundActno2-��Ŀ���㻧�� ��SettleActno1-�����˻���
    @Transactional
    private int actBookkeepingByMsgin(String cbsTxnSn, HmMsgIn msgin, int txnSubSn, String dc, String cbsTxnCode) throws ParseException {
        int fund1Rlt = fundActBookkeeping(cbsTxnSn, msgin.getMsgSn(), txnSubSn, msgin.getFundActno1(), msgin.getTxnAmt1(),
                dc, cbsTxnCode, msgin.getActionCode());
        int fund2Rlt = fundActBookkeeping(cbsTxnSn, msgin.getMsgSn(), txnSubSn, msgin.getFundActno2(), msgin.getTxnAmt1(),
                dc, cbsTxnCode, msgin.getActionCode());
        int setl1Rlt = stlActBookkeeping(cbsTxnSn, msgin.getMsgSn(), txnSubSn, msgin.getSettleActno1(), msgin.getTxnAmt1(),
                dc, cbsTxnCode);
        return fund1Rlt + fund2Rlt + setl1Rlt;
    }

    //���㻧���� ��������ˮ
    @Transactional
    public int fundActBookkeeping(String cbsTxnSn, String msgSn, int txnSubSn, String fundActno, BigDecimal amt, String dc, String cbsTxnCode, String actionCode) throws ParseException {
        HmActFund hmActFund = hmbActinfoService.qryHmActfundByActNo(fundActno);
        if (hmActFund == null) {
            return 0;
        }
        return fundActUpdate(hmActFund, amt, dc) + addTxnFund(cbsTxnSn, msgSn, txnSubSn, hmActFund, amt, dc, cbsTxnCode, actionCode);
    }

    @Transactional
    public int stlActBookkeeping(String cbsTxnSn, String msgSn, int txnSubSn, String stlActno, BigDecimal amt, String dc, String cbsTxnCode) throws ParseException {
        HmActStl hmActStl = hmbActinfoService.qryHmActstlBystlactNo(stlActno);
        if (hmActStl == null) {
            return 0;
        }
        return stlActUpdate(hmActStl, amt, dc) + addTxnStl(cbsTxnSn, msgSn, txnSubSn, hmActStl, amt, dc, cbsTxnCode);
    }

    @Transactional
    private int stlActUpdate(HmActStl hmActStl, BigDecimal amt, String dc) throws ParseException {

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
        // ���½��㻧�˺���Ϣ
        return hmActStlMapper.updateByPrimaryKey(hmActStl);
    }

    @Transactional
    private int addTxnStl(String cbsTxnSn, String msgSn, int subSn, HmActStl hmActStl, BigDecimal amt, String dc, String cbsTxnCode) {
        // �������㻧�˻�������ϸ��¼
        HmTxnStl hmTxnStl = new HmTxnStl();
        hmTxnStl.setPkid(UUID.randomUUID().toString());
        hmTxnStl.setCbsTxnSn(cbsTxnSn);
        hmTxnStl.setTxnSn(msgSn);
        hmTxnStl.setStlActno(hmActStl.getSettleActno1());
        hmTxnStl.setTxnSubSn(String.valueOf(subSn));
        hmTxnStl.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        hmTxnStl.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        hmTxnStl.setTxnCode(cbsTxnCode);
        hmTxnStl.setCbsActno(hmActStl.getCbsActno());
        hmTxnStl.setOpacBrid(hmActStl.getBranchId());
        hmTxnStl.setTxnAmt(amt);
        hmTxnStl.setDcFlag(dc);
        hmTxnStl.setReverseFlag("0");
        hmTxnStl.setLastActBal(hmActStl.getLastActBal());
        return hmTxnStlMapper.insertSelective(hmTxnStl);
    }

    // ���㻧��Ϣ���
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
        // ���º����˻���Ϣ
        return hmActFundMapper.updateByPrimaryKey(hmActFund);
    }

    // ���������˻�������ϸ��¼
    @Transactional
    private int addTxnFund(String cbsTxnSn, String msgSn, int txnSubSn, HmActFund hmActFund, BigDecimal amt, String dc, String cbsTxnCode, String actionCode) {

        HmTxnFund hmTxnFund = new HmTxnFund();
        hmTxnFund.setPkid(UUID.randomUUID().toString());
        hmTxnFund.setCbsTxnSn(cbsTxnSn);
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
