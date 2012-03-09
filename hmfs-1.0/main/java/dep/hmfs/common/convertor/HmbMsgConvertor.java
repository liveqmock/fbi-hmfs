package dep.hmfs.common.convertor;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.gateway.hmb8583.IsoMessage;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.Txn001;
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
    private HmbMessageFactory dataFormat;
    public static void main(String[] args) throws Exception {
        HmbMsgConvertor convertor = new HmbMsgConvertor();
        Map paramMap = new HashMap();
        paramMap.put("MSG_TYPE", "5110");
        Txn001 txn = new Txn001();
        txn.msgType="00001";
        txn.msgSn = "1212121212";
        txn.bizType = "9";
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();
        hmbMsgList.add(txn);
        paramMap.put("TXN_MSGS", hmbMsgList);
        convertor.marshal(paramMap);
    }

    public Map marshal(Map paramMap){
        String msgType = (String) paramMap.get("MSG_TYPE");
        if (msgType == null) {
            throw new IllegalArgumentException("交易码未定义！");
        }
        List<HmbMsg> hmbMsgList = (List<HmbMsg>) paramMap.get("TXN_MSGS");
        if (hmbMsgList == null) {
            throw new IllegalArgumentException("交易数据不存在！");
        }
        String subTxnCode = "";
        IsoMessage message;
        List<IsoMessage>  messageList = new ArrayList<IsoMessage>();
        try {
            for (HmbMsg hmbMsg : hmbMsgList) {
                String newSubTxnCode = hmbMsg.msgType.substring(2);
                if (!newSubTxnCode.equals(subTxnCode)) {
                    dataFormat = new HmbMessageFactory(hmbMsg);
                    subTxnCode = newSubTxnCode;
                }
                message = dataFormat.newMessage(hmbMsg);
                messageList.add(message);
            }
            FileOutputStream fout;
            for (IsoMessage isoMessage : messageList) {
                print(isoMessage);
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

    public static void print(IsoMessage m) {
        System.out.printf("TYPE: %04x\n", m.getType());
        for (int i = 1; i <= 128; i++) {
            if (m.hasField(i)) {
                System.out.printf("F %3d(%s): %s -> '%s'\n", i, m.getField(i).getType(),
                        m.getObjectValue(i), m.getField(i).toString());
            }
        }
    }
}
