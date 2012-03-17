package dep.hmfs.online.processor.hmb;

import common.service.SystemService;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.common.HmbTxnsnGenerator;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg004;
import dep.hmfs.online.processor.hmb.domain.Msg100;
import dep.hmfs.online.service.HmbActinfoService;
import dep.hmfs.online.service.hmb.HmbBaseService;
import dep.util.PropertyManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class HmbAbstractTxnProcessor {

    @Resource
    protected HmbMessageFactory mf;
    @Resource
    HmbTxnsnGenerator txnsnGenerator;
    @Resource
    protected HmbBaseService hmbBaseService;
    @Autowired
    protected HmbActinfoService hmbActinfoService;


    public abstract byte[] process(String txnCode, String msgSn, List<HmbMsg> hmbMsgList);

    public Msg100 createRtnMsg100(String msgSn) {
        if (StringUtils.isEmpty(msgSn)) {
            throw new RuntimeException("响应报文编号不能为空！");
        }
        Msg100 msg100 = new Msg100();
        msg100.msgSn = msgSn;
        msg100.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg100.origSysId = "00";
        msg100.rtnInfoCode = "00";
        msg100.rtnInfo = "报文处理成功";
        return msg100;
    }

    public Msg004 createRtnMsg004(String msgSn) {
        if (StringUtils.isEmpty(msgSn)) {
            throw new RuntimeException("响应报文编号不能为空！");
        }
        Msg004 msg004 = new Msg004();
        msg004.msgSn = msgSn;
        msg004.sendSysId = PropertyManager.getProperty("SEND_SYS_ID");
        msg004.origSysId = "00";
        msg004.rtnInfoCode = "00";
        msg004.rtnInfo = "报文处理成功";
        msg004.msgDt = SystemService.formatTodayByPattern("yyyyMMddHHmmss");
        msg004.msgEndDate = "#";
        msg004.origMsgSn = msgSn;
        return msg004;
    }
}
