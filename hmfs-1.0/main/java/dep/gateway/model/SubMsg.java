package dep.gateway.model;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class SubMsg extends HmbMsg{
    //F1：2位报文种类（00-汇总;01-子报文）+ 3位报文序号
    public String msgType;

    //F8：动作代码
    public String actionCode;
}
