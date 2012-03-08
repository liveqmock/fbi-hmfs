package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class Txn032 extends SubMsg{
    //F38：会计帐号
    public String cbsActno;

    //F32：结算户账号1
    public String settleActno1;

    //F33：结算户账号1类型
    public String settleActtype1;

    //F39：会计帐号类型
    public String cbsActtype;

    //F40：会计帐户名称
    public String cbsActname;

    //F41：开户银行名称
    public String bankName;

    //F42：开户银行分支机构编号
    public String branchId;

    //F43：存款类型
    public String depositType;

    //F59：单位ID
    public String orgId;

    //F60：单位类型  10：市局；11：区局；12：开发商；13：业委会；14：物业公司；15：审价单位；16：监理单位
    public String orgType;

    //F61：单位名称
    public String orgName;
}
