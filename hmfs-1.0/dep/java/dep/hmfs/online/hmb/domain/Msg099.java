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
@HmbMessage("099")
public class Msg099 extends SubMsg{
    //F9：和新交易相关的原始交易的报文编号，如抹账、冲正类交易被抹账、冲正交易的报文编号
    @Hmb8583Field(9)
    public String origMsgSn;
}

