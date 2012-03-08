package common.repository.hmfs.model;

import java.math.BigDecimal;

public class TxnFundLog {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.PKID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String pkid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_SN
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnSn;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_SUB_SN
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnSubSn;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_DATE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_TIME
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.FUND_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String fundActno;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_CODE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.ACTION_CODE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String actionCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.DC_FLAG
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String dcFlag;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXNLOG_STS
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnlogSts;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnActno;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_SUPER_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txnSuperActno;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.OPAC_BRID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String opacBrid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXAC_BRID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String txacBrid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.TXN_AMT
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private BigDecimal txnAmt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.MESG_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String mesgNo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.OPR1_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String opr1No;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.OPR2_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String opr2No;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_FUND_LOG.REMARK
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    private String remark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.PKID
     *
     * @return the value of HMFS.TXN_FUND_LOG.PKID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getPkid() {
        return pkid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.PKID
     *
     * @param pkid the value for HMFS.TXN_FUND_LOG.PKID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setPkid(String pkid) {
        this.pkid = pkid == null ? null : pkid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_SN
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_SN
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnSn() {
        return txnSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_SN
     *
     * @param txnSn the value for HMFS.TXN_FUND_LOG.TXN_SN
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnSn(String txnSn) {
        this.txnSn = txnSn == null ? null : txnSn.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_SUB_SN
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_SUB_SN
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnSubSn() {
        return txnSubSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_SUB_SN
     *
     * @param txnSubSn the value for HMFS.TXN_FUND_LOG.TXN_SUB_SN
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnSubSn(String txnSubSn) {
        this.txnSubSn = txnSubSn == null ? null : txnSubSn.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_DATE
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_DATE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnDate() {
        return txnDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_DATE
     *
     * @param txnDate the value for HMFS.TXN_FUND_LOG.TXN_DATE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate == null ? null : txnDate.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_TIME
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_TIME
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnTime() {
        return txnTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_TIME
     *
     * @param txnTime the value for HMFS.TXN_FUND_LOG.TXN_TIME
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime == null ? null : txnTime.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.FUND_ACTNO
     *
     * @return the value of HMFS.TXN_FUND_LOG.FUND_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getFundActno() {
        return fundActno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.FUND_ACTNO
     *
     * @param fundActno the value for HMFS.TXN_FUND_LOG.FUND_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setFundActno(String fundActno) {
        this.fundActno = fundActno == null ? null : fundActno.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_CODE
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_CODE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnCode() {
        return txnCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_CODE
     *
     * @param txnCode the value for HMFS.TXN_FUND_LOG.TXN_CODE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode == null ? null : txnCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.ACTION_CODE
     *
     * @return the value of HMFS.TXN_FUND_LOG.ACTION_CODE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getActionCode() {
        return actionCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.ACTION_CODE
     *
     * @param actionCode the value for HMFS.TXN_FUND_LOG.ACTION_CODE
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setActionCode(String actionCode) {
        this.actionCode = actionCode == null ? null : actionCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.DC_FLAG
     *
     * @return the value of HMFS.TXN_FUND_LOG.DC_FLAG
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getDcFlag() {
        return dcFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.DC_FLAG
     *
     * @param dcFlag the value for HMFS.TXN_FUND_LOG.DC_FLAG
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setDcFlag(String dcFlag) {
        this.dcFlag = dcFlag == null ? null : dcFlag.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXNLOG_STS
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXNLOG_STS
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnlogSts() {
        return txnlogSts;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXNLOG_STS
     *
     * @param txnlogSts the value for HMFS.TXN_FUND_LOG.TXNLOG_STS
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnlogSts(String txnlogSts) {
        this.txnlogSts = txnlogSts == null ? null : txnlogSts.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_ACTNO
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnActno() {
        return txnActno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_ACTNO
     *
     * @param txnActno the value for HMFS.TXN_FUND_LOG.TXN_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnActno(String txnActno) {
        this.txnActno = txnActno == null ? null : txnActno.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_SUPER_ACTNO
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_SUPER_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxnSuperActno() {
        return txnSuperActno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_SUPER_ACTNO
     *
     * @param txnSuperActno the value for HMFS.TXN_FUND_LOG.TXN_SUPER_ACTNO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnSuperActno(String txnSuperActno) {
        this.txnSuperActno = txnSuperActno == null ? null : txnSuperActno.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.OPAC_BRID
     *
     * @return the value of HMFS.TXN_FUND_LOG.OPAC_BRID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getOpacBrid() {
        return opacBrid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.OPAC_BRID
     *
     * @param opacBrid the value for HMFS.TXN_FUND_LOG.OPAC_BRID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setOpacBrid(String opacBrid) {
        this.opacBrid = opacBrid == null ? null : opacBrid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXAC_BRID
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXAC_BRID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getTxacBrid() {
        return txacBrid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXAC_BRID
     *
     * @param txacBrid the value for HMFS.TXN_FUND_LOG.TXAC_BRID
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxacBrid(String txacBrid) {
        this.txacBrid = txacBrid == null ? null : txacBrid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.TXN_AMT
     *
     * @return the value of HMFS.TXN_FUND_LOG.TXN_AMT
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.TXN_AMT
     *
     * @param txnAmt the value for HMFS.TXN_FUND_LOG.TXN_AMT
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.MESG_NO
     *
     * @return the value of HMFS.TXN_FUND_LOG.MESG_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getMesgNo() {
        return mesgNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.MESG_NO
     *
     * @param mesgNo the value for HMFS.TXN_FUND_LOG.MESG_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setMesgNo(String mesgNo) {
        this.mesgNo = mesgNo == null ? null : mesgNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.OPR1_NO
     *
     * @return the value of HMFS.TXN_FUND_LOG.OPR1_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getOpr1No() {
        return opr1No;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.OPR1_NO
     *
     * @param opr1No the value for HMFS.TXN_FUND_LOG.OPR1_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setOpr1No(String opr1No) {
        this.opr1No = opr1No == null ? null : opr1No.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.OPR2_NO
     *
     * @return the value of HMFS.TXN_FUND_LOG.OPR2_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getOpr2No() {
        return opr2No;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.OPR2_NO
     *
     * @param opr2No the value for HMFS.TXN_FUND_LOG.OPR2_NO
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setOpr2No(String opr2No) {
        this.opr2No = opr2No == null ? null : opr2No.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_FUND_LOG.REMARK
     *
     * @return the value of HMFS.TXN_FUND_LOG.REMARK
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_FUND_LOG.REMARK
     *
     * @param remark the value for HMFS.TXN_FUND_LOG.REMARK
     *
     * @mbggenerated Thu Mar 08 11:26:04 CST 2012
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}