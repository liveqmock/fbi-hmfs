package hmfs.service;

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
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:55
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppMngService {
    private static final Logger logger = LoggerFactory.getLogger(AppMngService.class);

    @Resource
    private HmSctMapper hmSctMapper;

    public HmSct getAppSysStatus(){
         return hmSctMapper.selectByPrimaryKey("1");
    }

    /**
     *   向国土局签到
     */
    @Transactional
    public void processSignon(){
        //TODO
        HmSct hmSct = getAppSysStatus();
        hmSct.setSysSts(SysCtlSts.SIGNON.getCode());
        hmSct.setSignonDt(new Date());
        hmSctMapper.updateByPrimaryKey(hmSct);

    }

    public void processSignout(){
        //TODO
        HmSct hmSct = getAppSysStatus();
        hmSct.setSysSts(SysCtlSts.SIGNOUT.getCode());
        hmSct.setSignoutDt(new Date());
        hmSctMapper.updateByPrimaryKey(hmSct);

    }

    public boolean processChkActBal(){
        //TODO
        HmSct hmSct = getAppSysStatus();
        if (!hmSct.getSysSts().endsWith(SysCtlSts.SIGNOUT.getCode())) {
            throw new RuntimeException("系统状态错误，签退后方可进行对帐操作。");
        }

        //depService.doSend

        //db

        //check db

        boolean result = false;
        //余额核对无误后，置系统状态
        if (1==1) {
            hmSct = getAppSysStatus();
            SysCtlSts currentSysSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
            if (currentSysSts.equals(SysCtlSts.HMB_DETLCHK_SUCCESS)) {
                hmSct.setSysSts(SysCtlSts.HMB_CHK_SUCCESS.getCode());
            }else{
                hmSct.setSysSts(SysCtlSts.HMB_BALCHK_SUCCESS.getCode());
            }
            //TODO
            hmSct.setHostChkDt(new Date());
            hmSctMapper.updateByPrimaryKey(hmSct);
            result = true;
        }

        return result;
    }

    public boolean processChkActDetl(){
        //TODO
        HmSct hmSct = getAppSysStatus();
        if (!hmSct.getSysSts().endsWith(SysCtlSts.SIGNOUT.getCode())) {
            throw new RuntimeException("系统状态错误，签退后方可进行对帐操作。");
        }

        //depService.doSend

        //db

        //check db

        boolean result = false;
        //余额核对无误后，置系统状态
        if (1==1) {
            hmSct = getAppSysStatus();
            SysCtlSts currentSysSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
            if (currentSysSts.equals(SysCtlSts.HMB_BALCHK_SUCCESS)) {
                hmSct.setSysSts(SysCtlSts.HMB_CHK_SUCCESS.getCode());
            }else{
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
