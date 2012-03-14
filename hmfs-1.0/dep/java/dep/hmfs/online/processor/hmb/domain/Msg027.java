package dep.hmfs.online.processor.hmb.domain;

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
@HmbMessage("027")
public class Msg027 extends SubMsg{
    //F16����ϢID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17����ϢID1����
    @Hmb8583Field(17)
    public String infoIdType1;

    //F18����ϢID2
    @Hmb8583Field(18)
    public String infoId2;

    //F19����ϢID2����
    @Hmb8583Field(19)
    public String infoIdType2;

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

    //F71���������赥λ����    ���㻧Ϊ����ʱ��������д#
    @Hmb8583Field(71)
    public String devOrgName;

    //=====================

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

    public String getInfoId2() {
        return infoId2;
    }

    public void setInfoId2(String infoId2) {
        this.infoId2 = infoId2;
    }

    public String getInfoIdType2() {
        return infoIdType2;
    }

    public void setInfoIdType2(String infoIdType2) {
        this.infoIdType2 = infoIdType2;
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

    public String getDevOrgName() {
        return devOrgName;
    }

    public void setDevOrgName(String devOrgName) {
        this.devOrgName = devOrgName;
    }
}
