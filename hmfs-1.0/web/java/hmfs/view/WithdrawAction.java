package hmfs.view;

import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmSysCtl;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.system.manage.dao.PtOperBean;
import skyline.common.utils.MessageUtil;
import skyline.service.PlatformService;

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
 * 支取交易处理
 * zhanrui
 * 2012/3/24
 */
@ManagedBean
@ViewScoped
public class WithdrawAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawAction.class);

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
    private BigDecimal totalAmt;

    private boolean checkPassed = false;
    private boolean confirmed = false;

    @ManagedProperty(value = "#{appMngService}")
    private AppMngService appMngService;
    @ManagedProperty(value = "#{actInfoService}")
    private ActInfoService actInfoService;
    @ManagedProperty(value = "#{depService}")
    private DepService depService;
    @ManagedProperty(value = "#{platformService}")
    private PlatformService platformService;

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
    }

    //支取查询
    public String onQuery() {
        try {
            this.summaryMsg = actInfoService.selectSummaryMsg(msgSn);
            TxnCtlSts txnCtlSts = TxnCtlSts.valueOfAlias(this.summaryMsg.getTxnCtlSts());
            if (!txnCtlSts.equals(TxnCtlSts.INIT)) {
                this.checkPassed = false;
                logger.error("交易处理状态错误。" + this.summaryMsg.getTxnCtlSts());
                MessageUtil.addError("本笔申请单处理状态错误，当前状态为：" + txnCtlSts.getTitle());
                return null;
            }
            this.subMsgList = actInfoService.selectSubMsgList(msgSn);
            this.totalCount = this.subMsgList.size();

            //总分金额核对
            this.totalAmt = new BigDecimal(0.00);
            for (HmMsgIn hmMsgIn : this.subMsgList) {
                this.totalAmt = this.totalAmt.add(hmMsgIn.getTxnAmt1());
            }
            if (txnAmt.compareTo(this.totalAmt) != 0) {
                this.checkPassed = false;
                MessageUtil.addError("交易金额不符！此申请单明细金额合计为：" + this.totalAmt.toString());
            } else {
                //与界面输入的金额比对
                BigDecimal msgTxnAmt = this.summaryMsg.getTxnAmt1();
                if (msgTxnAmt.compareTo(this.txnAmt) != 0) {
                    this.checkPassed = false;
                    MessageUtil.addError("交易金额不符！此申请单金额为：" + msgTxnAmt.toString());
                } else {
                    this.checkPassed = true;
                }
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    //支取处理
    public String onConfirm() {
        try {
            PtOperBean oper = platformService.getOperatorManager().getOperator();
            String response = depService.process("1005310|" + this.msgSn + "|"
                    + oper.getDeptid() + "|" + oper.getOperid());
            if (response.startsWith("0000")) { //成功
                this.confirmed = true;
                MessageUtil.addInfo("支取交易处理成功。");
            } else {
                MessageUtil.addError("处理失败。" + response);
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    //=============================

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

    public PlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

    public boolean isCheckPassed() {
        return checkPassed;
    }

    public void setCheckPassed(boolean checkPassed) {
        this.checkPassed = checkPassed;
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

    public DepService getDepService() {
        return depService;
    }

    public void setDepService(DepService depService) {
        this.depService = depService;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
