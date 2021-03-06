package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSysCtl;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AppMngAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(AppMngAction.class);

    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;
    private String lastSignonTime;
    private String lastSignoutTime;
    private String hostChkTime;
    private String hmbChkTime;
    private HmSysCtl hmSysCtl;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        this.sysSts = sysCtlSts.getTitle();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.lastSignonTime = sdf.format(hmSysCtl.getSignonDt());
        this.lastSignoutTime = sdf.format(hmSysCtl.getSignoutDt());
        this.hostChkTime = sdf.format(hmSysCtl.getHostChkDt());
        this.hmbChkTime = sdf.format(hmSysCtl.getHmbChkDt());
    }

    public String onLogon() {
        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        //if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHK_OVER)) {
            try {
                String response = depService.process("1007000");
                if (response.startsWith("0000")) { //成功
                     MessageUtil.addInfo("向国土局系统签到成功......");
                }else{
                    MessageUtil.addError("签到失败。" + response);
                }
            } catch (Exception e) {
                logger.error("签到失败。请重新发起签到。" ,e);
                MessageUtil.addError("签到失败。请重新发起签到。" + e.getMessage());
            }
        //} else {
        //    MessageUtil.addError("系统初始或与国土局对帐完成后方可签到。");
        //}
        init();
        return null;
    }

    public String onLogout() {
        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        //if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                String response = depService.process("1007001");
                if (response.startsWith("0000")) { //成功
                    MessageUtil.addInfo("向国土局系统签退成功......");
                }else{
                    MessageUtil.addError("签退失败。" + response);
                }
            } catch (Exception e) {
               logger.error("签退失败。请重新发起签退。" ,e);
               MessageUtil.addError("签退失败。请重新发起签退。" + e.getMessage());
            }
        //} else {
            //MessageUtil.addError("系统签到完成后方可签退。");
        //}
        init();
        return null;
    }

    /**
     * 日终国土局对帐
     *
     * @return
     */
    public String onHmbDailyChk() {
        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        //if (sysCtlSts.equals(SysCtlSts.HOST_CHK_OVER) || sysCtlSts.equals(SysCtlSts.SIGNOUT)) {
            try {
                String response = depService.process("1007003");
                if (response.startsWith("0000")) { //成功
                    MessageUtil.addInfo("余额对帐处理完成，余额对帐成功。");
                }else{
                    MessageUtil.addError("余额对帐处理未完成。" + response);
                }
            } catch (Exception e) {
                logger.error("对帐处理失败， 请重新发起对帐。" ,e);
                MessageUtil.addError("对帐处理失败， 请重新发起对帐。" + e.getMessage());
            }
        //} else {
        //    MessageUtil.addError("签退后或主机对帐后方可进行国土局对帐。");
        //}
        init();
        return null;
    }

    //每日主机对帐(供非联机模式使用)
    public String onCbsDailyChk() {
        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNOUT)) {
            try {
                //TODO  需重新定义交易号
                String response = depService.process("---");
                if (response.startsWith("0000")) { //成功
                    MessageUtil.addInfo("对帐处理完成，主机对帐成功。");
                }else{
                    MessageUtil.addError("主机对帐处理未完成。" + response);
                }
            } catch (Exception e) {
              logger.error("处理失败， 请重新发起对帐。" ,e);
               MessageUtil.addError("处理失败， 请重新发起对帐。" + e.getMessage());
            }
        } else {
            MessageUtil.addError("签退后方可与主机对帐。");
        }
        init();
        return null;
    }

    //=============================

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }

    public String getSysTime() {
        return sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }

    public String getSysSts() {
        return sysSts;
    }

    public void setSysSts(String sysSts) {
        this.sysSts = sysSts;
    }

    public HmSysCtl getHmSysCtl() {
        return hmSysCtl;
    }

    public void setHmSysCtl(HmSysCtl hmSysCtl) {
        this.hmSysCtl = hmSysCtl;
    }

    public String getLastSignonTime() {
        return lastSignonTime;
    }

    public void setLastSignonTime(String lastSignonTime) {
        this.lastSignonTime = lastSignonTime;
    }

    public String getLastSignoutTime() {
        return lastSignoutTime;
    }

    public void setLastSignoutTime(String lastSignoutTime) {
        this.lastSignoutTime = lastSignoutTime;
    }

    public String getHostChkTime() {
        return hostChkTime;
    }

    public void setHostChkTime(String hostChkTime) {
        this.hostChkTime = hostChkTime;
    }

    public String getHmbChkTime() {
        return hmbChkTime;
    }

    public void setHmbChkTime(String hmbChkTime) {
        this.hmbChkTime = hmbChkTime;
    }

    public DepService getDepService() {
        return depService;
    }

    public void setDepService(DepService depService) {
        this.depService = depService;
    }
}
