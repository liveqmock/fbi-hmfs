package dep.hmfs.online.processor.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("053")
public class Msg053 extends SubMsg{
    //F21：信息名称
    @Hmb8583Field(21)
    public String infoName;

    //F38：会计帐号
    @Hmb8583Field(38)
    public String cbsActno;

    //F39：会计帐号类型
    @Hmb8583Field(39)
    public String cbsActtype;

    //F40：会计帐户名称
    @Hmb8583Field(40)
    public String cbsActname;

    //F41：开户银行名称
    @Hmb8583Field(41)
    public String bankName;

    //F42：开户银行分支机构编号
    @Hmb8583Field(42)
    public String branchId;

    //F43：存款类型
    @Hmb8583Field(43)
    public String depositType;

    //F56：方案编号
    @Hmb8583Field(56)
    public String schemeNo;

    //F61：单位名称
    @Hmb8583Field(61)
    public String orgName;

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }

    public String getCbsActtype() {
        return cbsActtype;
    }

    public void setCbsActtype(String cbsActtype) {
        this.cbsActtype = cbsActtype;
    }

    public String getCbsActname() {
        return cbsActname;
    }

    public void setCbsActname(String cbsActname) {
        this.cbsActname = cbsActname;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getSchemeNo() {
        return schemeNo;
    }

    public void setSchemeNo(String schemeNo) {
        this.schemeNo = schemeNo;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
