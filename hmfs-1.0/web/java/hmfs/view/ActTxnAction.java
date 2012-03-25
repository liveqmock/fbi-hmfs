package hmfs.view;

import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmSysCtl;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
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
 * 帐务类交易处理
 */
@ManagedBean
@ViewScoped
public class ActTxnAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ActTxnAction.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ActinfoQryParam qryParam = new ActinfoQryParam();
    private String sysDate;
    private String sysTime;
    private String txnDate;
    private String sysSts;

    private String cbsActno;
    private String msgSn;
    private BigDecimal txnAmt;

    private HmMsgIn summaryMsg;
    private List<HmMsgIn> subMsgList;
    private HmMsgIn[] selectedRecords;

    private int totalCount;
    
    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;
    private boolean checkPass = false;


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
        //initList();
    }

    private void initList(){
    }

    public String onQueryDeposit() {
        try {
            this.summaryMsg = actInfoService.selectSummaryMsg(msgSn);
            TxnCtlSts txnCtlSts = TxnCtlSts.valueOfAlias(this.summaryMsg.getTxnCtlSts());
            if (!txnCtlSts.equals(TxnCtlSts.INIT)) {
                logger.error("交易处理状态错误。" +  this.summaryMsg.getTxnCtlSts());
                MessageUtil.addError("本笔申请单处理状态错误，当前状态为：" + txnCtlSts.getTitle());
            }
            //this.subMsgList = assembleMsgInList(actInfoService.selectSubMsgList(msgSn));
            this.subMsgList = actInfoService.selectPendDepositSubMsgList(msgSn);

            this.totalCount = this.subMsgList.size();
            //TODO 总分金额核对
            
            BigDecimal msgTxnAmt = this.summaryMsg.getTxnAmt1();
            if (msgTxnAmt.compareTo(this.txnAmt) != 0) {
                this.checkPass = false;
                MessageUtil.addError("交易金额不符！此申请单金额为：" + msgTxnAmt.toString());
            }else {
                this.checkPass = true;
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    private List<HmMsgIn> assembleMsgInList(List<HmMsgIn> hmMsgInList) {
        for (HmMsgIn hmMsgIn : hmMsgInList) {

        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    //=============================

    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        ActTxnAction.sdf = sdf;
    }

    public ActinfoQryParam getQryParam() {
        return qryParam;
    }

    public void setQryParam(ActinfoQryParam qryParam) {
        this.qryParam = qryParam;
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

    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }

    public String getMsgSn() {
        return msgSn;
    }

    public void setMsgSn(String msgSn) {
        this.msgSn = msgSn;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public HmMsgIn getSummaryMsg() {
        return summaryMsg;
    }

    public void setSummaryMsg(HmMsgIn summaryMsg) {
        this.summaryMsg = summaryMsg;
    }

    public List<HmMsgIn> getSubMsgList() {
        return subMsgList;
    }

    public void setSubMsgList(List<HmMsgIn> subMsgList) {
        this.subMsgList = subMsgList;
    }

    public HmMsgIn[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(HmMsgIn[] selectedRecords) {
        this.selectedRecords = selectedRecords;
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

    public boolean isCheckPass() {
        return checkPass;
    }

    public void setCheckPass(boolean checkPass) {
        this.checkPass = checkPass;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
