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

    private ActinfoQryParam qryParam = new ActinfoQryParam();
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;

    private String cbsActno;

    private List<HmActFund> fundBalList;
    private List<HmActStl> stlBalList;
    private List<HmTxnFund> fundDetlList;
    private List<HmTxnStl> stlDetlList;
    private HmActFund[] selectedFundBalRecords;
    private HmActStl[] selectedStlBalRecords;
    private HmTxnFund[] selectedFundDetlRecord;
    private HmTxnStl[] selectedStlDetlRecord;

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

        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        this.sysSts = sysCtlSts.getTitle();

        this.cbsActno = actInfoService.selectStlActno();
        this.qryParam.setCbsActno(this.cbsActno);
        initList();
    }

    private void initList(){
        this.fundBalList = actInfoService.selectAllFundActBalList(qryParam);
        if (this.fundBalList.size() > 0) {
            this.qryParam.setStartActno(this.fundBalList.get(0).getFundActno1());
            this.qryParam.setEndActno(this.fundBalList.get(this.fundBalList.size()-1).getFundActno1());
        }
        this.fundDetlList = actInfoService.selectFundTxnDetlList(qryParam);
        this.stlBalList = actInfoService.selectAllStlActBalList(qryParam);
        this.stlDetlList = actInfoService.selectStlTxnDetl(qryParam);
    }

    public String onQueryFundBal() {
        if (StringUtils.isEmpty(qryParam.getEndActno())) {
            qryParam.setEndActno(qryParam.getStartActno());
        }
        try {
            this.fundBalList = actInfoService.selectFundActBalList(qryParam);
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
            this.fundDetlList = actInfoService.selectFundTxnDetlList(qryParam);
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
            this.stlBalList = actInfoService.selectStlActBalList(qryParam);
            if (stlBalList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQueryCbsDetl(){
        try {
            this.stlDetlList = actInfoService.selectStlTxnDetl(qryParam);
            if (stlDetlList.isEmpty()) {
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

    public List<HmActFund> getFundBalList() {
        return fundBalList;
    }

    public void setFundBalList(List<HmActFund> fundBalList) {
        this.fundBalList = fundBalList;
    }

    public List<HmTxnFund> getFundDetlList() {
        return fundDetlList;
    }

    public void setFundDetlList(List<HmTxnFund> fundDetlList) {
        this.fundDetlList = fundDetlList;
    }

    public HmActFund[] getSelectedFundBalRecords() {
        return selectedFundBalRecords;
    }

    public void setSelectedFundBalRecords(HmActFund[] selectedFundBalRecords) {
        this.selectedFundBalRecords = selectedFundBalRecords;
    }

    public HmTxnFund[] getSelectedFundDetlRecord() {
        return selectedFundDetlRecord;
    }

    public void setSelectedFundDetlRecord(HmTxnFund[] selectedFundDetlRecord) {
        this.selectedFundDetlRecord = selectedFundDetlRecord;
    }

    public FundActnoStatus getFundActnoStatus() {
        return fundActnoStatus;
    }

    public void setFundActnoStatus(FundActnoStatus fundActnoStatus) {
        this.fundActnoStatus = fundActnoStatus;
    }

    public List<HmActStl> getStlBalList() {
        return stlBalList;
    }

    public void setStlBalList(List<HmActStl> stlBalList) {
        this.stlBalList = stlBalList;
    }

    public List<HmTxnStl> getStlDetlList() {
        return stlDetlList;
    }

    public void setStlDetlList(List<HmTxnStl> stlDetlList) {
        this.stlDetlList = stlDetlList;
    }

    public HmActStl[] getSelectedStlBalRecords() {
        return selectedStlBalRecords;
    }

    public void setSelectedStlBalRecords(HmActStl[] selectedStlBalRecords) {
        this.selectedStlBalRecords = selectedStlBalRecords;
    }

    public HmTxnStl[] getSelectedStlDetlRecord() {
        return selectedStlDetlRecord;
    }

    public void setSelectedStlDetlRecord(HmTxnStl[] selectedStlDetlRecord) {
        this.selectedStlDetlRecord = selectedStlDetlRecord;
    }

    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }
}
