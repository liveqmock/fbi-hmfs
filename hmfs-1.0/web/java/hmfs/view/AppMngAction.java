package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSct;
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
 * Time: ����9:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AppMngAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(AppMngAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;
    private String lastSignonTime;
    private String lastSignoutTime;
    private String hostChkTime;
    private String hmbChkTime;
    private HmSct hmSct;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy��MM��dd��").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        this.sysSts = sysCtlSts.getTitle();

        this.lastSignonTime = sdf.format(hmSct.getSignonDt());
        this.lastSignoutTime = sdf.format(hmSct.getSignoutDt());
        this.hostChkTime = sdf.format(hmSct.getHostChkDt());
        this.hmbChkTime = sdf.format(hmSct.getHmbChkDt());
    }

    public String onLogon() {
        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.INIT) || sysCtlSts.equals(SysCtlSts.HMB_CHK_SUCCESS)) {
            try {
                String response = depService.process("1007000");
                if (response.startsWith("0000")) { //�ɹ�
                     MessageUtil.addInfo("�������ϵͳǩ���ɹ�......");
                }else{
                    MessageUtil.addError("ǩ��ʧ�ܡ�" + response);
                }
            } catch (Exception e) {
                logger.error("ǩ��ʧ�ܡ������·���ǩ����" ,e);
                MessageUtil.addError("ǩ��ʧ�ܡ������·���ǩ����" + e.getMessage());
            }
        } else {
            MessageUtil.addError("ϵͳ��ʼ��������ֶ�����ɺ󷽿�ǩ����");
        }
        init();
        return null;
    }

    public String onLogout() {
        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                String response = depService.process("1007001");
                if (response.startsWith("0000")) { //�ɹ�
                    MessageUtil.addInfo("�������ϵͳǩ�˳ɹ�......");
                }else{
                    MessageUtil.addError("ǩ��ʧ�ܡ�" + response);
                }
            } catch (Exception e) {
               logger.error("ǩ��ʧ�ܡ������·���ǩ�ˡ�" ,e);
               MessageUtil.addError("ǩ��ʧ�ܡ������·���ǩ�ˡ�" + e.getMessage());
            }
        } else {
            MessageUtil.addError("ϵͳǩ����ɺ󷽿�ǩ�ˡ�");
        }
        init();
        return null;
    }

    /**
     * ����������
     *
     * @return
     */
    public String onCheckBal() {
        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.HOST_CHK_SUCCESS)) {
            try {
                String response = depService.process("1007003");
                if (response.startsWith("0000")) { //�ɹ�
                    MessageUtil.addInfo("�����ʴ�����ɣ������ʳɹ���");
                }else{
                    MessageUtil.addError("�����ʴ���δ��ɡ�" + response);
                }
            } catch (Exception e) {
                logger.error("���ʴ���ʧ�ܣ� �����·�����ʡ�" ,e);
                MessageUtil.addError("���ʴ���ʧ�ܣ� �����·�����ʡ�" + e.getMessage());
            }
        } else {
            MessageUtil.addError("�������ʳɹ��󷽿ɽ��й����ֶ��ʡ�");
        }
        init();
        return null;
    }

    public String onCheckDetl() {
        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.HOST_CHK_SUCCESS) ||sysCtlSts.equals(SysCtlSts.HMB_BALCHK_SUCCESS)) {
            try {
                String response = depService.process("1007004");
                if (response.startsWith("0000")) { //�ɹ�
                    MessageUtil.addInfo("��ˮ���ʴ�����ɣ���ˮ���ʳɹ���");
                }else{
                    MessageUtil.addError("��ˮ���ʴ���δ��ɡ�" + response);
                }
            } catch (Exception e) {
              logger.error("���ʴ���ʧ�ܣ� �����·�����ʡ�" ,e);
               MessageUtil.addError("���ʴ���ʧ�ܣ� �����·�����ʡ�" + e.getMessage());
            }
        } else {
            MessageUtil.addError("�������ʳɹ����������������ɺ󷽿ɽ��й�������ˮ���ʡ�");
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

    public String getLastSignoutTime() {
        return lastSignoutTime;
    }

    public void setLastSignoutTime(String lastSignoutTime) {
        this.lastSignoutTime = lastSignoutTime;
    }

    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        AppMngAction.sdf = sdf;
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
