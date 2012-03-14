package dep.test.hmbserver;

import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.SummaryMsg;
import dep.util.PropertyManager;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTxnProcessor implements TxnProcessor{
    protected  static String SEND_SYS_ID =  PropertyManager.getProperty("SEND_SYS_ID");
    protected  static String ORIG_SYS_ID =  PropertyManager.getProperty("ORIG_SYS_ID");

    protected static HmbMessageFactory messageFactory = new HmbMessageFactory();

    @Override
    abstract public byte[] process(byte[] msgin);


    /**
     * 处理汇总包
     * @param msg
     * @param submsgNum
     */
    protected void assembleSummaryMsg(SummaryMsg msg, int submsgNum) {
        msg.msgSn = "1111";
        msg.submsgNum = submsgNum;
        msg.sendSysId = SEND_SYS_ID;
        msg.origSysId = ORIG_SYS_ID;
        msg.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg.msgEndDate = "#";
    }

    protected Map<String, List<HmbMsg>> getResponseMap(byte[] msgin){
        return messageFactory.unmarshal(msgin);
    }

}
