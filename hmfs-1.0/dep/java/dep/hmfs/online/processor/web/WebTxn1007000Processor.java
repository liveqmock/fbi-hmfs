package dep.hmfs.online.processor.web;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSct;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg096;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 向房产局签到.
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1007000Processor extends WebAbstractHmbProductTxnProcessor{

    public String process(String request)  {
        processSignon();
        return "0000|签到交易成功";
    }

    /**
     * 向国土局签到
     */
    public void processSignon() {
        HmSct hmSct = hmbSysTxnService.getAppSysStatus();
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
            throw new RuntimeException("接收国土局报文出错，报文为空");
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg002.rtnInfo);
        }
    }
}
