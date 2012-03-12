package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("031")
public class Msg031 extends SubMsg{
    //F38������ʺ�
    @Hmb8583Field(38)
    public String cbsActno;

    //F39������ʺ�����
    @Hmb8583Field(39)
    public String cbsActtype;

    //F40������ʻ�����
    @Hmb8583Field(40)
    public String cbsActname;

    //F41��������������
    @Hmb8583Field(41)
    public String bankName;

    //F42���������з�֧�������
    @Hmb8583Field(42)
    public String branchId;

    //F43���������
    @Hmb8583Field(43)
    public String depositType;

    //F59����λID
    @Hmb8583Field(59)
    public String orgId;

    //F60����λ����  10���о֣�11�����֣�12�������̣�13��ҵί�᣻14����ҵ��˾��15����۵�λ��16������λ
    @Hmb8583Field(60)
    public String orgType;

    //F61����λ����
    @Hmb8583Field(61)
    public String orgName;
    //=====================

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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
