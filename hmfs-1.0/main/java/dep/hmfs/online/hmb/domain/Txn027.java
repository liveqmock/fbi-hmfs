package dep.hmfs.online.hmb.domain;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-8
 * Time: 下午6:50
 * To change this template use File | Settings | File Templates.
 */
public class Txn027 extends SubMsg{
    //F8：动作代码
    public String actionCode;

    //F16：信息ID1
    public String infoId1;

    //F17：信息ID1类型
    public String infoIdType1;

    //F18：信息ID2
    public String infoId2;

    //F19：信息ID2类型
    public String infoIdType2;

    //F20：信息编码
    public String infoCode;

    //F21：信息名称
    public String infoName;

    //F22：信息地址
    public String infoAddr;

    //F23：分户数
    public String cellNum;

    //F24：建筑面积
    public String builderArea;

    //F25：归属区县ID
    public String districtId;

    //F71：开发建设单位名称    核算户为区县时，可以填写#
    public String devOrgName;
}
