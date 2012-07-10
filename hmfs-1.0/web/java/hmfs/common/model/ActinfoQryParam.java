package hmfs.common.model;

import java.math.BigDecimal;

public class ActinfoQryParam {
    private String cbsActno = "";
    private String startActno = "";
    private String endActno = "";
    private String actName = "";
    private String actnoStatus = "";
    private String startDate = "";
    private String endDate = "";
    private BigDecimal txnAmt = new BigDecimal(0.00);
    private String fundActType = "";  //511:项目户 570：分户
    private String msgSn = "";        //申请单号
    private String cbsTxnSn = "";     //主机流水号


    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }

    public String getStartActno() {
        return startActno;
    }

    public void setStartActno(String startActno) {
        this.startActno = startActno;
    }

    public String getEndActno() {
        return endActno;
    }

    public void setEndActno(String endActno) {
        this.endActno = endActno;
    }

    public String getActnoStatus() {
        return actnoStatus;
    }

    public void setActnoStatus(String actnoStatus) {
        this.actnoStatus = actnoStatus;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getFundActType() {
        return fundActType;
    }

    public void setFundActType(String fundActType) {
        this.fundActType = fundActType;
    }

    public String getMsgSn() {
        return msgSn;
    }

    public void setMsgSn(String msgSn) {
        this.msgSn = msgSn;
    }

    public String getCbsTxnSn() {
        return cbsTxnSn;
    }

    public void setCbsTxnSn(String cbsTxnSn) {
        this.cbsTxnSn = cbsTxnSn;
    }
}
