package dep.test.hmbclient;

import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.hmb.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-11
 * Time: ÏÂÎç12:55
 * To change this template use File | Settings | File Templates.
 */
public class HmbClient {
    private static final Logger logger = LoggerFactory.getLogger(HmbClient.class);
    private HmbMessageFactory mf = new HmbMessageFactory();
    public static void main(String[] args) throws Exception {
        HmbClient client = new HmbClient();
        client.testMarshal();
        client.testUnmarshal();
    }

    private void testMarshal() throws IOException {
        List<HmbMsg> hmbMsgList = new ArrayList<HmbMsg>();

        Msg001 txn = new Msg001();
        //txn.msgType="00001";
        txn.msgSn = "msgsn";
        txn.bizType = "9";
        hmbMsgList.add(txn);
        Msg002 txn002 = new Msg002();
        txn002.msgType="00001";
        txn002.msgSn = "msgsn2";
        hmbMsgList.add(txn002);

        Msg031 txn031 = new Msg031();
        //txn002.msgType="01002";
        txn031.actionCode = "100";
        hmbMsgList.add(txn031);

        String txnCode = "5110";
        byte[] txnBuf = mf.marshal(txnCode, hmbMsgList);
        FileOutputStream fout;
        fout = new FileOutputStream("d:/tmp/txn" + txnCode + ".bin");
        fout.write(txnBuf);
        fout.close();
    }
    private void testUnmarshal() throws IOException {
        String txnCode = "5110";
        FileInputStream fi = new FileInputStream("d:/tmp/txn" + txnCode + ".bin");
        byte[] txnBuf = new byte[fi.available()];
        fi.read(txnBuf);
        fi.close();

        byte[] buf = new byte[txnBuf.length - 7];
        System.arraycopy(txnBuf, 7,  buf, 0,buf.length);
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(buf);
        logger.info((String) rtnMap.keySet().toArray()[0]);
    }

    private void  processMsg004(){
        Msg004 msg004 = new Msg004();
    }
}
