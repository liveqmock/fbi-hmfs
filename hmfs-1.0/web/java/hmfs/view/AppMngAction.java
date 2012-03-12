package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSct;
import hmfs.service.AppMngService;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
public class AppMngAction {
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;

    @ManagedProperty(value="#{appMngService}")
    private AppMngService appMngService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        SysCtlSts sysCtlSts = getSysStatus();
        this.sysSts = sysCtlSts.getTitle();
    }

    public String onNextButton() {
        SysCtlSts sysCtlSts = getSysStatus();
        if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHECKED)) {
            //
            MessageUtil.addInfo("对帐成功。");
        }else{
            MessageUtil.addError("系统初始或与国土局对帐完成后方可签到。");
        }
        return null;
    }

    private SysCtlSts getSysStatus() {
        HmSct hmSct = appMngService.getAppSysStatus();
        return SysCtlSts.valueOfAlias(hmSct.getSysSts());
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
}
