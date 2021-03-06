package dep.hmfs.online.processor.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:48
 * To change this template use File | Settings | File Templates.
 */
public class HmbMsg {
    //F1：2位报文种类（00-汇总;01-子报文）+ 3位报文序号
    @Hmb8583Field(1)
    public String msgType;

    //F128：后续报文标志 0－表示没有后续子报文；1－表示还有后续子报文；
    //@Hmb8583Field(128)
    //public String msgNextFlag = "0";


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}
