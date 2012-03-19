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
 * Time: 下午12:44
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
            throw new RuntimeException("未查询到该核算户记录或查询到多个账户！【核算户号】：" + fundActNo);
        } else {
            return actinfoFundList.get(0);
        }
    }

    public HmActinfoFund qryHmActinfoFundByInfoID(String infoID) {
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andInfoId1EqualTo(infoID);
        List<HmActinfoFund> actinfoFundList = hmActinfoFundMapper.selectByExample(example);
        if (actinfoFundList.size() != 1) {
            throw new RuntimeException("未查询到该核算户记录或查询到多个账户！【信息ID】：" + infoID);
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
                HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msg051.fundActno1);
                hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("报文体中含有非核算户更新子报文序号" + hmbMsg.getMsgType() + "！");
            }
        }
        return hmbMsgList.size();
    }

    @Transactional
    public int updateActinfoFundsByMsginList(List<HisMsginLog> fundInfoList) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (HisMsginLog msginLog : fundInfoList) {
            if ("01033".equals(msginLog.getMsgType())) {
                updateActinfosByMsginLog(msginLog);
            } else if ("01051".equals(msginLog.getMsgType())) {
                HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msginLog.getFundActno1());
                hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
            } else {
                throw new RuntimeException("报文体中含有非核算户更新子报文序号" + msginLog.getMsgType() + "！");
            }
        }
        return fundInfoList.size();
    }

    private int updateActinfosByMsg(Msg033 msg033) throws InvocationTargetException, IllegalAccessException {
        HmActinfoFund hmActinfoFund = qryHmActinfoFundByInfoID(msg033.infoId1);
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
        hmActinfoFund.setInfoCode(msg033.infoCode);
        hmActinfoFund.setInfoName(msg033.infoName);
        hmActinfoFund.setInfoAddr(msg033.infoAddr);
        hmActinfoFund.setCellNum(msg033.cellNum);
        hmActinfoFund.setBuilderArea(msg033.builderArea);
        if ("60".equals(msg033.infoIdType1)) {
            hmActinfoFund.setDevOrgName(msg033.devOrgName);
            hmActinfoFund.setHouseDepType(msg033.houseDepType);
            hmActinfoFund.setDepStandard1(msg033.depStandard1);
            hmActinfoFund.setDepStandard2(msg033.depStandard2);
            hmActinfoFund.setSellFlag(msg033.sellFlag);
            hmActinfoFund.setBuildingNo(msg033.buildingNo);
            hmActinfoFund.setUnitNo(msg033.unitNo);
            hmActinfoFund.setRoomNo(msg033.roomNo);
            hmActinfoFund.setCertType(msg033.certType);
            hmActinfoFund.setCertId(msg033.certId);
            hmActinfoFund.setOrgPhone(msg033.orgPhone);
            hmActinfoFund.setHouseContNo(msg033.houseContNo);
            hmActinfoFund.setHouseCustPhone(msg033.houseCustPhone);
            hmActinfoFund.setElevatorType(msg033.elevatorType);
            hmActinfoFund.setHouseTotalAmt(msg033.houseTotalAmt);
            // 信息类型30——项目核算户
        } else if ("30".equals(msg033.infoIdType1)) {
            // 无其他可更改字段
        } else {
            throw new RuntimeException("信息类型错误！【信息类型】：" + msg033.infoIdType1);
        }
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
                    throw new RuntimeException("该核算户" + msg051.fundActno1 + "账户中尚有余额，不能销户。");
                }
                hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
                hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
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
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andFundActno1EqualTo(msg.fundActno1);
        List<HmActinfoFund> actinfoFundList = hmActinfoFundMapper.selectByExample(example);
        if (actinfoFundList.size() == 0) {
            throw new RuntimeException("账户信息不存在。");
        }
        //TODO  检查多条？
        HmActinfoFund actinfoFund = actinfoFundList.get(0);
        int recversion = actinfoFund.getRecversion() + 1;
        BeanUtils.copyProperties(actinfoFund, msg);
        actinfoFund.setRemark("TXN6210 项目拆分合并" + new Date().toString());
        actinfoFund.setRecversion(recversion);
        hmActinfoFundMapper.updateByPrimaryKey(actinfoFund);
    }


    //125:取款销户
    public void op125cancelActinfoFunds(String msgSn, Msg051 msg051) throws ParseException {
        //销户
        HmActinfoFund hmActinfoFund = qryHmActinfoFundByFundActNo(msg051.fundActno1);
        hmActinfoFund.setActSts(FundActnoStatus.CANCEL.getCode());
        hmActinfoFundMapper.updateByPrimaryKey(hmActinfoFund);
        //取款帐务处理
        bookkeepingService.fundActBookkeeping(msgSn, msg051.fundActno1, msg051.actBal, "D", "125", "125");
        if (!"#".equals(msg051.fundActno2.trim())) {
            bookkeepingService.fundActBookkeeping(msgSn, msg051.fundActno2, msg051.actBal, "D", "125", "125");
        }
    }


    //115:存款
    @Transactional
    public void op115deposite(String msgSn, Msg035 msg035) throws ParseException {
        // 会计账号记账
        bookkeepingService.cbsActBookkeeping("HMB" + SystemService.formatTodayByPattern("yyyyMMddHHmmss"),
                msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115");

        // 批量核算户账户信息更新
        bookkeepingService.fundActBookkeeping(msgSn, msg035.fundActno1, msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115", "115");
        if (!"#".equals(msg035.fundActno2.trim())) {
            bookkeepingService.fundActBookkeeping(msgSn, msg035.fundActno2, msg035.txnAmt1, DCFlagCode.TXN_IN.getCode(), "115", "115");
        }
    }
}