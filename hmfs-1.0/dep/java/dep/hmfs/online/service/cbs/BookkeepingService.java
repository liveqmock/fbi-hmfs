package dep.hmfs.online.service.cbs;

import common.enums.DCFlagCode;
import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.dao.TxnCbsLogMapper;
import common.repository.hmfs.dao.TxnFundLogMapper;
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
 * Time: ����10:26
 * To change this template use File | Settings | File Templates.
 */
// ����ҵ��
@Service
public class BookkeepingService {

    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;
    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;
    @Autowired
    private TxnCbsLogMapper txnCbsLogMapper;
    @Autowired
    private TxnFundLogMapper txnFundLogMapper;
    @Autowired
    private HmbActinfoService hmbActinfoService;

    // ���»���˺���Ϣ
    @Transactional
    public int cbsActBookkeeping(String cbsSerialNo, BigDecimal amt, String dc) throws ParseException {
        HmActinfoCbs hmActinfoCbs = hmbActinfoService.getFirstHmActinfoCbs();
        return cbsActUpdate(hmActinfoCbs, amt, dc) + addTxnCbsLog(cbsSerialNo, hmActinfoCbs, amt, dc);
    }

    // [����]�����ӱ������ݸ��º����˻���Ϣ
    @Transactional
    public int fundActBookkeepingByMsgins(List<HisMsginLog> msginLogList, String dc) throws ParseException {
        int cnt = 0;
        for (HisMsginLog msginLog : msginLogList) {
            cnt += fundActBookkeepingByMsgin(msginLog, dc);
        }
        return cnt;
    }

    // �����ӱ������ݸ��º����˻���Ϣ  ��FundActno1-��Ŀ�ֻ��� ��FundActno2-��Ŀ���㻧�� ��SettleActno1-�����˻���
    @Transactional
    private int fundActBookkeepingByMsgin(HisMsginLog msginLog, String dc) throws ParseException {
        int fund1Rlt = fundActBookkeeping(msginLog.getMsgSn(), msginLog.getFundActno1(), msginLog.getTxnAmt1(),
                dc, msginLog.getActionCode());
        int fund2Rlt = fundActBookkeeping(msginLog.getMsgSn(), msginLog.getFundActno2(), msginLog.getTxnAmt1(),
                dc, msginLog.getActionCode());
        int setl1Rlt = fundActBookkeeping(msginLog.getMsgSn(), msginLog.getSettleActno1(), msginLog.getTxnAmt1(),
                dc, msginLog.getActionCode());
        return fund1Rlt + fund2Rlt + setl1Rlt;
    }

    //���㻧���� ��������ˮ
    @Transactional
    public int fundActBookkeeping(String msgSn, String fundActno, BigDecimal amt, String dc, String actionCode) throws ParseException {
        HmActinfoFund hmActinfoFund = hmbActinfoService.qryHmActinfoFundByFundActNo(fundActno);
        if (hmActinfoFund == null) {
            return 0;
        }
        return fundActUpdate(hmActinfoFund, amt, dc) + addTxnFundLog(msgSn, hmActinfoFund, amt, dc, actionCode);
    }

    @Transactional
    private int cbsActUpdate(HmActinfoCbs hmActinfoCbs, BigDecimal amt, String dc) throws ParseException {

        String strToday = SystemService.formatTodayByPattern("yyyyMMdd");
        if (!strToday.equals(hmActinfoCbs.getLastTxnDt())) {
            if (!StringUtils.isEmpty(hmActinfoCbs.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActinfoCbs.getLastTxnDt(), "yyyyMMdd");
                hmActinfoCbs.setIntcPdt(hmActinfoCbs.getIntcPdt()
                        .add(hmActinfoCbs.getLastActBal().multiply(new BigDecimal(days))));
            } else {
                hmActinfoCbs.setIntcPdt(new BigDecimal(0));
            }
            hmActinfoCbs.setLastActBal(hmActinfoCbs.getActBal());
            hmActinfoCbs.setLastTxnDt(strToday);
        }
        if (DCFlagCode.TXN_IN.getCode().equalsIgnoreCase(dc)) {
            hmActinfoCbs.setActBal(hmActinfoCbs.getActBal().add(amt));
        } else if (DCFlagCode.TXN_OUT.getCode().equalsIgnoreCase(dc)) {
            hmActinfoCbs.setActBal(hmActinfoCbs.getActBal().subtract(amt));
            if (hmActinfoCbs.getActBal().compareTo(new BigDecimal(0)) < 0) {
                throw new RuntimeException("����˺����㣡");
            }
        }
        // ���»���˺���Ϣ
        return hmActinfoCbsMapper.updateByPrimaryKey(hmActinfoCbs);
    }

