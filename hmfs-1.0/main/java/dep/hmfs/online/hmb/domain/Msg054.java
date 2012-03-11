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
@HmbMessage("054")
public class Msg054 extends SubMsg{
    //F8：动作代码
    @Hmb8583Field(8)
    public String actionCode;

    //F16：信息ID1
    @Hmb8583Field(16)
    public String infoId1;

    //F17：信息ID1类型
    @Hmb8583Field(17)
    public String infoIdType1;

    //F21：信息名称
    @Hmb8583Field(21)
    public String infoName;

    //F25：归属区县ID
    @Hmb8583Field(25)
    public String districtId;

    //F32：结算户账号1
    @Hmb8583Field(32)
    public String settleActno1;

    //F33：结算户账号1类型
    @Hmb8583Field(33)
    public String settleActtype1;

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

    //F59：单位ID
    @Hmb8583Field(59)
    public String orgId;

    //F60：单位类型  10：市局；11：区局；12：开发商；13：业委会；14：物业公司；15：审价单位；16：监理单位
    @Hmb8583Field(60)
    public String orgType;

    //F61：单位名称
    @Hmb8583Field(61)
    public String orgName;
}
