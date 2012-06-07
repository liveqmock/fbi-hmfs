package dep.hmfs.online.service.hmb;

import common.enums.DCFlagCode;
import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(HmbActinfoService.class);

    @Autowired
    private HmActFundMapper hmActFundMapper;
    @Autowired
    private ActBookkeepingService actBookkeepingService;

    @Autowired
    private HmActStlMapper hmActStlMapper;
    @Autowired
    private HmTxnStlMapper hmTxnStlMapper;
    @Autowired
    private HmChkActMapper hmChkActMapper;
    @Autowired
    private HmChkTxnMapper hmChkTxnMapper;
    @Autowired
    private HmSysCtlMapper hmSysCtlMapper;

    public HmSysCtl getSysCtl() {
        return hmSysCtlMapper.selectByPrimaryKey("1");
    }
    
    @Transactional
    public int deleteCbsChkActByDate(String date8, String cbsActno, String sendSysId) {
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(date8).andActnoEqualTo(cbsActno).andSendSysIdEqualTo(sendSysId);
        return hmChkActMapper.deleteByExample(example);
    }

    @Transactional
    public int insertChkAct(HmChkAct hmChkAct) {
        return hmChkActMapper.insert(hmChkAct);
    }
    
    @Transactional
    public int deleteCbsChkTxnByDate(String date8, String cbsActno, String sendSysId) {
        HmChkTxnExample example = new HmChkTxnExample();
        example.createCriteria().andTxnDateEqualTo(date8).andActnoEqualTo(cbsActno).andSendSysIdEqualTo(sendSysId);
        return hmChkTxnMapper.deleteByExample(example);
    }

    @Transactional
    public int insertChkTxn(HmChkTxn hmChkTxn) {
        return hmChkTxnMapper.insert(hmChkTxn);
    }

    public List<HmTxnStl> qryHmTxnStlForChkAct(String txnDate) {
        List<HmTxnStl> hmTxnStlList = hmTxnStlMapper.qryHmTxnStlForChkAct(txnDate);
        return hmTxnStlList;
    }

    public HmActStl qryHmActstlByCbsactNo(String cbsActNo) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andCbsActnoEqualTo(cbsActNo);
        List<HmActStl> actStlList = hmActStlMapper.selectByExample(example);
        return actStlList.size() > 0 ? actStlList.get(0) : null;
    }

    public HmActStl qryHmActstlBystlactNo(String stlActNo) {
        HmActStlExample example = new HmActStlExample();
        example.createCriteria().andSettleActno1EqualTo(stlActNo);
        List<HmActStl> actStlList = hmActStlMapper.selectByExample(example);
        return actStlList.size() > 0 ? actStlList.get(0) : null;
    }

    public HmActFund qryHmActfundByActNo(String fundActNo) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActNo);
        List<HmActFund> actFundList = hmActFundMapper.selectByExample(example);
        if (actFundList.size() != 1) {
            throw new RuntimeException("δ��ѯ���ú��㻧��¼���ѯ������˻��������㻧�š���" + fundActNo);
        } else {
            return actFundList.get(0);
        }
    }

    public HmActFund qryHmActfundByInfoID(String infoID) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andInfoId1EqualTo(infoID);
        List<HmActFund> actFundList = hmActFundMapper.selectByExample(example);
        if (actFundList.size() != 1) {
            throw new RuntimeException("δ��ѯ���ú��㻧��¼���ѯ������˻�������ϢID����" + infoID);
        } else {
            return actFundList.get(0);
        }
    }

    public boolean isExistFundActNo(String fundActno) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActno);
        return hmActFundMapper.countByExample(example) > 0;
    }

    @Transactional
    public int createActFundsByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01033".equals(hmbMsg.getMsgType())) {
                Msg033 msg033 = (Msg033) hmbMsg;
                createActinfoFundByHmbMsg(msg033);
            } else if ("01034".equals(hmbMsg.getMsgType())) {
                Msg034 msg034 = (Msg034) hmbMsg;
                createActinfoFundByHmbMsg(msg034);
            } /*else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + hmbMsg.getMsgType() + "��");
            }*/
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
                HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
            } /*else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + hmbMsg.getMsgType() + "��");
            }*/
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int updateActinfoFundsByMsginList(List<HmMsgIn> fundInfoList) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (HmMsgIn msginLog : fundInfoList) {
            if ("01033".equals(msginLog.getMsgType())) {
                updateActinfosByMsginLog(msginLog);
            } else if ("01051".equals(msginLog.getMsgType())) {
                HmActFund hmActFund = qryHmActfundByActNo(msginLog.getFundActno1());
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
            } else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + msginLog.getMsgType() + "��");
            }
        }
        return fundInfoList.size();
    }

    private int updateActinfosByMsg(Msg033 msg033) throws InvocationTargetException, IllegalAccessException {
        HmActFund hmActFund = qryHmActfundByInfoID(msg033.infoId1);
        // ��Ϣ����60�����ֻ����㻧
        // �ɸ����ֶ�
        /*
        20:��Ϣ����
        21:��Ϣ����
        22:��Ϣ��ַ
        23:�ֻ���
        24:�������
        	===
        71:�������赥λ����
        76:���ݽ�������
        78:�����׼1
        83:�����׼2
        99:�Ƿ����
        100:¥��
        101:�ź�
        102:�Һ�
        104:֤������
        105:֤�����
        64:��λ��ϵ�绰
        82:������ͬ��
        88:��������ϵ�绰
        93:���޵���
        106:�������ܶ�
         */
        hmActFund.setInfoCode(msg033.infoCode);
        hmActFund.setInfoName(msg033.infoName);
        hmActFund.setInfoAddr(msg033.infoAddr);
        hmActFund.setCellNum(msg033.cellNum);
        hmActFund.setBuilderArea(msg033.builderArea);
        if ("60".equals(msg033.infoIdType1)) {
            hmActFund.setDevOrgName(msg033.devOrgName);
            hmActFund.setHouseDepType(msg033.houseDepType);
            hmActFund.setDepStandard1(msg033.depStandard1);
            hmActFund.setDepStandard2(msg033.depStandard2);
            hmActFund.setSellFlag(msg033.sellFlag);
            hmActFund.setBuildingNo(msg033.buildingNo);
            hmActFund.setUnitNo(msg033.unitNo);
            hmActFund.setRoomNo(msg033.roomNo);
            hmActFund.setCertType(msg033.certType);
            hmActFund.setCertId(msg033.certId);
            hmActFund.setOrgPhone(msg033.orgPhone);
            hmActFund.setHouseContNo(msg033.houseContNo);
            hmActFund.setHouseCustPhone(msg033.houseCustPhone);
            hmActFund.setElevatorType(msg033.elevatorType);
            hmActFund.setHouseTotalAmt(msg033.houseTotalAmt);
            // ��Ϣ����30������Ŀ���㻧
        } else if ("30".equals(msg033.infoIdType1)) {
            // �������ɸ����ֶ�
        } else {
            throw new RuntimeException("��Ϣ���ʹ��󣡡���Ϣ���͡���" + msg033.infoIdType1);
        }
        return hmActFundMapper.updateByPrimaryKey(hmActFund);
    }


    @Transactional
    private int updateActinfosByMsginLog(HmMsgIn msginLog) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        HmActFund hmActFund = qryHmActfundByActNo(msginLog.getFundActno1());
        //PropertyUtils.copyProperties(hmActFund, msginLog);

        hmActFund.setInfoCode(msginLog.getInfoCode());
        hmActFund.setInfoName(msginLog.getInfoName());
        hmActFund.setInfoAddr(msginLog.getInfoAddr());
        hmActFund.setCellNum(msginLog.getCellNum());
        hmActFund.setBuilderArea(msginLog.getBuilderArea());
        if ("60".equals(msginLog.getInfoIdType1())) {
            hmActFund.setDevOrgName(msginLog.getDevOrgName());
            hmActFund.setHouseDepType(msginLog.getHouseDepType());
            hmActFund.setDepStandard1(msginLog.getDepStandard1());
            hmActFund.setDepStandard2(msginLog.getDepStandard2());
            hmActFund.setSellFlag(msginLog.getSellFlag());
            hmActFund.setBuildingNo(msginLog.getBuildingNo());
            hmActFund.setUnitNo(msginLog.getUnitNo());
            hmActFund.setRoomNo(msginLog.getRoomNo());
            hmActFund.setCertType(msginLog.getCertType());
            hmActFund.setCertId(msginLog.getCertId());
            hmActFund.setOrgPhone(msginLog.getOrgPhone());
            hmActFund.setHouseContNo(msginLog.getHouseContNo());
            hmActFund.setHouseCustPhone(msginLog.getHouseCustPhone());
            hmActFund.setElevatorType(msginLog.getElevatorType());
            hmActFund.setHouseTotalAmt(msginLog.getHouseTotalAmt());
            // ��Ϣ����30������Ŀ���㻧
        } else if ("30".equals(msginLog.getInfoIdType1())) {
            // �������ɸ����ֶ�
        } else {
            throw new RuntimeException("��Ϣ���ʹ��󣡡���Ϣ���͡���" + msginLog.getInfoIdType1());
        }
        int cnt = hmActFundMapper.updateByPrimaryKey(hmActFund);
        logger.info("���˿�ʱ���㻧��Ϣ���¡�:" + (cnt == 1));

        return cnt;
    }

    public int createActinfoFundByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {
        HmActFund actFund = new HmActFund();
        actFund.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actFund, hmbMsg);
        if (isExistFundActNo(actFund.getFundActno1())) {
            return 1;
        }
        String today = SystemService.formatTodayByPattern("yyyyMMdd");
        BigDecimal zero = new BigDecimal(0);
        actFund.setActSts("0");
        actFund.setLastActBal(zero);
        actFund.setLastTxnDt(today);
        actFund.setActBal(zero);
        actFund.setIntcPdt(zero);
        actFund.setOpenActDate(today);
        actFund.setRecversion(0);
        return hmActFundMapper.insert(actFund);
    }

    @Transactional
    public int cancelActinfoFundsByMsgList(List<HmbMsg> hmbMsgList) {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01051".equals(hmbMsg.getMsgType())) {
                Msg051 msg051 = (Msg051) hmbMsg;
                //HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
                //2012-05-31  linyong
                HmActFund hmActFund = qryHmActfundByInfoID(msg051.infoId1);
                if (hmActFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
                    //throw new RuntimeException("�ú��㻧" + msg051.fundActno1 + "�˻�������������������");
                    //2012-05-31 linyong
                    throw new RuntimeException("����ϢID" + msg051.infoId1 + "�˻�������������������");
                }
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
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
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(msg.fundActno1);
        List<HmActFund> actFundList = hmActFundMapper.selectByExample(example);
        if (actFundList.size() == 0) {
            throw new RuntimeException("�˻���Ϣ�����ڡ�");
        }
        //TODO  ��������
        HmActFund actFund = actFundList.get(0);
        int recversion = actFund.getRecversion() + 1;
        BeanUtils.copyProperties(actFund, msg);
        actFund.setRemark("TXN6210 ��Ŀ��ֺϲ�" + new Date().toString());
        actFund.setRecversion(recversion);
        hmActFundMapper.updateByPrimaryKey(actFund);
    }


    //125:ȡ������
    public void op125cancelActinfoFunds(String msgSn, Msg051 msg051) throws ParseException {
        //����
        HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
        hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActFundMapper.updateByPrimaryKey(hmActFund);
        //ȡ��������
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno1, msg051.actBal, "D", "125", "125");
        if (!"#".equals(msg051.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno2, msg051.actBal, "D", "125", "125");
        }
    }


    //115:���
    @Transactional
    public void op115deposite(String msgSn, Msg035 msg035) throws ParseException {

        // �������㻧�˻���Ϣ����
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg035.fundActno1, msg035.txnAmt1, DCFlagCode.DEPOSIT.getCode(), "115", "115");
        if (!"#".equals(msg035.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg035.fundActno2, msg035.txnAmt1, DCFlagCode.DEPOSIT.getCode(), "115", "115");
        }
    }
}