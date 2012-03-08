package dep.hmfs.online.cmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 12-3-7
 * Time: ����7:27
 * ҵ������|���׽��|סլ��ַ|סլ�������|�绰|סլ����|�������|�ɴ����|ҵ���ʽ��˺�
 */
public class PayDetail {

    private String accountName;
    private String txAmt;
    private String address;
    private String houseArea;
    private String phoneNo;
    private String houseType;
    private String projAmt;
    private String payPart;
    private String accountNo;

    public String toStringByDelimiter(String delimiter) {
        if (delimiter == null) {
            delimiter = "|";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(accountName).append("|");
        stringBuilder.append(txAmt).append("|");
        stringBuilder.append(address).append("|");
        stringBuilder.append(houseArea).append("|");
        stringBuilder.append(phoneNo).append("|");
        stringBuilder.append(houseType).append("|");
        stringBuilder.append(projAmt).append("|");
        stringBuilder.append(payPart).append("|");
        stringBuilder.append(accountNo);
        return stringBuilder.toString();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHouseArea() {
        return houseArea;
    }

    public void setHouseArea(String houseArea) {
        this.houseArea = houseArea;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public String getPayPart() {
        return payPart;
    }

    public void setPayPart(String payPart) {
        this.payPart = payPart;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProjAmt() {
        return projAmt;
    }

    public void setProjAmt(String projAmt) {
        this.projAmt = projAmt;
    }

    public String getTxAmt() {
        return txAmt;
    }

    public void setTxAmt(String txAmt) {
        this.txAmt = txAmt;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
