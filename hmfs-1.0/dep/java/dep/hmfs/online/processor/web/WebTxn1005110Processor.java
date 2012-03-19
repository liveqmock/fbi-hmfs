package dep.hmfs.online.processor.web;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg003;
import dep.hmfs.online.processor.hmb.domain.Msg031;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单位结算户开户通知.
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn1005110Processor extends WebAbstractHmbProductTxnProcessor{

    public String process(String request)  {
        processOpenactRequest(request);
        return "0000|单位结算户开户通知交易成功";
    }

    /**
     * 结算户开户交易
     * @param request
     */
    private void processOpenactRequest(String request) {
        String txnCode = "5110";
        Msg003 msg003 = new Msg003();
        assembleSummaryMsg(txnCode, msg003, 1, true);

        msg003.txnType = "1";//单笔批量？
        msg003.bizType = "1";
        msg003.origTxnCode = "9110";

        String[] fields = request.split("\\|");

        Msg031 msg031 = new Msg031();
        msg031.actionCode = "100";
        int f = 1;
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

}
