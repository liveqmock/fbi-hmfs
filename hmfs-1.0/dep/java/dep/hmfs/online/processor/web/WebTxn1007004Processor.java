package dep.hmfs.online.processor.web;

import common.repository.hmfs.model.HmChkAct;
import dep.hmfs.online.processor.hmb.domain.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易流水对帐.  暂不用
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Deprecated
public class WebTxn1007004Processor extends WebAbstractHmbProductTxnProcessor {

    @Override
    public String process(String request) {
        String txnCode = "7004";

        String txnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //清理本日旧数据
        hmbSysTxnService.deleteOldTxnChkDataByTxnDate(txnDate, "99");
        hmbSysTxnService.deleteOldTxnChkDataByTxnDate(txnDate, "00");

        //发送报文
        Map<String, List<HmbMsg>> responseMap = sendDataUntilRcv(getRequestBuf(txnCode));

        //处理返回报文
        List<HmbMsg> msgList = responseMap.get(txnCode);
        if (msgList == null || msgList.size() == 0) {
            throw new RuntimeException("接收国土局报文出错，报文为空");
        }

        Msg002 msg002 = (Msg002) msgList.get(0);
        if (!msg002.rtnInfoCode.equals("00")) {
            throw new RuntimeException("国土局返回错误信息：" + msg002.rtnInfo);
        } else {
            //保存到本地数据库
            processChkDetlResponse(msgList);
            //TODO 核对
        }

        return "0000|流水对帐交易成功";
    }

    private byte[] getRequestBuf(String txnCode) {
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        //汇总报文处理
        Msg001 msg001 = new Msg001();
        assembleSummaryMsg(txnCode, msg001, 1, false);
        msg001.txnType = "1";//单笔批量？
        msg001.bizType = "#"; //?
        msg001.origTxnCode = "#"; //TODO ????
        hmbMsgList.add(msg001);

        //TODO 处理本地数据

        //子报文处理  095-核算户 092-结算户
        return messageFactory.marshal(txnCode, hmbMsgList);
    }

    /**
     * 处理国土局返回的流水对帐信息
     */
    private void processChkDetlResponse(List<HmbMsg> msgList) {
        Msg002 msg002 = (Msg002) msgList.get(0);
        String txnDate = msg002.msgDt.substring(0, 8);
        for (HmbMsg hmbMsg : msgList.subList(1, msgList.size())) {
            HmChkAct hmChkAct = new HmChkAct();
            hmChkAct.setTxnDate(txnDate);
            hmChkAct.setSendSysId("00");
            if (hmbMsg instanceof Msg095) {
                hmChkAct.setActno(((Msg095) hmbMsg).fundActno1);
                hmChkAct.setActbal(((Msg095) hmbMsg).getTxnAmt1());
            } else {
                hmChkAct.setActno(((Msg092) hmbMsg).settleActno1);
                hmChkAct.setActbal(((Msg092) hmbMsg).getTxnAmt1());
            }
            hmChkActMapper.insert(hmChkAct);
        }
    }

}
