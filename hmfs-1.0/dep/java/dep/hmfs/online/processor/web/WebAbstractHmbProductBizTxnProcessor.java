package dep.hmfs.online.processor.web;

import common.enums.CbsErrorCode;
import common.enums.SysCtlSts;
import common.repository.hmfs.dao.HmSysCtlMapper;
import common.repository.hmfs.model.HmSysCtl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 状态检查：非签到状态不可进行交易。
 */
public abstract class WebAbstractHmbProductBizTxnProcessor extends WebAbstractHmbProductTxnProcessor {
    protected static final Logger logger = LoggerFactory.getLogger(WebAbstractHmbProductBizTxnProcessor.class);

    @Resource
    protected HmSysCtlMapper hmSysCtlMapper;

    @Override
    @Transactional
    public String run(String request) {
        HmSysCtl hmSysCtl = hmSysCtlMapper.selectByPrimaryKey("1");
        if (!SysCtlSts.SIGNON.getCode().equals(hmSysCtl.getSysSts())) {
            throw new RuntimeException(CbsErrorCode.SYS_NOT_SIGN_ON.getTitle());
        }
        return process(request);
    }

    protected abstract String process(String request);

}
