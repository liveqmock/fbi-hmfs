package dep.gateway.model;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
public class Txn001 extends SummaryMsg{
    //F12:交易方式  1－批量；2－单笔
    public String txnType;

    //F13:业务种类  1－单位；2－业主；3－其他；4－抹账
    public String bizType;

    //F14:交易类型 发起方的交易类型码
    public String txnCode;
}
