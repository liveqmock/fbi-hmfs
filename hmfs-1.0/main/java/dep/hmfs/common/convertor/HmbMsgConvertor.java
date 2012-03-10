package dep.hmfs.common.convertor;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.hmb8583.IsoMessage;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.Msg001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map paramMap = new HashMap();
        paramMap.put("MSG_TYPE", "5110");
        Msg001 txn = new Msg001();
        txn.msgType="00001";
        txn.msgSn = "msgsn";
        txn.bizType = "9";
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(txn);
        paramMap.put("TXN_MSGS", hmbMsgList);
        convertor.marshal(paramMap);
    }

    public byte[] marshal(Map paramMap){
        String msgType = (String) paramMap.get("MSG_TYPE");
        if (msgType == null) {
            throw new IllegalArgumentException("交易码未定义！");
        }
        List<HmbMsg> hmbMsgList = (List<HmbMsg>) paramMap.get("TXN_MSGS");
        if (hmbMsgList == null) {
            throw new IllegalArgumentException("交易数据不存在！");
        }
//        String subTxnCode = "";
        IsoMessage message;
        List<IsoMessage>  messageList = new ArrayList<IsoMessage>();
        try {
            int  msgTotalNum = hmbMsgList.size();
            int  step = 0;
            for (HmbMsg hmbMsg : hmbMsgList) {
                step++;
                if (step == msgTotalNum) {
                    hmbMsg.msgNextFlag = "0";
                }else{
                    hmbMsg.msgNextFlag = "1";
                }
/*
                String newSubTxnCode = hmbMsg.msgType.substring(2);
                if (!newSubTxnCode.equals(subTxnCode)) {
                    dataFormat = new HmbMessageFactory();
                    subTxnCode = newSubTxnCode;
                }
*/
                message = dataFormat.newMessage(hmbMsg);
                messageList.add(message);
            }
            FileOutputStream fout;
            for (IsoMessage isoMessage : messageList) {
                dataFormat.print(isoMessage);
                fout = new FileOutputStream("d:/tmp/iso.bin");
                isoMessage.write(fout);
                fout.close();
            }
        } catch (Exception e) {
            //TODO
            logger.error("", e);
            throw new RuntimeException("", e);
        }

        return null;
    }
}
