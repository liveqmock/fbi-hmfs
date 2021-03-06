package hmfs.view;

import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.repository.hmfs.model.HmChkTxn;
import common.repository.hmfs.model.HmSysCtl;
import common.repository.hmfs.model.HmTxnStl;
import common.repository.hmfs.model.hmfs.HmChkTxnVO;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.common.utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流水对帐处理
 * zhanrui
 * 2012/3/24
 */
@ManagedBean
@ViewScoped
public class TxnChkAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(TxnChkAction.class);

    private ActinfoQryParam qryParam = new ActinfoQryParam();
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;
    private String sendSysId;

    private String bankId;

    private List<HmChkTxnVO> detlList;
    private HmChkTxnVO selectedRecord;
    private HmChkTxnVO[] selectedRecords;

    //查询到的交易明细对账结果表数据
    private List<HmTxnStl> hmTxnStlList;

    private HmChkTxn hmChkTxn ;

    private int totalCount;
    private int totalErrorCount;
    private int totalSuccessCount;

    private BigDecimal totalAmt;

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

        // hanjianlong add 20130106 start
        Map<String, String> paramsmap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String pkid = paramsmap.get("pkid1");
        String action = paramsmap.get("action");
        logger.info("PKID: " + pkid + " ACTION:" + action);

        if(!StringUtils.isEmpty(pkid)) {
            //获得选择的行对象
            hmChkTxn = actInfoService .selectHmChkTxnByPkid(pkid);
            //获得所选行对象的[流水编号]
            String strMsgSn = hmChkTxn.getMsgSn();
            strMsgSn = strMsgSn == null ? "" : strMsgSn;
            //根据流水编号查询交易明细对账结果表
            hmTxnStlList = actInfoService.selectHmTxnStlAccordingToMsgSn(strMsgSn);
        }
        // hanjianlong add 20130106 end

        HmSysCtl hmSysCtl = appMngService.getAppSysStatus();
        this.bankId = hmSysCtl.getBankId();
        SysCtlSts sysCtlSts = SysCtlSts.valueOfAlias(hmSysCtl.getSysSts());
        this.sysSts = sysCtlSts.getTitle();
        this.sendSysId = hmSysCtl.getSendSysId();
    }

    public String onQueryHmb() {
        try {
            this.totalCount = actInfoService.countChkTxnRecordNumber(this.qryParam.getStartDate());
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                return null;
            }
            this.detlList = actInfoService.selectChkTxnFailResult("00", this.qryParam.getStartDate());
            this.totalErrorCount = this.detlList.size();
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQueryFailCbs() {
        try {
            this.totalCount = actInfoService.countChkTxnRecordNumber(this.qryParam.getStartDate());
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                return null;
            }
            this.detlList = actInfoService.selectChkTxnFailResult(this.bankId, this.qryParam.getStartDate());
            this.totalErrorCount = this.detlList.size();
            if (this.totalErrorCount == 0) {
                MessageUtil.addError("无不平账数据。");
                return null;
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQuerySuccCbs() {
        try {
            this.totalCount = actInfoService.countChkTxnRecordNumber(this.qryParam.getStartDate());
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                return null;
            }
            this.detlList = actInfoService.selectChkTxnSuccResult(this.bankId, this.qryParam.getStartDate());
            this.totalSuccessCount = this.detlList.size();
            if (this.totalSuccessCount == 0) {
                MessageUtil.addError("无平账数据。");
                return null;
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public void onRowSelect(SelectEvent event) {
        try {
            //获得选择的行对象
            selectedRecord = (HmChkTxnVO) event.getObject();
            //获得所选行对象的[流水编号]
            String strMsgSn = selectedRecord.getMsgSn1();
            strMsgSn = strMsgSn == null ? "" : strMsgSn;
            //根据流水编号查询交易明细对账结果表
            hmTxnStlList = actInfoService.selectHmTxnStlAccordingToMsgSn(strMsgSn);
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
    }

    //=============================

    public List<HmTxnStl> getHmTxnStlList() {
        return hmTxnStlList;
    }

    public void setHmTxnStlList(List<HmTxnStl> hmTxnStlList) {
        this.hmTxnStlList = hmTxnStlList;
    }

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

    public List<HmChkTxnVO> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<HmChkTxnVO> detlList) {
        this.detlList = detlList;
    }

    public HmChkTxnVO[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(HmChkTxnVO[] selectedRecords) {
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

    public HmChkTxnVO getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(HmChkTxnVO selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public int getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public void setTotalSuccessCount(int totalSuccessCount) {
        this.totalSuccessCount = totalSuccessCount;
    }
}
