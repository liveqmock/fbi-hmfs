package dep.hmfs.online.service.hmb;

import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.repository.hmfs.dao.*;
import common.repository.hmfs.model.*;
import dep.hmfs.online.processor.hmb.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 基本服务：签到签退 对帐.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HmbCmnTxnService extends HmbBaseService {
    private static final Logger logger = LoggerFactory.getLogger(HmbCmnTxnService.class);

    @Resource
    private HmActinfoFundMapper  hmActinfoFundMapper;

    @Resource
    private HmActinfoCbsMapper hmActinfoCbsMapper;

    @Resource
    private HmChkActMapper hmChkActMapper;
    @Resource
    private HmChkLogMapper hmChkLogMapper;
    @Resource
    private TxnFundLogMapper txnFundLogMapper;
    @Resource
    private TxnCbsLogMapper txnCbsLogMapper;

    /**
     * 向国土局签到
     */
    @Transactional
    public void processSignon() {
        HmSct hmSct = getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHK_SUCCESS)) {
            try {
                doHmbSignTxn("7000", "301");
                hmSct.setSysSts(SysCtlSts.SIGNON.getCode());
                hmSct.setSignonDt(new Date());
                hmSctMapper.updateByPrimaryKey(hmSct);
            } catch (Exception e) {
                logger.error("签到失败。", e);
                throw new RuntimeException("签到失败。" + e.getMessage());
            }
        } else {
            throw new RuntimeException("系统初始或与国土局对帐完成后方可签到。");
        }
    }

    private void doHmbSignTxn(String txnCode, String actionCode) throws Exception {
        boolean result = false;
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, 1, true);
        msg001.txnType = "1";//单笔批量？
        msg001.bizType = "3"; //????
        msg001.origTxnCode = "#"; //TODO ????

        Msg096 msg096 = new Msg096();
        msg096.actionCode = actionCode; //301:签到

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg001);
        hmbMsgList.add(msg096);
        byte[] txnBuf = messageFactory.marshal(txnCode, hmbMsgList);
        //发送报文
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(txnBuf);

        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            //
            throw new RuntimeException("接收国土局报文出错，报文为空");
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg002.rtnInfo);
        }
    }

    /**
     * 签退
     */
    @Transactional
    public void processSignout() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                doHmbSignTxn("7001", "302");
                hmSct.setSysSts(SysCtlSts.SIGNOUT.getCode());
                hmSct.setSignoutDt(new Date());
                hmSctMapper.updateByPrimaryKey(hmSct);
            } catch (Exception e) {
                logger.error("签退失败。请重新发起签退。", e);
                throw new RuntimeException("签退失败。请重新发起签退。" + e.getMessage());
            }
        } else {
            throw new RuntimeException("系统签到完成后方可签退。");
        }
    }



    /**
     * 流水对帐
     * @return
     */
    @Transactional
    public boolean processChkActDetl() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())
                && !hmSct.getSysSts().equals(SysCtlSts.HMB_BALCHK_SUCCESS.getCode())) {
            throw new RuntimeException("系统状态错误，主机对帐成功或国土局余额对帐完成后方可进行国土局流水对帐。");
        }

        //depService.doSend

        //db

        //check db

        boolean result = false;
        //余额核对无误后，置系统状态
        if (1 == 1) {
            hmSct = getAppSysStatus();
            SysCtlSts currentSysSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
            if (currentSysSts.equals(SysCtlSts.HMB_BALCHK_SUCCESS)) {
                hmSct.setSysSts(SysCtlSts.HMB_CHK_SUCCESS.getCode());
            } else {
                hmSct.setSysSts(SysCtlSts.HMB_DETLCHK_SUCCESS.getCode());
            }
            //TODO
            hmSct.setHostChkDt(new Date());
            hmSctMapper.updateByPrimaryKey(hmSct);
            result = true;
        }

        return result;
    }

    /**
     * 结算户开户交易
     * @param request
     */
    @Transactional
    public void processOpenactRequest(String request) {
        String txnCode = "5110";
        Msg003 msg003 = new Msg003();
        assembleSummaryMsg(txnCode, msg003, 1, true);

        msg003.txnType = "1";//单笔批量？
        msg003.bizType = "1"; //
        msg003.origTxnCode = "9110";

        String[] fields = request.split("\\|");

        Msg031 msg031 = new Msg031();
        msg031.actionCode = "100";
        int f = 2;
        msg031.cbsActno = fields[f++];
        msg031.cbsActtype = fields[f++];
        msg031.cbsActname  = fields[f++];
        msg031.bankName  = fields[f++];
        msg031.branchId  = fields[f++];
        msg031.depositType  = fields[f++];
        msg031.orgId   = fields[f++];
        msg031.orgType  = fields[f++];
        msg031.orgName   = fields[f];

        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(msg003);
        hmbMsgList.add(msg031);
        byte[] txnBuf = messageFactory.marshal(txnCode, hmbMsgList);
        //发送报文
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(txnBuf);
        List<HmbMsg> msgList = responseMap.get("9999");
        if (msgList == null || msgList.size() == 0) {
            throw new RuntimeException("接收国土局报文出错，报文为空");
        }

        Msg100 msg100 = (Msg100) msgList.get(0);
        if (!msg100.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg100.rtnInfo);
        }
    }


    //===============================service db============================

    /**
     * 核算账户余额
     * @return
     */
    public List<HmActinfoFund> selectFundActinfo(){
        HmActinfoFundExample example = new HmActinfoFundExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActinfoFundMapper.selectByExample(example);
    }

    /**
     * 结算账户余额
     * @return
     */
    public List<HmActinfoCbs> selectCbsActinfo(){
        HmActinfoCbsExample example = new HmActinfoCbsExample();
        example.createCriteria().andActStsNotEqualTo(FundActnoStatus.CANCEL.getCode());
        return hmActinfoCbsMapper.selectByExample(example);
    }
    /**
     * 核算账户流水
     * @return
     */
    public List<TxnFundLog> selectFundTxnDetl(String yyyymmdd){
        TxnFundLogExample example = new TxnFundLogExample();
        example.createCriteria().andTxnDateEqualTo(yyyymmdd);
        return txnFundLogMapper.selectByExample(example);
    }

    /**
     * 处理国土局返回的余额对帐信息
     */
    @Transactional
    public void processChkBalResponse(List<HmbMsg> msgList){
        Msg002 msg002 = (Msg002) msgList.get(0);
        String txnDate = msg002.msgDt.substring(0,8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg098) {
                hmChkAct.setActno(((Msg098) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg098) hmbMsg).getActBal());
            }else{
                hmChkAct.setActno(((Msg094) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg094) hmbMsg).getActBal());
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }

    /**
     * 校验余额对帐数据
     * @return
     */
    @Transactional
    public boolean verifyActBalData(String txnDate){
        //SEND_SYS_ID
        HmChkActExample example = new HmChkActExample();
        example.createCriteria().andTxnDateEqualTo(txnDate);
        //List<HmChkAct> hmChkActMapper

        //TODO  sql
        return false;
    }
    /**
     * 处理国土局返回的流水对帐信息
     */
    @Transactional
    public void processChkDetlResponse(List<HmbMsg> msgList){
        Msg002 msg002 = (Msg002) msgList.get(0);
        String txnDate = msg002.msgDt.substring(0,8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg095) {
                hmChkAct.setActno(((Msg095) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg095) hmbMsg).getTxnAmt1());
            }else{
                hmChkAct.setActno(((Msg092) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg092) hmbMsg).getTxnAmt1());
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }
}
