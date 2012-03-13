package dep.hmfs.service.hmb;

import common.enums.SysCtlSts;
import common.repository.hmfs.dao.HmSctMapper;
import common.repository.hmfs.model.HmSct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 基本服务：签到签退 对帐.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AppBaseService.class);

    @Resource
    private HmSctMapper hmSctMapper;

    public HmSct getAppSysStatus() {
        return hmSctMapper.selectByPrimaryKey("1");
    }

    /**
     * 向国土局签到
     */
    @Transactional
    public void processSignon() {
        HmSct hmSct = getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHK_SUCCESS)) {
            try {
                //appMngService.processSignon();
                hmSct.setSysSts(SysCtlSts.SIGNON.getCode());
                hmSct.setSignonDt(new Date());
                hmSctMapper.updateByPrimaryKey(hmSct);
            } catch (Exception e) {
                logger.error("签到失败。请重新发起签到。", e);
                throw new RuntimeException("签到失败。请重新发起签到。" + e.getMessage());
            }
        } else {
            throw new RuntimeException("系统初始或与国土局对帐完成后方可签到。");
        }
    }

    @Transactional
    public void processSignout() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                //appMngService.processSignout();
                hmSct.setSysSts(SysCtlSts.SIGNOUT.getCode());
                hmSct.setSignoutDt(new Date());
                hmSctMapper.updateByPrimaryKey(hmSct);
                //MessageUtil.addInfo("向国土局系统签退成功......");
            } catch (Exception e) {
                logger.error("签退失败。请重新发起签退。" ,e);
                throw new RuntimeException("签退失败。请重新发起签退。" + e.getMessage());
            }
        } else {
            throw new RuntimeException("系统签到完成后方可签退。");
        }
    }

    @Transactional
    public boolean processChkActBal() {
        //TODO
        HmSct hmSct = getAppSysStatus();
        if (!hmSct.getSysSts().equals(SysCtlSts.HOST_CHK_SUCCESS.getCode())) {
            throw new RuntimeException("系统状态错误，主机对帐成功后方可进行国土局对帐操作。");
        }

        //depService.doSend

        //db

        //check db

        boolean result = false;
        //余额核对无误后，置系统状态
        if (1 == 1) {
            hmSct = getAppSysStatus();
            SysCtlSts currentSysSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
            if (currentSysSts.equals(SysCtlSts.HMB_DETLCHK_SUCCESS)) {
                hmSct.setSysSts(SysCtlSts.HMB_CHK_SUCCESS.getCode());
            } else {
                hmSct.setSysSts(SysCtlSts.HMB_BALCHK_SUCCESS.getCode());
            }
            //TODO
            hmSct.setHostChkDt(new Date());
            hmSctMapper.updateByPrimaryKey(hmSct);
            result = true;
        }

        return result;
    }

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


}
