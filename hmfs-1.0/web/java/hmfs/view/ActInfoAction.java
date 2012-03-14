package hmfs.view;

import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmActinfoFund;
import common.repository.hmfs.model.HmSct;
import common.repository.hmfs.model.TxnFundLog;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-12
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ActInfoAction {
    private static final Logger logger = LoggerFactory.getLogger(ActInfoAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ActinfoQryParam qryParam = new ActinfoQryParam();
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;
    private List<HmActinfoFund> balList;
    private List<TxnFundLog> detlList;
    private HmActinfoFund[] selectedBalRecords;
    private TxnFundLog[] selectedDetlRecords;

    private static SelectItem[] actnoStatusList = new SelectItem[]{
            new SelectItem("0", "正常"),
            new SelectItem("1", "销户"),
            new SelectItem("2", "封帐")
    };

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;


    @PostConstruct
    public void init() {
        this.sysDate = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
        this.txnDate = this.sysDate;
        this.sysTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        this.sysSts = sysCtlSts.getTitle();
    }

    private void initList(){
        //this.balList = actInfoService.selectFundActInfo(qryParam);
    }

    public String onQueryBal() {
        this.balList = actInfoService.selectFundActInfo(qryParam);
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

    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        ActInfoAction.sdf = sdf;
    }


    public ActInfoService getActInfoService() {
        return actInfoService;
    }

    public void setActInfoService(ActInfoService actInfoService) {
        this.actInfoService = actInfoService;
    }

    public static SelectItem[] getActnoStatusList() {
        return actnoStatusList;
    }

    public static void setActnoStatusList(SelectItem[] actnoStatusList) {
        ActInfoAction.actnoStatusList = actnoStatusList;
    }

    public ActinfoQryParam getQryParam() {
        return qryParam;
    }

    public void setQryParam(ActinfoQryParam qryParam) {
        this.qryParam = qryParam;
    }

    public List<HmActinfoFund> getBalList() {
        return balList;
    }

    public void setBalList(List<HmActinfoFund> balList) {
        this.balList = balList;
    }

    public List<TxnFundLog> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<TxnFundLog> detlList) {
        this.detlList = detlList;
    }

    public HmActinfoFund[] getSelectedBalRecords() {
        return selectedBalRecords;
    }

    public void setSelectedBalRecords(HmActinfoFund[] selectedBalRecords) {
        this.selectedBalRecords = selectedBalRecords;
    }

    public TxnFundLog[] getSelectedDetlRecords() {
        return selectedDetlRecords;
    }

    public void setSelectedDetlRecords(TxnFundLog[] selectedDetlRecords) {
        this.selectedDetlRecords = selectedDetlRecords;
    }
}
