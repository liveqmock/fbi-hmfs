package dep.hmfs.online.service.hmb;

import common.enums.DCFlagCode;
import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
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
    private HmTxnFundMapper hmTxnFundMapper;
    @Autowired
    private HmChkActMapper hmChkActMapper;
    @Autowired
    private HmChkTxnMapper hmChkTxnMapper;
    @Autowired
    private HmSysCtlMapper hmSysCtlMapper;
    @Autowired
    private HmActFundDelMapper hmActFundDelMapper;

    public HmSysCtl getSysCtl() {
        return hmSysCtlMapper.selectByPrimaryKey("1");
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

    public List<HmActFund> qryExistFundActNo(String fundActno) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActno);
        return hmActFundMapper.selectByExample(example);
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
                /* hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);*/
                cancelActFund(hmActFund);
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
                /*hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);*/
                cancelActFund(hmActFund);
            } else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + msginLog.getMsgType() + "��");
            }
        }
        return fundInfoList.size();
    }

    private void cancelActFund(HmActFund hmActFund) {
        // 7-27 ��ֻ�ʱ�ֻ�������Ҳ������
        /* if (hmActFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
            throw new RuntimeException("�ú��㻧" + hmActFund.getFundActno1() + "�˻�������������������");
        }*/
        hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActFundMapper.updateByPrimaryKey(hmActFund);
        if (hmActFund.getFundActno2() != null && !"#".equals(hmActFund.getFundActno2().trim())) {
            HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
            hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
            logger.info("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                    + " �����ֻ����㻧�������" + hmActFund.getBuilderArea());
            try {
                hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                        .subtract(new BigDecimal(hmActFund.getBuilderArea().trim())).toString());
            } catch (Exception e) {
                logger.error("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                        + " �����ֻ����㻧�������" + hmActFund.getBuilderArea() + "[���ݸ�ʽ����]", e);
                throw new RuntimeException("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                        + " �����ֻ����㻧�������" + hmActFund.getBuilderArea() + "[���ݸ�ʽ����]");
            }
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        }
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
        BigDecimal fundArea = new BigDecimal(hmActFund.getBuilderArea());
        BigDecimal newArea = new BigDecimal(msg033.builderArea);

        BigDecimal builderArea = new BigDecimal("0.00");
        if (fundArea.compareTo(newArea) > 0) {
            builderArea = fundArea.subtract(newArea);
            HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
            hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea()).subtract(builderArea).toString());
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        } else if (fundArea.compareTo(newArea) < 0) {
            builderArea = newArea.subtract(fundArea);
            HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
            hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea()).add(builderArea).toString());
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        } else {
            // ����ޱ仯
        }
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
        // 2012-07-16 �»�����  �������������Ӻ��㻧��ɾ�������浽hm_act_fund_del
        logger.info("���¿������㻧��Ϣ���˺�:" + actFund.getFundActno1() + "����:" + actFund.getFundActtype1());
        logger.info("���¿������㻧��Ϣ���ϼ��˺�:" + actFund.getFundActno2() + "����:" + actFund.getFundActtype2());
        List<HmActFund> originActFundList = qryExistFundActNo(actFund.getFundActno1());
        if (originActFundList.size() > 0) {
            for (HmActFund record : originActFundList) {
                logger.info("���¿������㻧��Ϣ�������˺�:" + record.getFundActno1() + "����:" + record.getFundActtype1());
                logger.info("���¿������㻧��Ϣ�������˺��ϼ��˻�:" + record.getFundActno1() + "����:" + record.getFundActtype1());

                if (FundActnoStatus.CANCEL.getCode().equals(record.getActSts())) {
                    // TODO
                    HmActFundDel hmActFundDel = new HmActFundDel();
                    try {
                        PropertyUtils.copyProperties(hmActFundDel, record);
                    } catch (NoSuchMethodException e) {
                        logger.error("���㻧" + record.getFundActno1() + "����ʧ�ܡ�", e);
                        throw new RuntimeException("���㻧" + record.getFundActno1() + "����ʧ�ܡ�");
                    }
                    hmActFundDelMapper.insert(hmActFundDel);
                    hmActFundMapper.deleteByPrimaryKey(record.getPkid());
                } else {
                    throw new RuntimeException("���㻧" + record.getFundActno1() + "�Ѵ��ڡ�");
                }
            }
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
        actFund.setInitActBal(zero);
        actFund.setIntAmt(zero);
        actFund.setIncrAmt(zero);
        actFund.setOthInAm(zero);
        actFund.setAgnInAmt(zero);
        actFund.setOutAmt(zero);
        actFund.setIncrOutAmt(zero);
        actFund.setOthOutAmt(zero);
        // 2012-07-10 zhangxiaobo �����ֻ�ʱ ������Ŀ������������ֻ���+1
        if (actFund.getFundActno2() != null && !"#".equals(actFund.getFundActno2().trim())) {

            HmActFund hmActFund = qryHmActfundByActNo(actFund.getFundActno2());
            // ����ϼ���Ŀ���㻧�ֻ���Ϊ0����ô����Ŀ���ķֻ������������Ϊ0��Ȼ���ۼ�
            int cnt = qrySubActfundCnt(actFund.getFundActno2());
            if (cnt == 0) {
                hmActFund.setCellNum("0");
                hmActFund.setBuilderArea("0");
            }
            if (hmActFund != null) {
                hmActFund.setCellNum(String.valueOf(Integer.parseInt(hmActFund.getCellNum().trim()) + 1));
                logger.info("��Ŀ���㻧�������" + hmActFund.getBuilderArea()
                        + " �¿��ֻ����㻧�������" + actFund.getBuilderArea());
                try {
                    hmActFund.setBuilderArea(new BigDecimal(hmActFund.getBuilderArea().trim())
                            .add(new BigDecimal(actFund.getBuilderArea().trim())).toString());
                } catch (Exception e) {
                    logger.error("��Ŀ���㻧�������" + hmActFund.getBuilderArea()
                            + " �¿��ֻ����㻧�������" + actFund.getBuilderArea() + "[���ݸ�ʽ����]");
                }
                hmActFundMapper.updateByPrimaryKey(hmActFund);
            }
        }
        return hmActFundMapper.insert(actFund);
    }

    @Transactional
    public int cancelActinfoIdFundsByMsgList(List<HmbMsg> hmbMsgList) {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01051".equals(hmbMsg.getMsgType())) {
                Msg051 msg051 = (Msg051) hmbMsg;
                //HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
                //2012-05-31  linyong
                HmActFund hmActFund = qryHmActfundByInfoID(msg051.infoId1);
                /*if (hmActFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
                    //throw new RuntimeException("�ú��㻧" + msg051.fundActno1 + "�˻�������������������");
                    //2012-05-31 linyong
                    throw new RuntimeException("����ϢID" + msg051.infoId1 + "�˻�������������������");
                }
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
                // 2012-07-16 zhangxiaobo �ֻ�����ʱ��Ŀ���㻧�ֻ���-1 ������ֻ����
                if (hmActFund.getFundActno2() != null && !"#".equals(hmActFund.getFundActno2().trim())) {
                    HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
                    hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
                    logger.info("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                            + " �����ֻ����㻧�������" + hmActFund.getBuilderArea());
                    try {
                        hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                                .subtract(new BigDecimal(hmActFund.getBuilderArea().trim())).toString());
                    } catch (Exception e) {
                        logger.error("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                                + " �����ֻ����㻧�������" + hmActFund.getBuilderArea() + "[���ݸ�ʽ����]");
                    }
                    hmActFundMapper.updateByPrimaryKey(hmActFund2);
                }*/
                cancelActFund(hmActFund);
            } else {
                throw new RuntimeException("�������к��зǺ��㻧�����ӱ������" + hmbMsg.getMsgType() + "��");
            }
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int cancelActNoFundsByMsgList(List<HmbMsg> hmbMsgList) {
        for (HmbMsg hmbMsg : hmbMsgList) {
            if ("01051".equals(hmbMsg.getMsgType())) {
                Msg051 msg051 = (Msg051) hmbMsg;
                HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
                /*if (hmActFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
                    throw new RuntimeException("�ú��㻧" + msg051.fundActno1 + "�˻�������������������");
                }
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
                if (hmActFund.getFundActno2() != null && !"#".equals(hmActFund.getFundActno2().trim())) {
                    HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
                    hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
                    logger.info("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                            + " �����ֻ����㻧�������" + hmActFund.getBuilderArea());
                    try {
                        hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                                .subtract(new BigDecimal(hmActFund.getBuilderArea().trim())).toString());
                    } catch (Exception e) {
                        logger.error("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                                + " �����ֻ����㻧�������" + hmActFund.getBuilderArea() + "[���ݸ�ʽ����]");
                    }
                    hmActFundMapper.updateByPrimaryKey(hmActFund2);
                }*/
                cancelActFund(hmActFund);
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
    // ��A��Ŀ���µķֻ����ϼ���Ŀ������ΪB��ͬʱ����A�ķֻ��������
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

        // �����ϼ����㻧�ķֻ�����A-��
        if (actFund.getFundActno2() != null && !"#".equals(actFund.getFundActno2().trim())) {
            HmActFund hmActFund2 = qryHmActfundByActNo(actFund.getFundActno2());
            hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
            logger.info("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                    + " -�ֻ����㻧�������" + actFund.getBuilderArea());
            try {
                hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                        .subtract(new BigDecimal(actFund.getBuilderArea().trim())).toString());
            } catch (Exception e) {
                logger.error("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                        + " -�ֻ����㻧�������" + actFund.getBuilderArea() + "[���ݸ�ʽ����]", e);
                throw new RuntimeException("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                        + " -�ֻ����㻧�������" + actFund.getBuilderArea() + "[���ݸ�ʽ����]");
            }
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        }
        // B+
        if (msg.fundActno2 != null && !"#".equals(msg.fundActno2.trim())) {
            HmActFund hmActFund2 = qryHmActfundByActNo(msg.fundActno2);
            hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) + 1));
            logger.info("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                    + " +�ֻ����㻧�������" + actFund.getBuilderArea());
            try {
                hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                        .add(new BigDecimal(actFund.getBuilderArea().trim())).toString());
            } catch (Exception e) {
                logger.error("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                        + " +�ֻ����㻧�������" + actFund.getBuilderArea() + "[���ݸ�ʽ����]", e);
                throw new RuntimeException("��Ŀ���㻧�������" + hmActFund2.getBuilderArea()
                        + " +�ֻ����㻧�������" + actFund.getBuilderArea() + "[���ݸ�ʽ����]");
            }
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        }
        int recversion = actFund.getRecversion() + 1;
        // BeanUtils.copyProperties(actFund, msg);
        actFund.setFundActno2(msg.fundActno2);
        actFund.setRemark("TXN6210 ��Ŀ��ֺϲ�" + new Date().toString());
        actFund.setRecversion(recversion);
        hmActFundMapper.updateByPrimaryKey(actFund);
    }


    //125:ȡ������ 2012-07-27
    public void op125cancelActinfoFunds(String msgSn, Msg051 msg051) throws ParseException {
        //ȡ��������
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno1, msg051.actBal,
                DCFlagCode.WITHDRAW.getCode(), "125", "125");
        if (!"#".equals(msg051.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno2, msg051.actBal,
                    DCFlagCode.WITHDRAW.getCode(), "125", "125");
        }
        //����
        HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
        /*hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActFundMapper.updateByPrimaryKey(hmActFund);*/
        cancelActFund(hmActFund);
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

    // ��������ˮ�Ų�ѯ���㻧������ϸ
    public HmTxnStl qryTxnStlByCbsSn(String cbsSn) {
        HmTxnStlExample example = new HmTxnStlExample();
        example.createCriteria().andCbsTxnSnEqualTo(cbsSn);
        List<HmTxnStl> txnStlList = hmTxnStlMapper.selectByExample(example);
        return txnStlList.size() > 0 ? txnStlList.get(0) : null;
    }

    // ��ѯ��Ŀ���·ֻ���
    public int qrySubActfundCnt(String actFundNo) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno2EqualTo(actFundNo).andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.countByExample(example);
    }

    // �ظ�����ʱ�����¾��������׺�Ϊ�½��׺ţ��� HM_TXN_STL, HM_TXN_FUND
    // ������ HM_TXN_VCH
    /*@Transactional
    public void updateCbsTxnSn(String msgSn, String txnAmt, String newCbsSn) {

        // update HM_TXN_STL
        HmTxnStlExample txnStlExample = new HmTxnStlExample();
        txnStlExample.createCriteria().andTxnSnEqualTo(msgSn).andTxnAmtEqualTo(new BigDecimal(txnAmt));
        List<HmTxnStl> txnStlList = hmTxnStlMapper.selectByExample(txnStlExample);
        if (txnStlList.size() == 1) {
            HmTxnStl txnStl = txnStlList.get(0);
            txnStl.setCbsTxnSn(newCbsSn);
            hmTxnStlMapper.updateByPrimaryKey(txnStl);
        } else {
            logger.info("δ��ѯ������HmTxnStl���ף����뵥�ţ�" + msgSn + ", �����" + txnAmt);
        }
        // update HM_TXN_FUND
        HmTxnFundExample txnFundExample = new HmTxnFundExample();
        txnFundExample.createCriteria().andTxnSnEqualTo(msgSn).andTxnAmtEqualTo(new BigDecimal(txnAmt));
        List<HmTxnFund> txnFundList = hmTxnFundMapper.selectByExample(txnFundExample);
        if (txnFundList.size() == 1) {
            HmTxnFund txnFund = txnFundList.get(0);
            txnFund.setCbsTxnSn(newCbsSn);
            hmTxnFundMapper.updateByPrimaryKey(txnFund);
        } else {
            logger.info("δ��ѯ������HmTxnFund���ף����뵥�ţ�" + msgSn + ", �����" + txnAmt);
        }
    }*/
}