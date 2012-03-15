package dep.hmfs.online.processor.web;

import common.repository.hmfs.model.HmActinfoCbs;
import common.repository.hmfs.model.HmActinfoFund;
import dep.hmfs.online.processor.hmb.domain.*;
import dep.hmfs.online.service.hmb.HmbCmnTxnService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 余额对帐.
 * User: zhanrui
 * Date: 12-3-15
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WebTxn7003Processor extends WebAbstractTxnProcessor{
    @Resource
    private  HmbCmnTxnService hmbCmnTxnService;

    @Override
    public String process(String request)  {
        //TODO
        //HmSct hmSct = hmbCmnTxnService.getAppSysStatus();
        //if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())) {
        //    throw new RuntimeException("系统状态错误，主机对帐成功后方可进行国土局对帐操作。");
        //}

        String txnCode = "7003";
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

        //子报文处理  098 094
        List<HmActinfoFund> actinfoFundList = hmbCmnTxnService.selectFundActinfo();
        for (HmActinfoFund hmActinfoFund : actinfoFundList) {
            Msg098 msg098 = new Msg098();
            msg098.actionCode = "304"; //304:日终对账
            msg098.infoId1 = hmActinfoFund.getInfoId1();
            msg098.infoIdType1 = hmActinfoFund.getInfoIdType1();
            msg098.cellNum = Integer.parseInt(hmActinfoFund.getCellNum());
            msg098.builderArea = new BigDecimal(hmActinfoFund.getBuilderArea());
            msg098.fundActno1 = hmActinfoFund.getFundActno1();
            msg098.fundActtype1 = hmActinfoFund.getFundActtype1();
            hmbMsgList.add(msg098);
        }
        List<HmActinfoCbs> actinfoCbsList = hmbCmnTxnService.selectCbsActinfo();
        for (HmActinfoCbs hmActinfoCbs : actinfoCbsList) {
            Msg094 msg094 = new Msg094();
            msg094.actionCode = "304"; //304:日终对账
            msg094.orgId = hmActinfoCbs.getOrgId();
            msg094.orgType = hmActinfoCbs.getOrgType();
            msg094.settleActno1 = hmActinfoCbs.getSettleActno1();
            msg094.settleActtype1 = hmActinfoCbs.getSettleActtype1();
            hmbMsgList.add(msg094);
        }
        return  messageFactory.marshal(txnCode, hmbMsgList);
    }
}
