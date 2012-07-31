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
 * Time: 下午12:44
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
            throw new RuntimeException("未查询到该核算户记录或查询到多个账户！【核算户号】：" + fundActNo);
        } else {
            return actFundList.get(0);
        }
    }

    public HmActFund qryHmActfundByInfoID(String infoID) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andInfoId1EqualTo(infoID);
        List<HmActFund> actFundList = hmActFundMapper.selectByExample(example);
        if (actFundList.size() != 1) {
            throw new RuntimeException("未查询到该核算户记录或查询到多个账户！【信息ID】：" + infoID);
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
                throw new RuntimeException("报文体中含有非核算户开户子报文序号" + hmbMsg.getMsgType() + "！");
            }*/
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int updateActinfoFundsByMsgList(List<HmbMsg> hmbMsgList) throws InvocationTargetException, IllegalAccessException {
        for (HmbMsg hmbMsg : hmbMsgList) {
            // 核算户信息变更
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
                throw new RuntimeException("报文体中含有非核算户更新子报文序号" + hmbMsg.getMsgType() + "！");
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
                throw new RuntimeException("报文体中含有非核算户更新子报文序号" + msginLog.getMsgType() + "！");
            }
        }
        return fundInfoList.size();
    }

    private void cancelActFund(HmActFund hmActFund) {
        // 7-27 拆分户时分户尚有余额，也可销户
        /* if (hmActFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
            throw new RuntimeException("该核算户" + hmActFund.getFundActno1() + "账户中尚有余额，不能销户。");
        }*/
        hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActFundMapper.updateByPrimaryKey(hmActFund);
        if (hmActFund.getFundActno2() != null && !"#".equals(hmActFund.getFundActno2().trim())) {
            HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
            hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
            logger.info("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                    + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea());
            try {
                hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                        .subtract(new BigDecimal(hmActFund.getBuilderArea().trim())).toString());
            } catch (Exception e) {
                logger.error("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                        + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea() + "[数据格式错误]", e);
                throw new RuntimeException("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                        + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea() + "[数据格式错误]");
            }
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        }
    }

    private int updateActinfosByMsg(Msg033 msg033) throws InvocationTargetException, IllegalAccessException {
        HmActFund hmActFund = qryHmActfundByInfoID(msg033.infoId1);
        // 信息类型60——分户核算户
        // 可更新字段
        /*
        20:信息编码
        21:信息名称
        22:信息地址
        23:分户数
        24:建筑面积
        	===
        71:开发建设单位名称
        76:房屋交存类型
        78:交存标准1
        83:交存标准2
        99:是否出售
        100:楼号
        101:门号
        102:室号
        104:证件类型
        105:证件编号
        64:单位联系电话
        82:购房合同号
        88:购房人联系电话
        93:有无电梯
        106:购房款总额
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
            // 面积无变化
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

            // 信息类型30——项目核算户
        } else if ("30".equals(msg033.infoIdType1)) {
            // 无其他可更改字段
        } else {
            throw new RuntimeException("信息类型错误！【信息类型】：" + msg033.infoIdType1);
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

            // 信息类型30——项目核算户
        } else if ("30".equals(msginLog.getInfoIdType1())) {
            // 无其他可更改字段
        } else {
            throw new RuntimeException("信息类型错误！【信息类型】：" + msginLog.getInfoIdType1());
        }
        int cnt = hmActFundMapper.updateByPrimaryKey(hmActFund);
        logger.info("【退款时核算户信息更新】:" + (cnt == 1));

        return cnt;
    }

    public int createActinfoFundByHmbMsg(HmbMsg hmbMsg) throws InvocationTargetException, IllegalAccessException {
        HmActFund actFund = new HmActFund();
        actFund.setPkid(UUID.randomUUID().toString());
        BeanUtils.copyProperties(actFund, hmbMsg);
        // 2012-07-16 新户开户  如果已销户，则从核算户表删除，保存到hm_act_fund_del
        logger.info("【新开户核算户信息】账号:" + actFund.getFundActno1() + "类型:" + actFund.getFundActtype1());
        logger.info("【新开户核算户信息】上级账号:" + actFund.getFundActno2() + "类型:" + actFund.getFundActtype2());
        List<HmActFund> originActFundList = qryExistFundActNo(actFund.getFundActno1());
        if (originActFundList.size() > 0) {
            for (HmActFund record : originActFundList) {
                logger.info("【新开户核算户信息】已有账号:" + record.getFundActno1() + "类型:" + record.getFundActtype1());
                logger.info("【新开户核算户信息】已有账号上级账户:" + record.getFundActno1() + "类型:" + record.getFundActtype1());

                if (FundActnoStatus.CANCEL.getCode().equals(record.getActSts())) {
                    // TODO
                    HmActFundDel hmActFundDel = new HmActFundDel();
                    try {
                        PropertyUtils.copyProperties(hmActFundDel, record);
                    } catch (NoSuchMethodException e) {
                        logger.error("核算户" + record.getFundActno1() + "销户失败。", e);
                        throw new RuntimeException("核算户" + record.getFundActno1() + "销户失败。");
                    }
                    hmActFundDelMapper.insert(hmActFundDel);
                    hmActFundMapper.deleteByPrimaryKey(record.getPkid());
                } else {
                    throw new RuntimeException("核算户" + record.getFundActno1() + "已存在。");
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
        // 2012-07-10 zhangxiaobo 新增分户时 更新项目户建筑面积、分户数+1
        if (actFund.getFundActno2() != null && !"#".equals(actFund.getFundActno2().trim())) {

            HmActFund hmActFund = qryHmActfundByActNo(actFund.getFundActno2());
            // 如果上级项目核算户分户数为0，那么该项目户的分户数和面积先置为0，然后累加
            int cnt = qrySubActfundCnt(actFund.getFundActno2());
            if (cnt == 0) {
                hmActFund.setCellNum("0");
                hmActFund.setBuilderArea("0");
            }
            if (hmActFund != null) {
                hmActFund.setCellNum(String.valueOf(Integer.parseInt(hmActFund.getCellNum().trim()) + 1));
                logger.info("项目核算户建筑面积" + hmActFund.getBuilderArea()
                        + " 新开分户核算户建筑面积" + actFund.getBuilderArea());
                try {
                    hmActFund.setBuilderArea(new BigDecimal(hmActFund.getBuilderArea().trim())
                            .add(new BigDecimal(actFund.getBuilderArea().trim())).toString());
                } catch (Exception e) {
                    logger.error("项目核算户建筑面积" + hmActFund.getBuilderArea()
                            + " 新开分户核算户建筑面积" + actFund.getBuilderArea() + "[数据格式错误]");
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
                    //throw new RuntimeException("该核算户" + msg051.fundActno1 + "账户中尚有余额，不能销户。");
                    //2012-05-31 linyong
                    throw new RuntimeException("该信息ID" + msg051.infoId1 + "账户中尚有余额，不能销户。");
                }
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
                // 2012-07-16 zhangxiaobo 分户销户时项目核算户分户数-1 面积减分户面积
                if (hmActFund.getFundActno2() != null && !"#".equals(hmActFund.getFundActno2().trim())) {
                    HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
                    hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
                    logger.info("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                            + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea());
                    try {
                        hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                                .subtract(new BigDecimal(hmActFund.getBuilderArea().trim())).toString());
                    } catch (Exception e) {
                        logger.error("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                                + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea() + "[数据格式错误]");
                    }
                    hmActFundMapper.updateByPrimaryKey(hmActFund2);
                }*/
                cancelActFund(hmActFund);
            } else {
                throw new RuntimeException("报文体中含有非核算户撤销子报文序号" + hmbMsg.getMsgType() + "！");
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
                    throw new RuntimeException("该核算户" + msg051.fundActno1 + "账户中尚有余额，不能销户。");
                }
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
                if (hmActFund.getFundActno2() != null && !"#".equals(hmActFund.getFundActno2().trim())) {
                    HmActFund hmActFund2 = qryHmActfundByActNo(hmActFund.getFundActno2());
                    hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
                    logger.info("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                            + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea());
                    try {
                        hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                                .subtract(new BigDecimal(hmActFund.getBuilderArea().trim())).toString());
                    } catch (Exception e) {
                        logger.error("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                                + " 销户分户核算户建筑面积" + hmActFund.getBuilderArea() + "[数据格式错误]");
                    }
                    hmActFundMapper.updateByPrimaryKey(hmActFund2);
                }*/
                cancelActFund(hmActFund);
            } else {
                throw new RuntimeException("报文体中含有非核算户撤销子报文序号" + hmbMsg.getMsgType() + "！");
            }
        }
        return hmbMsgList.size();
    }

    /**
     * 项目拆分合并
     */


    //更新核算帐户信息 (不开立新户)
    // 将A项目户下的分户的上级项目户更新为B，同时更新A的分户和面积。
    @Transactional
    public void op145changeFundActinfo(Msg033 msg) throws InvocationTargetException, IllegalAccessException {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(msg.fundActno1);
        List<HmActFund> actFundList = hmActFundMapper.selectByExample(example);
        if (actFundList.size() == 0) {
            throw new RuntimeException("账户信息不存在。");
        }
        //TODO  检查多条？
        HmActFund actFund = actFundList.get(0);

        // 更新上级核算户的分户和余额（A-）
        if (actFund.getFundActno2() != null && !"#".equals(actFund.getFundActno2().trim())) {
            HmActFund hmActFund2 = qryHmActfundByActNo(actFund.getFundActno2());
            hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) - 1));
            logger.info("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                    + " -分户核算户建筑面积" + actFund.getBuilderArea());
            try {
                hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                        .subtract(new BigDecimal(actFund.getBuilderArea().trim())).toString());
            } catch (Exception e) {
                logger.error("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                        + " -分户核算户建筑面积" + actFund.getBuilderArea() + "[数据格式错误]", e);
                throw new RuntimeException("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                        + " -分户核算户建筑面积" + actFund.getBuilderArea() + "[数据格式错误]");
            }
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        }
        // B+
        if (msg.fundActno2 != null && !"#".equals(msg.fundActno2.trim())) {
            HmActFund hmActFund2 = qryHmActfundByActNo(msg.fundActno2);
            hmActFund2.setCellNum(String.valueOf(Integer.parseInt(hmActFund2.getCellNum().trim()) + 1));
            logger.info("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                    + " +分户核算户建筑面积" + actFund.getBuilderArea());
            try {
                hmActFund2.setBuilderArea(new BigDecimal(hmActFund2.getBuilderArea().trim())
                        .add(new BigDecimal(actFund.getBuilderArea().trim())).toString());
            } catch (Exception e) {
                logger.error("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                        + " +分户核算户建筑面积" + actFund.getBuilderArea() + "[数据格式错误]", e);
                throw new RuntimeException("项目核算户建筑面积" + hmActFund2.getBuilderArea()
                        + " +分户核算户建筑面积" + actFund.getBuilderArea() + "[数据格式错误]");
            }
            hmActFundMapper.updateByPrimaryKey(hmActFund2);
        }
        int recversion = actFund.getRecversion() + 1;
        // BeanUtils.copyProperties(actFund, msg);
        actFund.setFundActno2(msg.fundActno2);
        actFund.setRemark("TXN6210 项目拆分合并" + new Date().toString());
        actFund.setRecversion(recversion);
        hmActFundMapper.updateByPrimaryKey(actFund);
    }


    //125:取款销户 2012-07-27
    public void op125cancelActinfoFunds(String msgSn, Msg051 msg051) throws ParseException {
        //取款帐务处理
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno1, msg051.actBal,
                DCFlagCode.WITHDRAW.getCode(), "125", "125");
        if (!"#".equals(msg051.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno2, msg051.actBal,
                    DCFlagCode.WITHDRAW.getCode(), "125", "125");
        }
        //销户
        HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
        /*hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActFundMapper.updateByPrimaryKey(hmActFund);*/
        cancelActFund(hmActFund);
    }

    //115:存款
    @Transactional
    public void op115deposite(String msgSn, Msg035 msg035) throws ParseException {

        // 批量核算户账户信息更新
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg035.fundActno1, msg035.txnAmt1, DCFlagCode.DEPOSIT.getCode(), "115", "115");
        if (!"#".equals(msg035.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg035.fundActno2, msg035.txnAmt1, DCFlagCode.DEPOSIT.getCode(), "115", "115");
        }
    }

    // 按主机流水号查询结算户交易明细
    public HmTxnStl qryTxnStlByCbsSn(String cbsSn) {
        HmTxnStlExample example = new HmTxnStlExample();
        example.createCriteria().andCbsTxnSnEqualTo(cbsSn);
        List<HmTxnStl> txnStlList = hmTxnStlMapper.selectByExample(example);
        return txnStlList.size() > 0 ? txnStlList.get(0) : null;
    }

    // 查询项目户下分户数
    public int qrySubActfundCnt(String actFundNo) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno2EqualTo(actFundNo).andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActFundMapper.countByExample(example);
    }

    // 重复交款时，更新旧主机交易号为新交易号，表 HM_TXN_STL, HM_TXN_FUND
    // 不更新 HM_TXN_VCH
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
            logger.info("未查询到已有HmTxnStl交易，申请单号：" + msgSn + ", 交款金额：" + txnAmt);
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
            logger.info("未查询到已有HmTxnFund交易，申请单号：" + msgSn + ", 交款金额：" + txnAmt);
        }
    }*/
}