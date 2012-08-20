package hmfs.view;

import common.enums.DCFlagCode;
import common.enums.FundActType;
import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.repository.hmfs.model.*;
import common.repository.hmfs.model.hmfs.HmFundTxnVO;
import hmfs.common.model.ActinfoQryParam;
import hmfs.common.util.JxlsManager;
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
import java.util.ArrayList;
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
    private List<HmTxnStlDbl> stlDetlDblList;
    private HmActFund[] selectedFundBalRecords;
    private HmActStl[] selectedStlBalRecords;
    private HmTxnFund[] selectedFundDetlRecord;
    private HmTxnStl[] selectedStlDetlRecord;

    //核算分户交易明细
    private List<HmFundTxnVO> indiviFundDetlList;

    private FundActnoStatus fundActnoStatus = FundActnoStatus.NORMAL;
    private DCFlagCode dcFlagCode = DCFlagCode.DEPOSIT;
    private FundActType fundActType = FundActType.PROJECT;

    private List<SelectItem> actnoStatusList = new ArrayList<SelectItem>();
    private List<SelectItem> fundActTypeList = new ArrayList<SelectItem>();

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

        for (FundActnoStatus c : FundActnoStatus.values())
            actnoStatusList.add(new SelectItem(c.getCode(), c.getTitle()));

        fundActTypeList.add(new SelectItem("", ""));
        for (FundActType c : FundActType.values())
            fundActTypeList.add(new SelectItem(c.getCode(), c.getTitle()));

        //initList();
    }

    private void initList() {
        this.fundBalList = actInfoService.selectAllFundActBalList(qryParam);
        if (this.fundBalList.size() > 0) {
            this.qryParam.setStartActno(this.fundBalList.get(0).getFundActno1());
            this.qryParam.setEndActno(this.fundBalList.get(this.fundBalList.size() - 1).getFundActno1());
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

    public String onQueryFundDetl() {
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

    public String onQueryCbsDblDetl() {
        try {
            //this.stlDetlList = actInfoService.selectStlTxnDetl(qryParam);
             this.stlDetlDblList = actInfoService.selectStlTxnDblDetl(qryParam);
            if (stlDetlDblList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQueryCbsDetl() {
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

    //分户交易明细
    public String onQueryIndiviFundDetl() {
        try {
            this.indiviFundDetlList = actInfoService.selectIndiviFundTxnDetlList(qryParam);
            if (indiviFundDetlList.isEmpty()) {
                MessageUtil.addWarn("数据不存在...");
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onExportExcelForIndiviFund() {
        if (this.indiviFundDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "分户账务交易清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportIndiviFundTxnList(excelFilename, this.indiviFundDetlList);
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

    public List<SelectItem> getActnoStatusList() {
        return actnoStatusList;
    }

    public void setActnoStatusList(List<SelectItem> actnoStatusList) {
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

    public List<HmTxnStlDbl> getStlDetlDblList() {
        return stlDetlDblList;
    }

    public void setStlDetlDblList(List<HmTxnStlDbl> stlDetlDblList) {
        this.stlDetlDblList = stlDetlDblList;
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

    public DCFlagCode getDcFlagCode() {
        return dcFlagCode;
    }

    public void setDcFlagCode(DCFlagCode dcFlagCode) {
        this.dcFlagCode = dcFlagCode;
    }

    public List<SelectItem> getFundActTypeList() {
        return fundActTypeList;
    }

    public void setFundActTypeList(List<SelectItem> fundActTypeList) {
        this.fundActTypeList = fundActTypeList;
    }

    public FundActType getFundActType() {
        return fundActType;
    }

    public void setFundActType(FundActType fundActType) {
        this.fundActType = fundActType;
    }

    public List<HmFundTxnVO> getIndiviFundDetlList() {
        return indiviFundDetlList;
    }

    public void setIndiviFundDetlList(List<HmFundTxnVO> indiviFundDetlList) {
        this.indiviFundDetlList = indiviFundDetlList;
    }
}
