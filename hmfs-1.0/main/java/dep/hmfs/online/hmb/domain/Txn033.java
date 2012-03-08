package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class Txn033 extends SubMsg{
    //F8：动作代码
    public String actionCode;

    //F16：信息ID1
    public String infoId1;

    //F17：信息ID1类型
    public String infoIdType1;

    //F20：信息编码
    public String infoCode;

    //F21：信息名称
    public String infoName;

    //F22：信息地址
    public String infoAddr;

    //F23：分户数
    public String cellNum;

    //F24：建筑面积
    public String builderArea;

    //F25：归属区县ID
    public String districtId;

    //F28：核算户账号1。
    public String fundActno1;

    //F29：核算户账号1类型
    public String fundActtype1;

    //F30：核算户账号2。
    public String fundActno2;

    //F31：核算户账号2类型
    public String fundActtype2;

    //F71：开发建设单位名称    核算户为区县时，可以填写#
    public String devOrgName;

    //F76：房屋交存类型
    public String houseDepType;

    //F78：交存标准1
    public String depStandard1;

    //F83：交存标准2
    public String depStandard2;

    //F99：是否出售
    public String sellFlag;

    //F100：楼号
    public String buildingNo;

    //F101：门号
    public String unitNo;

    //F102：室号
    public String roomNo;

    //F104：证件类型
    public String certType;

    //F105：证件编号
    public String certId;

    //F64：单位联系电话
    public String orgPhone;

    //F82：购房合同号
    public String houseContNo;

    //F88：购房人联系电话
    public String houseCustPhone;

    //F93：有无电梯
    public String elevatorType;

    //F106：购房款总额
    public String houseTotalAmt;

    //F38：会计帐号
    public String cbsActno;
}
