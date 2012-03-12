package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: ����6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("033")
public class Msg033 extends SubMsg{
    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F20����Ϣ����
    @Hmb8583Field(20)
    public String infoCode;

    //F21����Ϣ����
    @Hmb8583Field(21)
    public String infoName;

    //F22����Ϣ��ַ
    @Hmb8583Field(22)
    public String infoAddr;

    //F23���ֻ���
    @Hmb8583Field(23)
    public int cellNum;

    //F24���������
    @Hmb8583Field(24)
    public BigDecimal builderArea;

    //F25����������ID
    @Hmb8583Field(25)
    public String districtId;

    //F28�����㻧�˺�1��
    @Hmb8583Field(28)
    public String fundActno1;

    //F29�����㻧�˺�1����
    @Hmb8583Field(29)
    public String fundActtype1;

    //F30�����㻧�˺�2��
    @Hmb8583Field(30)
    public String fundActno2;

    //F31�����㻧�˺�2����
    @Hmb8583Field(31)
    public String fundActtype2;

    //F71���������赥λ����    ���㻧Ϊ����ʱ��������д#
    @Hmb8583Field(71)
    public String devOrgName;

    //F76�����ݽ�������
    @Hmb8583Field(76)
    public String houseDepType;

    //F78�������׼1
    @Hmb8583Field(78)
    public String depStandard1;

    //F83�������׼2
    @Hmb8583Field(83)
    public String depStandard2;

    //F99���Ƿ����
    @Hmb8583Field(99)
    public String sellFlag;

    //F100��¥��
    @Hmb8583Field(100)
    public String buildingNo;

    //F101���ź�
    @Hmb8583Field(101)
    public String unitNo;

    //F102���Һ�
    @Hmb8583Field(102)
    public String roomNo;

    //F104��֤������
    @Hmb8583Field(104)
    public String certType;

    //F105��֤�����
    @Hmb8583Field(105)
    public String certId;

    //F64����λ��ϵ�绰
    @Hmb8583Field(64)
    public String orgPhone;

    //F82��������ͬ��
    @Hmb8583Field(82)
    public String houseContNo;

    //F88����������ϵ�绰
    @Hmb8583Field(88)
    public String houseCustPhone;

    //F93�����޵���
    @Hmb8583Field(93)
    public String elevatorType;

    //F106���������ܶ�
    @Hmb8583Field(106)
    public String houseTotalAmt;

    //F38������ʺ�
    @Hmb8583Field(38)
    public String cbsActno;

    public String getInfoId1() {
        return infoId1;
    }

    public void setInfoId1(String infoId1) {
        this.infoId1 = infoId1;
    }

    public String getInfoIdType1() {
        return infoIdType1;
    }

    public void setInfoIdType1(String infoIdType1) {
        this.infoIdType1 = infoIdType1;
    }

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        this.infoName = infoName;
    }

    public String getInfoAddr() {
        return infoAddr;
    }

    public void setInfoAddr(String infoAddr) {
        this.infoAddr = infoAddr;
    }

    public int getCellNum() {
        return cellNum;
    }

    public void setCellNum(int cellNum) {
        this.cellNum = cellNum;
    }

    public BigDecimal getBuilderArea() {
        return builderArea;
    }

    public void setBuilderArea(BigDecimal builderArea) {
        this.builderArea = builderArea;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getFundActno1() {
        return fundActno1;
    }

    public void setFundActno1(String fundActno1) {
        this.fundActno1 = fundActno1;
    }

    public String getFundActtype1() {
        return fundActtype1;
    }

    public void setFundActtype1(String fundActtype1) {
        this.fundActtype1 = fundActtype1;
    }

    public String getFundActno2() {
        return fundActno2;
    }

    public void setFundActno2(String fundActno2) {
        this.fundActno2 = fundActno2;
    }

    public String getFundActtype2() {
        return fundActtype2;
    }

    public void setFundActtype2(String fundActtype2) {
        this.fundActtype2 = fundActtype2;
    }

    public String getDevOrgName() {
        return devOrgName;
    }

    public void setDevOrgName(String devOrgName) {
        this.devOrgName = devOrgName;
    }

    public String getHouseDepType() {
        return houseDepType;
    }

    public void setHouseDepType(String houseDepType) {
        this.houseDepType = houseDepType;
    }

    public String getDepStandard1() {
        return depStandard1;
    }

    public void setDepStandard1(String depStandard1) {
        this.depStandard1 = depStandard1;
    }

    public String getDepStandard2() {
        return depStandard2;
    }

    public void setDepStandard2(String depStandard2) {
        this.depStandard2 = depStandard2;
    }

    public String getSellFlag() {
        return sellFlag;
    }

    public void setSellFlag(String sellFlag) {
        this.sellFlag = sellFlag;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getOrgPhone() {
        return orgPhone;
    }

    public void setOrgPhone(String orgPhone) {
        this.orgPhone = orgPhone;
    }

    public String getHouseContNo() {
        return houseContNo;
    }

    public void setHouseContNo(String houseContNo) {
        this.houseContNo = houseContNo;
    }

    public String getHouseCustPhone() {
        return houseCustPhone;
    }

    public void setHouseCustPhone(String houseCustPhone) {
        this.houseCustPhone = houseCustPhone;
    }

    public String getElevatorType() {
        return elevatorType;
    }

    public void setElevatorType(String elevatorType) {
        this.elevatorType = elevatorType;
    }

    public String getHouseTotalAmt() {
        return houseTotalAmt;
    }

    public void setHouseTotalAmt(String houseTotalAmt) {
        this.houseTotalAmt = houseTotalAmt;
    }

    public String getCbsActno() {
        return cbsActno;
    }

    public void setCbsActno(String cbsActno) {
        this.cbsActno = cbsActno;
    }
}
