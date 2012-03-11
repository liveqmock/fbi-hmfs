package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
@HmbMessage("051")
public class Msg051 extends SubMsg{
    //F8：动作代码
    @Hmb8583Field(8)
    public String actionCode;

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

    //F37：账户余额
    @Hmb8583Field(37)
    public BigDecimal actBal;
}

