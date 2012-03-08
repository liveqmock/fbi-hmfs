package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class SummaryMsg extends HmbMsg{
    //F2：本笔报文的编号，唯一标识一笔报文，在整个报文的生命周期中，不会改变，规则如下：2位年（年的后两位）+2位月+2位日+6位序号+4位交易类型+2位发起地
    public String msgSn;

    //F3：含有的子报文的数目(不包含汇总报文)
    public int submsgNum;

    //F4：发送报文方的系统编码
    public String sendSysId;

    //F5：标识报文发起方:本字段由房地局统一定义，如：维修资金系统为00
    public String origSysId;

    //F6：报文产生的时间：8位日期＋6位时间（格式为：YYYYMMDDHHMMSS）
    public String msgDt;

    //F7：报文的截止交易日期，如果截至日期为非工作日，则需要顺延到下一工作日
    public String msgEndDate;
}
