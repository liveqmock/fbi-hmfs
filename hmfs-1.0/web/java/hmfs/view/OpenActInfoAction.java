package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSct;
import hmfs.common.model.Msg031;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class OpenActInfoAction {
    private static final Logger logger = LoggerFactory.getLogger(OpenActInfoAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Msg031 msg031= new Msg031();

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;


    @PostConstruct
    public void init() {

    }

    public String onCommit() {
        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        if (sysCtlSts.equals(SysCtlSts.SIGNON)) {
            try {
                String response = depService.process("1000|openact|" + getMsg031Str());
                String[] fields = response.split("\\|");
                if ("0000".endsWith(fields[1])) { //签到成功
                     MessageUtil.addInfo("结算户开户处理成功......");
                }else{
                    MessageUtil.addError("处理失败。" + response);
                }
            } catch (Exception e) {
                logger.error("处理失败。" ,e);
                MessageUtil.addError("处理失败。" + e.getMessage());
            }
        } else {
            MessageUtil.addError("系统状态错误。");
        }
        init();
        return null;
    }

    private String getMsg031Str(){
        StringBuilder sb = new StringBuilder();
        sb.append(msg031.cbsActno)
                .append("|").append(msg031.cbsActtype)
                .append("|").append(msg031.cbsActname)
                .append("|").append(msg031.bankName)
                .append("|").append(msg031.branchId)
                .append("|").append(msg031.depositType)
                .append("|").append(msg031.orgId)
                .append("|").append(msg031.orgType)
                .append("|").append(msg031.orgName);
        return sb.toString();
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }


    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        OpenActInfoAction.sdf = sdf;
    }


    public DepService getDepService() {
        return depService;
    }

    public void setDepService(DepService depService) {
        this.depService = depService;
    }

    public Msg031 getMsg031() {
        return msg031;
    }

    public void setMsg031(Msg031 msg031) {
        this.msg031 = msg031;
    }

}
