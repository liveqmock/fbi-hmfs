package dep.hmfs.online.service.hmb;

import common.enums.DCFlagCode;
import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.HmActinfoCbsMapper;
import common.repository.hmfs.dao.HmActinfoFundMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.hmfs.online.service.cbs.BookkeepingService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-11
 * Time: ����12:44
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbActinfoService {

    @Autowired
    private HmActinfoFundMapper hmActinfoFundMapper;
    @Autowired
    private BookkeepingService bookkeepingService;

    @Autowired
    private HmActinfoCbsMapper hmActinfoCbsMapper;

    public HmActinfoCbs getFirstHmActinfoCbs() {
        return hmActinfoCbsMapper.selectByExample(new HmActinfoCbsExample()).get(0);
    }

    public HmActinfoCbs qryHmActinfoCbsByNo(String cbsActNo) {
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria().andCbsActnoEqualTo(cbsActNo);
        List<HmActinfoCbs> actinfoCbsList = hmActinfoCbsMapper.selectByExample(example);
        return actinfoCbsList.size() > 0 ? actinfoCbsList.get(0) : null;
    }

    private int createActinfoCbsByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {

        HmActinfoCbs actinfoCbs = new HmActinfoCbs();

        actinfoCbs.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actinfoCbs, hmbMsg);
        actinfoCbs.setActSts("0");
        actinfoCbs.setActBal(new BigDecimal(0));
        actinfoCbs.setIntcPdt(new BigDecimal(0));
        actinfoCbs.setOpenActDate(SystemService.formatTodayByPattern("yyyyMMdd"));
        actinfoCbs.setRecversion(0);
        return hmActinfoCbsMapper.insert(actinfoCbs);
    }

    public HmActinfoFund qryHmActinfoFundByFundActNo(String fundActNo) {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActNo);
        List<HmActinfoFund> actinfoFundList = hmActinfoFundMapper.selectByExample(example);
        if (actinfoFundList.size() != 1) {
            throw new RuntimeException("δ��ѯ���ú��㻧��¼���ѯ������˻��������㻧�š���" + fundActNo);
        } else {
            return actinfoFundList.get(0);
        }
    }

    public boolean isExistFundActNo(String fundActno) {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActno);
        return hmActinfoFundMapper.countByExample(example) > 0;
    }

    @Transactional
    public int createActinfoFundsByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01033".equals(hmbMsg.getMsgType())) {
                Msg033 msg033 = (Msg033) hmbMsg;
                createActinfoFundByHmbMsg(msg033);
            } else if ("01034".equals(hmbMsg.getMsgType())) {
                Msg034 msg034 = (Msg034) hmbMsg;
                createActinfoFundByHmbMsg(msg034);
            } else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + hmbMsg.getMsgType() + "��");
            }
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int updateActinfoFundsByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            // ���㻧��Ϣ���
            if ("01033".equals(hmbMsg.getMsgType())) {
                Msg033 msg033 = (Msg033) hmbMsg;
                updateActinfosByMsg(msg033);
            } else if ("01051".equals(hmbMsg.getMsgType())) {
                Msg051 msg051 = (Msg051) hmbMsg;
                HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msg051.fundActno1);
                hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + hmbMsg.getMsgType() + "��");
            }
        }
        return hmbMsgList.size();
    }
    
    @Transactional
    public int updateActinfoFundsByMsginList(List<HisMsginLog> fundInfoList) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for(HisMsginLog msginLog : fundInfoList) {
            if ("01033".equals(msginLog.getMsgType())) {
                 updateActinfosByMsginLog(msginLog);
             } else if ("01051".equals(msginLog.getMsgType())) {
                 HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msginLog.getFundActno1());
                 hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                 hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
             } else {
                 throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + msginLog.getMsgType() + "��");
             }  
        }
        return fundInfoList.size();
    }

    private int updateActinfosByMsg(Msg033 msg033) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msg033.fundActno1);
        BeanUtils.copyProperties(hmActinfoFund, msg033);
        return hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
    }

    private int updateActinfosByMsginLog(HisMsginLog msginLog) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msginLog.getFundActno1());
            PropertyUtils.copyProperties(hmActinfoFund, msginLog);
            return hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
        }

    public int createActinfoFundByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFund actinfoFund = new HmActinfoFund();
        actinfoFund.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actinfoFund, hmbMsg);
        if (isExistFundActNo(actinfoFund.getFundActno1())) {
            return 1;
        }
        String today = SystemService.formatTodayByPattern("yyyyMMdd");
        BigDecimal zero = new BigDecimal(0);
        actinfoFund.setActSts("0");
        actinfoFund.setLastActBal(zero);
        actinfoFund.setLastTxnDt(today);
        actinfoFund.setActBal(zero);
        actinfoFund.setIntcPdt(zero);
        actinfoFund.setOpenActDate(today);
        actinfoFund.setRecversion(0);
        return hmActinfoFundMapper.insert(actinfoFund);
    }

    @Transactional
    public int cancelActinfoFundsByMsgList(List<HmbMsg> hmbMsgList) {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01051".equals(hmbMsg.getMsgType())) {
                Msg051 msg051 = (Msg051) hmbMsg;
                HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msg051.fundActno1);
                if (hmActinfoFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
                    throw new RuntimeException("�ú��㻧" + msg051.fundActno1 + "�˻�������������������");
                }
                hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + hmbMsg.getMsgType() + "��");
            }
        }
        return hmbMsgList.size();
    }

    /**
     * ��Ŀ��ֺϲ�
     */


    //���º����ʻ���Ϣ (�������»�)
    @Transactional
    public void op145changeFundActinfo(Msg033 msg) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andFundActno1EqualTo(msg.fundActno1);
        List<HmActinfoFund> actinfoFundList = hmActinfoFundMapper.selectByExample(example);
        if (actinfoFundList.size() == 0) {
            throw new RuntimeException("�˻���Ϣ�����ڡ�");
        }
        //TODO  ��������
        HmActinfoFund actinfoFund = actinfoFundList.get(0);
        int recversion = actinfoFund.getRecversion() + 1;
        BeanUtils.copyProperties(actinfoFund, msg);
        actinfoFund.setRemark("TXN6210 ��Ŀ��ֺϲ�" + new Date().toString());
        actinfoFund.setRecversion(recversion);
        hmActinfoFundMapper.updateByPrimaryKey(actinfoFund);
    }


    //125:ȡ������
    public void op125cancelActinfoFunds(String msgSn, Msg051 msg051) throws ParseException {
        //����
        HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msg051.fundActno1);
        hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
        //ȡ��������
        bookkeepingService.fundActBookkeeping(msgSn, msg051.fundActno1, msg051.actBal, "D", "125", "125");
        if (!"#".equals(msg051.fundActno2.trim())) {
            bookkeepingService.fundActBookkeeping(msgSn, msg051.fundActno2, msg051.actBal, "D", "125", "125");
        }
    }


    //115:���
    @Transactional
    public void op115deposite(String msgSn, Msg035 msg035) throws ParseException {
        // ����˺ż���
        bookkeepingService.cbsActBookkeeping("HMB" + SystemService.formatTodayByPattern("yyyyMMddHHmmss"),
                msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115");

        // �������㻧�˻���Ϣ����
        bookkeepingService.fundActBookkeeping(msgSn, msg035.fundActno1, msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115", "115");
        if (!"#".equals(msg035.fundActno2.trim())) {
            bookkeepingService.fundActBookkeeping(msgSn, msg035.fundActno2, msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115", "115");
        }
    }
}
