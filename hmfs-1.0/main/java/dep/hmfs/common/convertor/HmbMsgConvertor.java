package dep.hmfs.common.convertor;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.Msg001;
import dep.hmfs.online.hmb.domain.Msg002;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 房产局接口报文CODEC处理.
 * User: zhanrui
 * Date: 12-3-9
 * Time: 下午4:36
 * To change this template use File | Settings | File Templates.
 */
public class HmbMsgConvertor {
    private static final Logger logger = LoggerFactory.getLogger(HmbMsgConvertor.class);
    private HmbMessageFactory dataFormat = new HmbMessageFactory();
    public static void main(String[] args) throws Exception {
        HmbMsgConvertor convertor = new HmbMsgConvertor();
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        Msg001 txn = new Msg001();
        txn.msgType="00001";
        txn.msgSn = "msgsn";
        txn.bizType = "9";
        hmbMsgList.add(txn);
        txn = new Msg001();
        txn.msgType="00001";
        txn.msgSn = "msgsn2";
        txn.bizType = "9";
        hmbMsgList.add(txn);

        Msg002 txn002 = new Msg002();
        txn002.msgType="01002";
        txn002.msgSn = "msgsn3";
        hmbMsgList.add(txn002);


        convertor.dataFormat.marshal("5110", hmbMsgList);
    }

}
