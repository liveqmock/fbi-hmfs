package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("033")
public class Msg033 extends SubMsg{
    //F16：信息ID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17：信息ID1类型
    @Hmb8583Field(17)
    public String infoIdType1;

    //F20：信息编码
    @Hmb8583Field(20)
    public String infoCode;

    //F21：信息名称
    @Hmb8583Field(21)
    public String infoName;

    //F22：信息地址
    @Hmb8583Field(22)
    public String infoAddr;

    //F23：分户数
    @Hmb8583Field(23)
    public String cellNum;

    //F24：建筑面积
    @Hmb8583Field(24)
    public String builderArea;

    //F25：归属区县ID
    @Hmb8583Field(25)
    public String districtId;

    //F28：核算户账号1。
    @Hmb8583Field(28)
    public String fundActno1;

    //F29：核算户账号1类型
    @Hmb8583Field(29)
    public String fundActtype1;

    //F30：核算户账号2。
    @Hmb8583Field(30)
    public String fundActno2;

    //F31：核算户账号2类型
    @Hmb8583Field(31)
    public String fundActtype2;

    //F71：开发建设单位名称    核算户为区县时，可以填写#
    @Hmb8583Field(71)
    public String devOrgName;

    //F76：房屋交存类型
    @Hmb8583Field(76)
    public String houseDepType;

    //F78：交存标准1
    @Hmb8583Field(78)
    public String depStandard1;

    //F83：交存标准2
    @Hmb8583Field(83)
    public String depStandard2;

    //F99：是否出售
    @Hmb8583Field(99)
    public String sellFlag;

    //F100：楼号
    @Hmb8583Field(100)
    public String buildingNo;

    //F101：门号
    @Hmb8583Field(101)
    public String unitNo;

    //F102：室号
    @Hmb8583Field(102)
    public String roomNo;

    //F104：证件类型
    @Hmb8583Field(104)
    public String certType;

    //F105：证件编号
    @Hmb8583Field(105)
    public String certId;

    //F64：单位联系电话
    @Hmb8583Field(64)
    public String orgPhone;

    //F82：购房合同号
    @Hmb8583Field(82)
    public String houseContNo;

    //F88：购房人联系电话
    @Hmb8583Field(88)
    public String houseCustPhone;

    //F93：有无电梯
    @Hmb8583Field(93)
    public String elevatorType;

    //F106：购房款总额
    @Hmb8583Field(106)
    public String houseTotalAmt;

    //F38：会计帐号
    @Hmb8583Field(38)
    public String cbsActno;
}
