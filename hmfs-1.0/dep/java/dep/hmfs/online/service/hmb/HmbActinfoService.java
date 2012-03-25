package dep.hmfs.online.service.hmb;

import common.enums.DCFlagCode;
import common.enums.FundActnoStatus;
import common.repository.hmfs.dao.HmActFundMapper;
import common.repository.hmfs.dao.HmActStlMapper;
import common.repository.hmfs.dao.HmTxnStlMapper;
import common.repository.hmfs.model.*;
import common.service.SystemService;
import dep.hmfs.online.processor.hmb.domain.*;
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
 * Time: 下午12:44
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbActinfoService {

    @Autowired
    private HmActFundMapper hmActFundMapper;
    @Autowired
    private ActBookkeepingService actBookkeepingService;

    @Autowired
    private HmActStlMapper hmActStlMapper;
    @Autowired
    private HmTxnStlMapper hmTxnStlMapper;

    public List<HmTxnStl> qryTxnstlsByDate(String txnDate) {
        HmTxnStlExample example = new HmTxnStlExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        example.setOrderByClause("TXN_SN");
        List<HmTxnStl> hmTxnStlList = hmTxnStlMapper.selectByExample(example);
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

    public boolean isExistFundActNo(String fundActno) {
        HmActFundExample example = new HmActFundExample();
        example.createCriteria().andFundActno1EqualTo(fundActno);
        return hmActFundMapper.countByExample(example) > 0;
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
                throw new RuntimeException("报文体中含有非核算户开户子报文序号" + hmbMsg.getMsgType() + "！");
            }
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
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
            } else {
                throw new RuntimeException("报文体中含有非核算户更新子报文序号" + hmbMsg.getMsgType() + "！");
            }
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
                throw new RuntimeException("报文体中含有非核算户更新子报文序号" + msginLog.getMsgType() + "！");
            }
        }
        return fundInfoList.size();
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


    private int updateActinfosByMsginLog(HmMsgIn msginLog) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        HmActFund hmActFund = qryHmActfundByActNo(msginLog.getFundActno1());
        PropertyUtils.copyProperties(hmActFund, msginLog);
        return hmActFundMapper.updateByPrimaryKey(hmActFund);
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
                HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
                if (hmActFund.getActBal().compareTo(new BigDecimal(0)) > 0) {
                    throw new RuntimeException("该核算户" + msg051.fundActno1 + "账户中尚有余额，不能销户。");
                }
                hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActFundMapper.updateByPrimaryKey(hmActFund);
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
        int recversion = actFund.getRecversion() + 1;
        BeanUtils.copyProperties(actFund, msg);
        actFund.setRemark("TXN6210 项目拆分合并" + new Date().toString());
        actFund.setRecversion(recversion);
        hmActFundMapper.updateByPrimaryKey(actFund);
    }


    //125:取款销户
    public void op125cancelActinfoFunds(String msgSn, Msg051 msg051) throws ParseException {
        //销户
        HmActFund hmActFund = qryHmActfundByActNo(msg051.fundActno1);
        hmActFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActFundMapper.updateByPrimaryKey(hmActFund);
        //取款帐务处理
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno1, msg051.actBal, "D", "125", "125");
        if (!"#".equals(msg051.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg051.fundActno2, msg051.actBal, "D", "125", "125");
        }
    }


    //115:存款
    @Transactional
    public void op115deposite(String msgSn, Msg035 msg035) throws ParseException {

        // 批量核算户账户信息更新
        actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg035.fundActno1, msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115", "115");
        if (!"#".equals(msg035.fundActno2.trim())) {
            actBookkeepingService.fundActBookkeeping(null, msgSn, 1, msg035.fundActno2, msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115", "115");
        }
    }
}