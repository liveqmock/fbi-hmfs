package dep.hmfs.online.hmb.domain;

import dep.hmfs.common.annotation.Hmb8583Field;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class SubMsg extends HmbMsg{
    //F8：动作代码
    @Hmb8583Field(8)
    public String actionCode = "#";

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }
}
