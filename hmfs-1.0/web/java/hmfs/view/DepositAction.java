package hmfs.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmActFund;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmSysCtl;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.system.manage.dao.PtOperBean;
import skyline.common.utils.MessageUtil;
import skyline.service.PlatformService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 缴款交易处理
 * zhanrui
 * 2012/3/24
 */
@ManagedBean
@ViewScoped
public class DepositAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(DepositAction.class);

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

    private HmActFund actFund;
    private boolean nameIsEmpty;

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

    //缴款查询
    public String onQuery() {
        try {
            if (!checkMsgsn()) {
                return null;
            }
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
                this.subMsgList = null;
                MessageUtil.addError("交易金额不符！此申请单明细金额合计为：" + this.totalAmt.toString());
            } else {
                //与界面输入的金额比对
                BigDecimal msgTxnAmt = this.summaryMsg.getTxnAmt1();
                if (msgTxnAmt.compareTo(this.txnAmt) != 0) {
                    this.checkPassed = false;
                    this.subMsgList = null;
                    MessageUtil.addError("交易金额不符！此申请单金额为：" + msgTxnAmt.toString());
                } else {
                    this.checkPassed = true;
                }
                // 检查业主姓名是否为空
                // HM_MSG_IN中业主姓名为空，只需判断核算户表中的业主姓名是否为空即可。
                actFund = actInfoService.selectActFundByno(this.subMsgList.get(0).getFundActno1());
                if (actFund == null) {
                    this.checkPassed = false;
                    logger.error("本次查询核算户不存在。" + this.subMsgList.get(0).getFundActno1());
                    MessageUtil.addError("本次查询核算户不存在，核算户账号：" + this.subMsgList.get(0).getFundActno1());
                    return null;
                } else if (StringUtils.isEmpty(actFund.getInfoName()) || "#".equals(actFund.getInfoName())) {
                    this.nameIsEmpty = true;
                    logger.error("本次查询核算户名为空。" + actFund.getFundActno1());
                    MessageUtil.addError("该业主户名为空，请手动录入。");
                } else {
                    this.nameIsEmpty = false;
                }
            }
        } catch (Exception e) {
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    //缴款处理
    public String onConfirm() {
        try {
            // 如果填写业主姓名，则需先更新HM_ACT_FUND，HM_MSG_IN无需修改，保持报文原始状态
            if (this.nameIsEmpty) {
                logger.info("核算户" + actFund.getFundActno1() + "户名由空更新为：" + actFund.getInfoName());
                actFund.setRemark("户名由空更新为" + actFund.getInfoName() + new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
                actInfoService.updateHmActFund(actFund);
            }
            // 用户机构号和柜员号
            PtOperBean oper = platformService.getOperatorManager().getOperator();
            String response = depService.process("1005210|" + this.msgSn + "|"
                    + oper.getDeptid() + "|" + oper.getOperid());
            if (response.startsWith("0000")) { //成功
                this.confirmed = true;
                MessageUtil.addInfo("缴款交易处理成功。");
            } else {
                MessageUtil.addError("处理失败。" + response);
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onPrintAll() {

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        //通过Response把数据以Excel格式保存
        response.reset();
        response.setContentType("application/pdf;charset=UTF-8");
        try {
            response.addHeader("Content-Disposition", "attachment;filename=\""
                    + new String(("用户意见信息表" + ".pdf").getBytes("GBK"),
                    "ISO8859_1") + "\"");
//            response.setHeader("Content-Disposition", "inline; attachment");
            ServletOutputStream out = response.getOutputStream();
            writePdf(out);
            out.flush();
            out.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writePdf(OutputStream outputStream) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        document.addTitle("Test PDF");
        document.addSubject("Testing email PDF");
        document.addKeywords("iText, email");
        document.addAuthor("FBI");
        document.addCreator("FBI");

        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk("hello!"));
        document.add(paragraph);

        document.close();
    }

    public void searchFirstOneRecordForPrint(ActionEvent actionEvent) {
//        for (HmMsgIn hmMsgIn : this.subMsgList) {
//
//        }


        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("acctno", "123456");    //basic parameter
//        context.addCallbackParam("user", user);     //pojo as json

        //execute javascript oncomplete
        context.execute("PrimeFaces.info('Hello from the Backing Bean');");

        //update panel
//        context.update("form:panel");
        //scroll to panel
//        context.scrollTo("form:panel");


    }

    //检查申请单编号
    private boolean checkMsgsn() {
        if (msgSn.length() != 18) {
            MessageUtil.addError("申请单编号是18位的编码，请检查！");
            return false;
        } else {
            int intLength = msgSn.length();
            if (!"5210".equals(msgSn.substring(intLength - 6, intLength - 2))) {
                MessageUtil.addError("申请单编号不是缴款编码，请检查！");
                return false;
            }
        }
        return true;
    }
    //=============================


    public boolean isNameIsEmpty() {
        return nameIsEmpty;
    }

    public void setNameIsEmpty(boolean nameIsEmpty) {
        this.nameIsEmpty = nameIsEmpty;
    }

    public HmActFund getActFund() {
        return actFund;
    }

    public void setActFund(HmActFund actFund) {
        this.actFund = actFund;
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

    public PlatformService getPlatformService() {
        return platformService;
    }

    public void setPlatformService(PlatformService platformService) {
        this.platformService = platformService;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
