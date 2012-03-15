package dep.hmfs.online.processor.web;

import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.service.hmb.HmbCmnTxnService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 交易流水对帐.
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn7004Processor extends WebAbstractTxnProcessor{
    @Resource
    private  HmbCmnTxnService hmbCmnTxnService;

    @Override
    public String process(String request)  {
        //TODO
        //HmSct hmSct = hmbCmnTxnService.getAppSysStatus();
        //if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())) {
        //    throw new RuntimeException("系统状态错误，主机对帐成功后方可进行国土局对帐操作。");
        //}

        String txnCode = "7004";
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
        }else{
            //保存到本地数据库
            hmbCmnTxnService.processChkBalResponse(msgList);
        }

        return null;
    }

    private byte[] getRequestBuf(String txnCode){
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        //汇总报文处理
        Msg001 msg001 = new Msg001();
        hmbCmnTxnService.assembleSummaryMsg(txnCode, msg001, 1, false);
        msg001.txnType = "1";//单笔批量？
        msg001.bizType = "#"; //?
        msg001.origTxnCode = "#"; //TODO ????
        hmbMsgList.add(msg001);

        //子报文处理  095-核算户 092-结算户
        return  messageFactory.marshal(txnCode, hmbMsgList);
    }
}
