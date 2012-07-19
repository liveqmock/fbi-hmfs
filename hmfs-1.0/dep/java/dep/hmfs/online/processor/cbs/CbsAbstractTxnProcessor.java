package dep.hmfs.online.processor.cbs;

import common.enums.CbsErrorCode;
import common.enums.SysCtlSts;
import common.repository.hmfs.dao.HmSysCtlMapper;
import common.repository.hmfs.model.HmSysCtl;
import dep.hmfs.online.processor.cbs.domain.base.TOA;
import dep.hmfs.online.service.hmb.HmbBaseService;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-8
 * Time: 下午7:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class CbsAbstractTxnProcessor {

    @Resource
    protected HmbBaseService hmbBaseService;
    @Resource
    protected HmSysCtlMapper hmSysCtlMapper;

    public abstract TOA process(String txnSerialNo, byte[] bytes) throws Exception;

    public TOA run(String txnCode, String serialNo, byte[] datagramBytes) throws Exception {
        HmSysCtl hmSysCtl = hmSysCtlMapper.selectByPrimaryKey("1");
        String sysSts = hmSysCtl.getSysSts();

        /*
            对于主机发起的交易（或WEB层发起的交易），此处不检查签到签退交易时系统的状态。
            签到签退交易（包括自动发起的交易）由房产交易中心的系统控制。
         */
        if ("5001".equals(txnCode)) { //对帐交易
            if (!SysCtlSts.SIGNOUT.getCode().equals(sysSts) && !SysCtlSts.HMB_CHK_OVER.getCode().equals(sysSts)) {
                throw new RuntimeException(CbsErrorCode.SYS_NOT_SIGN_OUT.getCode());
            }
        }else{ //其它交易（不包括WEB层发起的签到签退）
            if (!SysCtlSts.SIGNON.getCode().equals(sysSts)) {
                throw new RuntimeException(CbsErrorCode.SYS_NOT_SIGN_ON.getCode());
            }
        }
        return process(serialNo, datagramBytes);
    }
}
