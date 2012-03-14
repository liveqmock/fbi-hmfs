package hmfs.common.model;

public class ActinfoQryParam {
    private String startActno = "";
    private String endActno = "";
    private String actnoStatus = "";

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
}
