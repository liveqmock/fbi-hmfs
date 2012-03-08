package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
public class Txn100 extends HmbMsg{
    //F2：本笔报文的编号，唯一标识一笔报文，在整个报文的生命周期中，不会改变，规则如下：2位年（年的后两位）+2位月+2位日+6位序号+4位交易类型+2位发起地
    public String msgSn;

    //F4：发送报文方的系统编码
    public String sendSysId;

    //F5：标识报文发起方:本字段由房地局统一定义，如：维修资金系统为00
    public String origSysId;

    //F10：用于标识报文处理的结果，成功为"00"（可根据现有报文错误进行），对通讯层错误，由双方约定错误码；如果为业务处理错误，则定义统一错误码“99”
    public String rtnInfoCode;

    //F11：用于说明报文处理代码的对应错误信息，如果报文处理代码为“99”,则表示业务处理错误信息的详细描述。
    public String rtnInfo;

}
