package common.repository.hmfs.model;

import java.math.BigDecimal;

public class TxnCbsLog {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.PKID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String pkid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.TXN_SN
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String txnSn;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.TXN_DATE
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String txnDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.TXN_TIME
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String txnTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.FUND_ACTNO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String fundActno;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.TXN_CODE
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String txnCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.MESG_NO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String mesgNo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.OPAC_BRID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String opacBrid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.TXAC_BRID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String txacBrid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.CBS_ACCTNO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String cbsAcctno;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.TXN_AMT
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private BigDecimal txnAmt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column HMFS.TXN_CBS_LOG.DC_FLAG
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    private String dcFlag;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.PKID
     *
     * @return the value of HMFS.TXN_CBS_LOG.PKID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getPkid() {
        return pkid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.PKID
     *
     * @param pkid the value for HMFS.TXN_CBS_LOG.PKID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setPkid(String pkid) {
        this.pkid = pkid == null ? null : pkid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.TXN_SN
     *
     * @return the value of HMFS.TXN_CBS_LOG.TXN_SN
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getTxnSn() {
        return txnSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.TXN_SN
     *
     * @param txnSn the value for HMFS.TXN_CBS_LOG.TXN_SN
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setTxnSn(String txnSn) {
        this.txnSn = txnSn == null ? null : txnSn.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.TXN_DATE
     *
     * @return the value of HMFS.TXN_CBS_LOG.TXN_DATE
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getTxnDate() {
        return txnDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.TXN_DATE
     *
     * @param txnDate the value for HMFS.TXN_CBS_LOG.TXN_DATE
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate == null ? null : txnDate.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.TXN_TIME
     *
     * @return the value of HMFS.TXN_CBS_LOG.TXN_TIME
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getTxnTime() {
        return txnTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.TXN_TIME
     *
     * @param txnTime the value for HMFS.TXN_CBS_LOG.TXN_TIME
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime == null ? null : txnTime.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.FUND_ACTNO
     *
     * @return the value of HMFS.TXN_CBS_LOG.FUND_ACTNO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getFundActno() {
        return fundActno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.FUND_ACTNO
     *
     * @param fundActno the value for HMFS.TXN_CBS_LOG.FUND_ACTNO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setFundActno(String fundActno) {
        this.fundActno = fundActno == null ? null : fundActno.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.TXN_CODE
     *
     * @return the value of HMFS.TXN_CBS_LOG.TXN_CODE
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getTxnCode() {
        return txnCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.TXN_CODE
     *
     * @param txnCode the value for HMFS.TXN_CBS_LOG.TXN_CODE
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode == null ? null : txnCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.MESG_NO
     *
     * @return the value of HMFS.TXN_CBS_LOG.MESG_NO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getMesgNo() {
        return mesgNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.MESG_NO
     *
     * @param mesgNo the value for HMFS.TXN_CBS_LOG.MESG_NO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setMesgNo(String mesgNo) {
        this.mesgNo = mesgNo == null ? null : mesgNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.OPAC_BRID
     *
     * @return the value of HMFS.TXN_CBS_LOG.OPAC_BRID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getOpacBrid() {
        return opacBrid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.OPAC_BRID
     *
     * @param opacBrid the value for HMFS.TXN_CBS_LOG.OPAC_BRID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setOpacBrid(String opacBrid) {
        this.opacBrid = opacBrid == null ? null : opacBrid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.TXAC_BRID
     *
     * @return the value of HMFS.TXN_CBS_LOG.TXAC_BRID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getTxacBrid() {
        return txacBrid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.TXAC_BRID
     *
     * @param txacBrid the value for HMFS.TXN_CBS_LOG.TXAC_BRID
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setTxacBrid(String txacBrid) {
        this.txacBrid = txacBrid == null ? null : txacBrid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.CBS_ACCTNO
     *
     * @return the value of HMFS.TXN_CBS_LOG.CBS_ACCTNO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getCbsAcctno() {
        return cbsAcctno;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.CBS_ACCTNO
     *
     * @param cbsAcctno the value for HMFS.TXN_CBS_LOG.CBS_ACCTNO
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setCbsAcctno(String cbsAcctno) {
        this.cbsAcctno = cbsAcctno == null ? null : cbsAcctno.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.TXN_AMT
     *
     * @return the value of HMFS.TXN_CBS_LOG.TXN_AMT
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public BigDecimal getTxnAmt() {
        return txnAmt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.TXN_AMT
     *
     * @param txnAmt the value for HMFS.TXN_CBS_LOG.TXN_AMT
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setTxnAmt(BigDecimal txnAmt) {
        this.txnAmt = txnAmt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column HMFS.TXN_CBS_LOG.DC_FLAG
     *
     * @return the value of HMFS.TXN_CBS_LOG.DC_FLAG
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public String getDcFlag() {
        return dcFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column HMFS.TXN_CBS_LOG.DC_FLAG
     *
     * @param dcFlag the value for HMFS.TXN_CBS_LOG.DC_FLAG
     *
     * @mbggenerated Sun Mar 11 11:38:24 CST 2012
     */
    public void setDcFlag(String dcFlag) {
        this.dcFlag = dcFlag == null ? null : dcFlag.trim();
    }
}