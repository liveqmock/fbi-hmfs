package dep.hmfs.online.processor.web;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmActStl;
import common.repository.hmfs.model.HmChkAct;
import dep.hmfs.online.processor.hmb.domain.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 余额对帐.
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1007003Processor extends WebAbstractHmbProductTxnProcessor {

    @Override
    public String process(String request) {
        String txnResult = "";
        String txnCode = "7003";
        String txnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //清理本日旧数据
        hmbSysTxnService.deleteOldActChkDataByTxnDate(txnDate, "99");
        hmbSysTxnService.deleteOldActChkDataByTxnDate(txnDate, "00");
        hmbSysTxnService.deleteOldTxnChkDataByTxnDate(txnDate, "99");
        hmbSysTxnService.deleteOldTxnChkDataByTxnDate(txnDate, "00");

        Map<String, List<HmbMsg>> responseMap = null;
        try {
            //发送报文
            responseMap = sendDataUntilRcv(getFirstChkReqBuf(txnCode, txnDate));
        } catch (RuntimeException re) {
            if (!StringUtils.isEmpty(re.getMessage()) && re.getMessage().startsWith("700")) {
                txnResult = "7001|网络连接超时";
                return txnResult;
            }
        }



        //处理返回报文
        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            Msg100 msg100 = (Msg100) responseMap.get("9999").get(0);
            throw new RuntimeException(msg100.rtnInfo);
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg002.rtnInfo);
        } else {
            //保存国土局到本地数据库
            hmbSysTxnService.processChkBalResponse(msgList, txnDate);
            //数据核对处理
            boolean verify = hmbSysTxnService.verifyHmbActBalData(txnDate, "511");

            if (verify) {
                txnResult = "0000|余额对帐成功";
            } else {
                logger.info("开始第二次校验处理");

                //查找对帐状态为不符且发送方为‘99’（本地数据）的
                List<HmChkAct> chkActList = hmbSysTxnService.selectChkFailedFundActList(txnDate);

                if (chkActList.isEmpty()) { //对于存在对帐不符记录且是中心的数据有但本地数据没有的情况，直接返回失败
                    txnResult = "9999|余额对帐失败";
                } else {
                    //发送二次核对报文
                    responseMap = sendDataUntilRcv(getSecondChkReqBuf(chkActList, txnCode, txnDate));
                    //处理返回报文
                    msgList = responseMap.get(txnCode);
                    if (msgList == null || msgList.size() == 0) {
                        Msg100 msg100 = (Msg100) responseMap.get("9999").get(0);
                        throw new RuntimeException(msg100.rtnInfo);
                    }
                    msg002 = (Msg002) msgList.get(0);
                    if (!msg002.rtnInfoCode.equals("00")) {
                        throw new RuntimeException("国土局返回错误信息：" + msg002.rtnInfo);
                    } else {
                        //数据核对处理
                        //保存国土局到本地数据库
                        hmbSysTxnService.processChkBalResponse(msgList, txnDate);
                        hmbSysTxnService.verifyHmbActBalData(txnDate, "570");
                        txnResult = "9999|余额对帐失败";
                    }
                }
            }
        }
        //置系统控制表状态 : 余额对帐完成  TODO:处理时机需探讨
        updateSysCtlStatus(SysCtlSts.HMB_CHK_OVER);
        return txnResult;
    }

    //第一轮余额对帐报文：核对结算户和项目核算户
    private byte[] getFirstChkReqBuf(String txnCode, String txnDate) {
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        List<HmActFund> actFundList = hmbSysTxnService.selectProjectFundActinfo();
        List<HmActStl> actStlList = hmbSysTxnService.selectStlActinfo();

        //汇总报文处理
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, actFundList.size() + actStlList.size(), true);
        msg001.txnType = "1";//单笔批量？
        msg001.bizType = "3"; //?
        msg001.origTxnCode = "5110"; //TODO ????
        hmbMsgList.add(msg001);

        //子报文处理  098 094
        for (HmActFund hmActFund : actFundList) {
            Msg098 msg098 = new Msg098();
            msg098.actionCode = "304"; //304:日终对账
            msg098.infoId1 = hmActFund.getInfoId1();
            msg098.infoIdType1 = hmActFund.getInfoIdType1();
            msg098.cellNum = hmActFund.getCellNum();
            msg098.builderArea = hmActFund.getBuilderArea();
            msg098.fundActno1 = hmActFund.getFundActno1();
            msg098.fundActtype1 = hmActFund.getFundActtype1();
            msg098.actBal = hmActFund.getActBal();
            hmbMsgList.add(msg098);

            //保存发起对帐的数据到本地数据库
            /*
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg098.fundActno1);
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(msg098.actBal);
            hmChkActMapper.insert(hmChkAct);
            */
        }
        //保存发起对帐的数据到本地数据库
        hmbSysTxnService.insertChkFundRecord(actFundList, txnDate);

        for (HmActStl hmActStl : actStlList) {
            Msg094 msg094 = new Msg094();
            msg094.actionCode = "304"; //304:日终对账
            msg094.orgId = hmActStl.getOrgId();
            msg094.orgType = hmActStl.getOrgType();
            msg094.settleActno1 = hmActStl.getSettleActno1();
            msg094.settleActtype1 = hmActStl.getSettleActtype1();
            msg094.actBal = hmActStl.getActBal();
            hmbMsgList.add(msg094);

            //保存发起对帐的数据到本地数据库
            /*
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg094.settleActno1);
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(msg094.actBal);
            hmChkActMapper.insert(hmChkAct);
            */
        }
        //保存发起对帐的数据到本地数据库
        hmbSysTxnService.insertChkStlRecord(actStlList, txnDate);

        return messageFactory.marshal(txnCode, hmbMsgList);
    }

    //第二轮余额对帐报文：第一轮核对不上的项目核算户中的分户核算户
    private byte[] getSecondChkReqBuf(List<HmChkAct> chkActList, String txnCode, String txnDate) {
        //List<HmChkAct> chkActList = hmbSysTxnService.selectChkFailedFundActList(txnDate);
        List<String> chkFailedActnoList = new ArrayList<String>();
        for (HmChkAct hmChkAct : chkActList) {
            //logger.info("==核对失败帐号：" + hmChkAct.getActno());
            chkFailedActnoList.add(hmChkAct.getActno());
        }
        List<HmActFund> actFundList = hmbSysTxnService.selectIndividFundActinfo(chkFailedActnoList);
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        //汇总报文处理
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, actFundList.size(), true);
        msg001.txnType = "1";//单笔批量？
        msg001.bizType = "3"; //?
        msg001.origTxnCode = "5110"; //TODO ????
        hmbMsgList.add(msg001);

        //子报文处理  098 094
        for (HmActFund hmActFund : actFundList) {
            Msg098 msg098 = new Msg098();
            msg098.actionCode = "304"; //304:日终对账
            msg098.infoId1 = hmActFund.getInfoId1();
            msg098.infoIdType1 = hmActFund.getInfoIdType1();
            msg098.cellNum = hmActFund.getCellNum();
            msg098.builderArea = hmActFund.getBuilderArea();
            msg098.fundActno1 = hmActFund.getFundActno1();
            msg098.fundActtype1 = hmActFund.getFundActtype1();
            msg098.actBal = hmActFund.getActBal();
            hmbMsgList.add(msg098);

            //保存发起对帐的数据到本地数据库
            /*
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setPkid(UUID.randomUUID().toString());
            hmChkAct.setActno(msg098.fundActno1);
            hmChkAct.setTxnDate(txnDate);
            //hmChkAct.setSendSysId(SEND_SYS_ID);
            hmChkAct.setSendSysId("99");
            hmChkAct.setActbal(msg098.actBal);
            hmChkActMapper.insert(hmChkAct);
            */
        }
        //保存发起对帐的数据到本地数据库
        hmbSysTxnService.insertChkFundRecord(actFundList, txnDate);

        return messageFactory.marshal(txnCode, hmbMsgList);
    }

}
