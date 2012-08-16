package common.repository.hmfs.model.hmfs;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-7-24
 * Time: ÏÂÎç1:51
 * To change this template use File | Settings | File Templates.
 */
public class HmFundTxnVO {
    private  String pkid;
    private  String txndate;
    private  String txntime;
    private  String actno;
    private  String msgsn;
    private  String txnamt;
    private  String vchnum;
    private  String infoname;
    private  String infoaddr;
    private  String area;
    private  String dcflag;
    private  String revflag;
    private  String cbstxnsn;
    private String bankid;
    private String operid;


    public String getTxndate() {
        return txndate;
    }

    public void setTxndate(String txndate) {
        this.txndate = txndate;
    }

    public String getTxntime() {
        return txntime;
    }

    public void setTxntime(String txntime) {
        this.txntime = txntime;
    }

    public String getActno() {
        return actno;
    }

    public void setActno(String actno) {
        this.actno = actno;
    }

    public String getMsgsn() {
        return msgsn;
    }

    public void setMsgsn(String msgsn) {
        this.msgsn = msgsn;
    }

    public String getTxnamt() {
        return txnamt;
    }

    public void setTxnamt(String txnamt) {
        this.txnamt = txnamt;
    }

    public String getVchnum() {
        return vchnum;
    }

    public void setVchnum(String vchnum) {
        this.vchnum = vchnum;
    }

    public String getInfoname() {
        return infoname;
    }

    public void setInfoname(String infoname) {
        this.infoname = infoname;
    }

    public String getInfoaddr() {
        return infoaddr;
    }

    public void setInfoaddr(String infoaddr) {
        this.infoaddr = infoaddr;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDcflag() {
        return dcflag;
    }

    public void setDcflag(String dcflag) {
        this.dcflag = dcflag;
    }

    public String getRevflag() {
        return revflag;
    }

    public void setRevflag(String revflag) {
        this.revflag = revflag;
    }

    public String getCbstxnsn() {
        return cbstxnsn;
    }

    public void setCbstxnsn(String cbstxnsn) {
        this.cbstxnsn = cbstxnsn;
    }

    public String getPkid() {
        return pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

    public String getOperid() {
        return operid;
    }

    public void setOperid(String operid) {
        this.operid = operid;
    }
}
