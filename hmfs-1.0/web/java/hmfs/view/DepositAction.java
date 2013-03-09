package hmfs.view;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import common.enums.FundActnoStatus;
import common.enums.SysCtlSts;
import common.enums.TxnCtlSts;
import common.repository.hmfs.model.HmMsgIn;
import common.repository.hmfs.model.HmSysCtl;
import hmfs.common.model.ActinfoQryParam;
import hmfs.service.ActInfoService;
import hmfs.service.AppMngService;
import hmfs.service.DepService;
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
 * �ɿ�״���
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
        this.sysDate = new SimpleDateFormat("yyyy��MM��dd��").format(new Date());
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

    //�ɿ��ѯ
    public String onQuery() {
        try {
            if (!checkMsgsn()){
                return null;
            }
            this.summaryMsg = actInfoService.selectSummaryMsg(msgSn);
            TxnCtlSts txnCtlSts = TxnCtlSts.valueOfAlias(this.summaryMsg.getTxnCtlSts());
            if (!txnCtlSts.equals(TxnCtlSts.INIT)) {
                this.checkPassed = false;
                logger.error("���״���״̬����" + this.summaryMsg.getTxnCtlSts());
                MessageUtil.addError("�������뵥����״̬���󣬵�ǰ״̬Ϊ��" + txnCtlSts.getTitle());
                return null;
            }
            this.subMsgList = actInfoService.selectSubMsgList(msgSn);
            this.totalCount = this.subMsgList.size();

            //�ֽܷ��˶�
            this.totalAmt = new BigDecimal(0.00);
            for (HmMsgIn hmMsgIn : this.subMsgList) {
                this.totalAmt = this.totalAmt.add(hmMsgIn.getTxnAmt1());
            }
            if (txnAmt.compareTo(this.totalAmt) != 0) {
                this.checkPassed = false;
                this.subMsgList = null;
                MessageUtil.addError("���׽����������뵥��ϸ���ϼ�Ϊ��" + this.totalAmt.toString());
            } else {
                //���������Ľ��ȶ�
                BigDecimal msgTxnAmt = this.summaryMsg.getTxnAmt1();
                if (msgTxnAmt.compareTo(this.txnAmt) != 0) {
                    this.checkPassed = false;
                    this.subMsgList = null;
                    MessageUtil.addError("���׽����������뵥���Ϊ��" + msgTxnAmt.toString());
                } else {
                    this.checkPassed = true;
                }
            }
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
        }
        return null;
    }

    //�ɿ��
    public String onConfirm() {
        try {
            // �û������ź͹�Ա��
            PtOperBean oper = platformService.getOperatorManager().getOperator();
            String response = depService.process("1005210|" + this.msgSn + "|"
                    + oper.getDeptid() + "|" + oper.getOperid());
            if (response.startsWith("0000")) { //�ɹ�
                this.confirmed = true;
                MessageUtil.addInfo("�ɿ�״���ɹ���");
            } else {
                MessageUtil.addError("����ʧ�ܡ�" + response);
            }
        } catch (Exception e) {
            logger.error("����ʧ�ܡ�", e);
            MessageUtil.addError("����ʧ�ܡ�" + e.getMessage());
        }
        return null;
    }

    public String onPrintAll() {

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        //ͨ��Response��������Excel��ʽ����
        response.reset();
        response.setContentType("application/pdf;charset=UTF-8");
        try {
            response.addHeader("Content-Disposition", "attachment;filename=\""
                    + new String(("�û������Ϣ��" + ".pdf").getBytes("GBK"),
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

    //������뵥���
    private boolean checkMsgsn(){
        if(msgSn.length()!=18){
            MessageUtil.addError("���뵥�����18λ�ı��룬���飡");
            return false;
        }else{
            int intLength = msgSn.length();
            if(!"5210".equals(msgSn.substring(intLength-6,intLength-2))){
                MessageUtil.addError("���뵥��Ų��ǽɿ���룬���飡");
                return false;
            }
        }
        return true;
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
