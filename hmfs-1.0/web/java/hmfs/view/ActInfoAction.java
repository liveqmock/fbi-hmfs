package hmfs.view;

import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.repository.hmfs.model.*;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 帐户管理
 */
@ManagedBean
@ViewScoped
public class ActInfoAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ActInfoAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ActinfoQryParam qryParam = new ActinfoQryParam();
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;

    private String cbsActno;

    private List<HmActinfoFund> fundBalList;
    private List<HmActinfoCbs> cbsBalList;
    private List<TxnFundLog> fundDetlList;
    private List<TxnCbsLog>  cbsDetlList;
    private HmActinfoFund[] selectedFundBalRecords;
    private HmActinfoCbs[] selectedCbsBalRecords;
    private TxnFundLog[] selectedFundDetlRecords;
    private TxnCbsLog[] selectedCbsDetlRecords;

    private FundActnoStatus fundActnoStatus = FundActnoStatus.NORMAL;

    private SelectItem[] actnoStatusList = new SelectItem[]{
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

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.qryParam.setStartDate(date);
        this.qryParam.setEndDate(date);
        this.qryParam.setActnoStatus(FundActnoStatus.NORMAL.getCode());

        HmSct hmSct = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSct.getSysSts());
        this.sysSts = sysCtlSts.getTitle();

        this.cbsActno = actInfoService.selectCbsActno();
        this.qryParam.setCbsActno(this.cbsActno);
        initList();
    }

    private void initList(){
        this.fundBalList = actInfoService.selectAllFundActBal(qryParam);
        this.fundDetlList = actInfoService.selectFundActDetl(qryParam);
        this.cbsBalList = actInfoService.selectAllCbsActBal(qryParam);
        this.cbsDetlList = actInfoService.selectCbsActDetl(qryParam);
    }

    public String onQueryFundBal() {
        if (StringUtils.isEmpty(qryParam.getEndActno())) {
            qryParam.setEndActno(qryParam.getStartActno());
        }
        try {
            this.fundBalList = actInfoService.selectFundActBal(qryParam);
            if (fundBalList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQueryFundDetl(){
        if (StringUtils.isEmpty(qryParam.getEndActno())) {
            qryParam.setEndActno(qryParam.getStartActno());
        }

        try {
            this.fundDetlList = actInfoService.selectFundActDetl(qryParam);
            if (fundDetlList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }
    public String onQueryCbsBal() {
        try {
            this.cbsBalList = actInfoService.selectCbsActBal(qryParam);
            if (cbsBalList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQueryCbsDetl(){
        try {
            this.cbsDetlList = actInfoService.selectCbsActDetl(qryParam);
            if (cbsDetlList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
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

    public SelectItem[] getActnoStatusList() {
        return actnoStatusList;
    }

    public void setActnoStatusList(SelectItem[] actnoStatusList) {
        this.actnoStatusList = actnoStatusList;
    }

    public ActinfoQryParam getQryParam() {
        return qryParam;
    }

    public void setQryParam(ActinfoQryParam qryParam) {
        this.qryParam = qryParam;
    }

    public List<HmActinfoFund> getFundBalList() {
        return fundBalList;
    }

    public void setFundBalList(List<HmActinfoFund> fundBalList) {
        this.fundBalList = fundBalList;
    }

    public List<TxnFundLog> getFundDetlList() {
        return fundDetlList;
    }

    public void setFundDetlList(List<TxnFundLog> fundDetlList) {
        this.fundDetlList = fundDetlList;
    }

    public HmActinfoFund[] getSelectedFundBalRecords() {
        return selectedFundBalRecords;
    }

    public void setSelectedFundBalRecords(HmActinfoFund[] selectedFundBalRecords) {
        this.selectedFundBalRecords = selectedFundBalRecords;
    }

    public TxnFundLog[] getSelectedFundDetlRecords() {
        return selectedFundDetlRecords;
    }

    public void setSelectedFundDetlRecords(TxnFundLog[] selectedFundDetlRecords) {
        this.selectedFundDetlRecords = selectedFundDetlRecords;
    }

    public FundActnoStatus getFundActnoStatus() {
        return fundActnoStatus;
    }

    public void setFundActnoStatus(FundActnoStatus fundActnoStatus) {
        this.fundActnoStatus = fundActnoStatus;
    }

    public List<HmActinfoCbs> getCbsBalList() {
        return cbsBalList;
    }

    public void setCbsBalList(List<HmActinfoCbs> cbsBalList) {
        this.cbsBalList = cbsBalList;
    }

    public List<TxnCbsLog> getCbsDetlList() {
        return cbsDetlList;
    }

    public void setCbsDetlList(List<TxnCbsLog> cbsDetlList) {
        this.cbsDetlList = cbsDetlList;
    }

    public HmActinfoCbs[] getSelectedCbsBalRecords() {
        return selectedCbsBalRecords;
    }

    public void setSelectedCbsBalRecords(HmActinfoCbs[] selectedCbsBalRecords) {
        this.selectedCbsBalRecords = selectedCbsBalRecords;
    }

    public TxnCbsLog[] getSelectedCbsDetlRecords() {
        return selectedCbsDetlRecords;
    }

    public void setSelectedCbsDetlRecords(TxnCbsLog[] selectedCbsDetlRecords) {
        this.selectedCbsDetlRecords = selectedCbsDetlRecords;
    }

    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }
}
