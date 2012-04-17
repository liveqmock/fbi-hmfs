package hmfs.view;

import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmSysCtl;
import common.repository.hmfs.model.hmfs.HmChkActVO;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
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
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 余额对帐处理
 * zhanrui
 * 2012/3/24
 */
@ManagedBean
@ViewScoped
public class ActChkAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ActChkAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ActinfoQryParam qryParam = new ActinfoQryParam();
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;
    private String sendSysId;


    private List<HmChkActVO> detlList;
    private HmChkActVO selectedRecord;
    private HmChkActVO[] selectedRecords;

    private int totalCount;
    private int totalErrorCount;

    private BigDecimal totalAmt;

    private String bankId;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;

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
        this.bankId = hmSysCtl.getBankId();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        this.sysSts = sysCtlSts.getTitle();
        this.sendSysId = hmSysCtl.getSendSysId();
        //this.detlList = actInfoService.selectChkActResult("00", this.qryParam.getStartDate());
    }

    public String onQueryHmb() {
        try {
            this.totalCount = actInfoService.countChkActRecordNumber(this.qryParam.getStartDate());
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                return null;
            }
            this.detlList = actInfoService.selectChkActResult("00", this.qryParam.getStartDate());
            this.totalErrorCount = this.detlList.size();
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQueryCbs() {
        try {
            this.totalCount = actInfoService.countChkActRecordNumber(this.qryParam.getStartDate());
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                return null;
            }
            this.detlList = actInfoService.selectChkActResult(this.bankId, this.qryParam.getStartDate());
            this.totalErrorCount = this.detlList.size();
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

    public String getSysSts() {
        return sysSts;
    }

    public void setSysSts(String sysSts) {
        this.sysSts = sysSts;
    }

    public String getSendSysId() {
        return sendSysId;
    }

    public void setSendSysId(String sendSysId) {
        this.sendSysId = sendSysId;
    }

    public List<HmChkActVO> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<HmChkActVO> detlList) {
        this.detlList = detlList;
    }

    public HmChkActVO[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(HmChkActVO[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public AppMngService getAppMngService() {
        return appMngService;
    }

    public void setAppMngService(AppMngService appMngService) {
        this.appMngService = appMngService;
    }

    public ActInfoService getActInfoService() {
        return actInfoService;
    }

    public void setActInfoService(ActInfoService actInfoService) {
        this.actInfoService = actInfoService;
    }

    public DepService getDepService() {
        return depService;
    }

    public void setDepService(DepService depService) {
        this.depService = depService;
    }

    public ActinfoQryParam getQryParam() {
        return qryParam;
    }

    public void setQryParam(ActinfoQryParam qryParam) {
        this.qryParam = qryParam;
    }

    public int getTotalErrorCount() {
        return totalErrorCount;
    }

    public void setTotalErrorCount(int totalErrorCount) {
        this.totalErrorCount = totalErrorCount;
    }

    public HmChkActVO getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(HmChkActVO selectedRecord) {
        this.selectedRecord = selectedRecord;
    }
}
