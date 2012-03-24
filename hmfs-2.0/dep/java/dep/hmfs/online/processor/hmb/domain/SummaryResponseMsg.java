package dep.hmfs.online.processor.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午7:05
 * To change this template use File | Settings | File Templates.
 */
public class SummaryResponseMsg extends SummaryMsg{
    //F9：和新交易相关的原始交易的报文编号，如抹账、冲正类交易被抹账、冲正交易的报文编号
    @Hmb8583Field(9)
    public String origMsgSn = "#";

    //F10：用于标识报文处理的结果，成功为"00"（可根据现有报文错误进行），对通讯层错误，由双方约定错误码；如果为业务处理错误，则定义统一错误码“99”
    @Hmb8583Field(10)
    public String rtnInfoCode = "#";

    //F11：用于说明报文处理代码的对应错误信息，如果报文处理代码为“99”,则表示业务处理错误信息的详细描述。
    @Hmb8583Field(11)
    public String rtnInfo = "#";

    public String getOrigMsgSn() {
        return origMsgSn;
    }

    public void setOrigMsgSn(String origMsgSn) {
        this.origMsgSn = origMsgSn;
    }

    public String getRtnInfoCode() {
        return rtnInfoCode;
    }

    public void setRtnInfoCode(String rtnInfoCode) {
        this.rtnInfoCode = rtnInfoCode;
    }

    public String getRtnInfo() {
        return rtnInfo;
    }

    public void setRtnInfo(String rtnInfo) {
        this.rtnInfo = rtnInfo;
    }
}
