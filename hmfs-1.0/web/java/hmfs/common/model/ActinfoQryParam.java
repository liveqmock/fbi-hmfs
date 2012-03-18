package hmfs.common.model;

public class ActinfoQryParam {
    private String cbsActno = "";
    private String startActno = "";
    private String endActno = "";
    private String actName = "";
    private String actnoStatus = "";
    private String startDate = "";
    private String endDate = "";

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
}