    @Transactional
    private int addTxnCbsLog(String cbsSerialNo, HmActinfoCbs hmActinfoCbs, BigDecimal amt, String dc) {
        // ����CBS�˻�������ϸ��¼
        TxnCbsLog txnCbsLog = new TxnCbsLog();
        txnCbsLog.setPkid(UUID.randomUUID().toString());
        txnCbsLog.setTxnSn(cbsSerialNo);
        txnCbsLog.setTxnSubSn("00001");
        txnCbsLog.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        txnCbsLog.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        txnCbsLog.setTxnCode("1002");
        txnCbsLog.setCbsAcctno(hmActinfoCbs.getCbsActno());
        txnCbsLog.setOpacBrid(hmActinfoCbs.getBranchId());
        txnCbsLog.setTxnAmt(amt);
        txnCbsLog.setDcFlag(dc);
        txnCbsLog.setReverseFlag("0");
        txnCbsLog.setLastActBal(hmActinfoCbs.getLastActBal());
        return txnCbsLogMapper.insertSelective(txnCbsLog);
    }

    // ���㻧��Ϣ���
    @Transactional
    private int fundActUpdate(HmActinfoFund hmActinfoFund, BigDecimal amt, String dc) throws ParseException {

        String strToday = SystemService.formatTodayByPattern("yyyyMMdd");
        if (!strToday.equals(hmActinfoFund.getLastTxnDt())) {
            if (!StringUtils.isEmpty(hmActinfoFund.getLastTxnDt())) {
                long days = SystemService.daysBetween(strToday, hmActinfoFund.getLastTxnDt(), "yyyyMMdd");
                hmActinfoFund.setIntcPdt(hmActinfoFund.getIntcPdt()
                        .add(hmActinfoFund.getLastActBal().multiply(new BigDecimal(days))));
            } else {
                hmActinfoFund.setIntcPdt(new BigDecimal(0));
            }
            hmActinfoFund.setLastActBal(hmActinfoFund.getActBal());
            hmActinfoFund.setLastTxnDt(strToday);
        }
        if (DCFlagCode.TXN_IN.getCode().equalsIgnoreCase(dc)) {
            hmActinfoFund.setActBal(hmActinfoFund.getActBal().add(amt));
        } else if (DCFlagCode.TXN_OUT.getCode().equalsIgnoreCase(dc)) {
            hmActinfoFund.setActBal(hmActinfoFund.getActBal().subtract(amt));
            if (hmActinfoFund.getActBal().compareTo(new BigDecimal(0)) < 0) {
                throw new RuntimeException("�����˻����㣡");
            }
        }
        // ���º����˻���Ϣ
        return hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
    }

    // ���������˻�������ϸ��¼
    @Transactional
    private int addTxnFundLog(String msgSn, HmActinfoFund hmActinfoFund, BigDecimal amt, String dc, String actionCode) {

        TxnFundLog txnFundLog = new TxnFundLog();
        txnFundLog.setPkid(UUID.randomUUID().toString());
        txnFundLog.setFundActno(hmActinfoFund.getFundActno1());
        txnFundLog.setFundActtype(hmActinfoFund.getFundActtype1());
        txnFundLog.setTxnSn(msgSn);
        txnFundLog.setTxnSubSn("00001");
        txnFundLog.setTxnAmt(amt);
        txnFundLog.setDcFlag(dc);
        txnFundLog.setLastActBal(hmActinfoFund.getLastActBal());
        txnFundLog.setTxnDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        txnFundLog.setTxnTime(SystemService.formatTodayByPattern("HHmmss"));
        txnFundLog.setTxnCode("1002");
        txnFundLog.setActionCode(actionCode);
        return txnFundLogMapper.insertSelective(txnFundLog);
    }
}
