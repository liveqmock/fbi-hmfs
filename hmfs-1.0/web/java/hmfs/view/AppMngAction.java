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
 * Time: ����9:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AppMngAction {
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;
    private String lastSignonTime;
    private HmSct hmSct;

    @ManagedProperty(value="#{appMngService}")
    private AppMngService appMngService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy��MM��dd��").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        this.sysSts = sysCtlSts.getTitle();
        this.lastSignonTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hmSct.getSignonDt());
    }

    public String onNextButton() {
        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHECKED)) {
            //
            try {
                appMngService.processSignon();
                MessageUtil.addInfo("�������ϵͳǩ���ɹ�......");
            } catch (Exception e) {
                MessageUtil.addError("ǩ��ʧ�ܡ�" + e.getMessage());
            }
        }else{
            MessageUtil.addError("ϵͳ��ʼ��������ֶ�����ɺ󷽿�ǩ����");
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

    public HmSct getHmSct() {
        return hmSct;
    }

    public void setHmSct(HmSct hmSct) {
        this.hmSct = hmSct;
    }

    public String getLastSignonTime() {
        return lastSignonTime;
    }

    public void setLastSignonTime(String lastSignonTime) {
        this.lastSignonTime = lastSignonTime;
    }
}
