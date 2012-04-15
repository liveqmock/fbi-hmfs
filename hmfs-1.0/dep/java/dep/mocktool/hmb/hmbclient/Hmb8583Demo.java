package dep.mocktool.hmb.hmbclient;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HmMsgOutMapper;
import common.repository.hmfs.dao.TmpMsgOutMapper;
import common.repository.hmfs.model.HmMsgOut;
import common.repository.hmfs.model.TmpMsgOut;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.online.processor.hmb.domain.HmbMsg;
import dep.hmfs.online.processor.hmb.domain.Msg001;
import dep.hmfs.online.processor.hmb.domain.Msg002;
import dep.hmfs.online.processor.hmb.domain.Msg031;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-11
 * Time: ÏÂÎç12:55
 * To change this template use File | Settings | File Templates.
 */
public class Hmb8583Demo {
    private static final Logger logger = LoggerFactory.getLogger(Hmb8583Demo.class);
    private  ApplicationContext context;

    private HmbMessageFactory mf = new HmbMessageFactory();
    public static void main(String[] args) throws Exception {
        Hmb8583Demo client = new Hmb8583Demo();
        client.context = new ClassPathXmlApplicationContext("applicationContext.xml");

//        client.testMarshal();
//        client.testUnmarshal();

        client.processMsgIn("5120");
        //client.processMsgIn("5130");
        //client.processMsgIn("5140");
//        client.processMsgIn("5210");
        //client.processMsgIn("5230");
//        client.processMsgIn("5610");
//        client.processMsgIn("7000");
//        client.processMsgIn("7001");
//        client.processMsgIn("7003");

//        client.processMsgOut("5140");
//        client.processMsgOut("5210");
//        client.processMsgOut("5230");
//        client.processMsgOut("5610");
//        client.processMsgOut("7000");
//        client.processMsgOut("7001");
//        client.processMsgOut("7003");
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
        String txnCode = "5210";
//        FileInputStream fi = new FileInputStream("d:/tmp/txn" + txnCode + ".bin");
        FileInputStream fi = new FileInputStream("d:/tmp/recv" + txnCode + ".txt");

        byte[] txnBuf = new byte[fi.available()];
        fi.read(txnBuf);
        fi.close();

        byte[] buf = new byte[txnBuf.length - 7];
        System.arraycopy(txnBuf, 7,  buf, 0,buf.length);
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(buf);
        logger.info((String) rtnMap.keySet().toArray()[0]);
    }


    private  void processMsgIn(String txnCode) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        FileInputStream fi = new FileInputStream("d:/tmp/in" + txnCode + ".txt");

        byte[] txnBuf = new byte[fi.available()];
        fi.read(txnBuf);
        fi.close();

        byte[] buf = new byte[txnBuf.length - 7];
        System.arraycopy(txnBuf, 7,  buf, 0,buf.length);
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(buf);
        logger.info((String) rtnMap.keySet().toArray()[0]);

        //Spring
        TmpMsgOutMapper tmpMsgOutMapper = context.getBean(TmpMsgOutMapper.class);
        
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
            TmpMsgOut tmpMsgOut = new TmpMsgOut();
            BeanUtils.copyProperties(tmpMsgOut, hmbMsg);
//            PropertyUtils.copyProperties(msginLog, hmbMsg);
            String guid = UUID.randomUUID().toString();
            tmpMsgOut.setPkid(guid);
            tmpMsgOut.setTxnCode(txnCode);
            Date date = new Date();
            tmpMsgOut.setMsgProcDate(new SimpleDateFormat("yyyyMMdd").format(date));
            tmpMsgOut.setMsgProcTime(new SimpleDateFormat("HHmmss").format(date));

            index++;
            if (index == 1) {
                msgSn = tmpMsgOut.getMsgSn();
            } else {
                tmpMsgOut.setMsgSn(msgSn);
            }
            tmpMsgOut.setMsgSubSn(index);
            tmpMsgOut.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            tmpMsgOutMapper.insert(tmpMsgOut);
        }
    }
    private  void processMsgOut(String txnCode) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        FileInputStream fi = new FileInputStream("d:/tmp/out" + txnCode + ".txt");

        byte[] txnBuf = new byte[fi.available()];
        fi.read(txnBuf);
        fi.close();

        byte[] buf = new byte[txnBuf.length + 4];
        System.arraycopy(txnCode.getBytes(), 0,  buf, 0, 4);
        System.arraycopy(txnBuf, 0,  buf, 4 , txnBuf.length);
        Map<String, List<HmbMsg>> rtnMap = mf.unmarshal(buf);

        logger.info((String) rtnMap.keySet().toArray()[0]);

        //Spring
        HmMsgOutMapper msgLogMapper = context.getBean(HmMsgOutMapper.class);

        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
            HmMsgOut msgLog = new HmMsgOut();
            BeanUtils.copyProperties(msgLog, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msgLog.setPkid(guid);
            msgLog.setTxnCode(txnCode);
            Date date = new Date();
            msgLog.setMsgProcDate(new SimpleDateFormat("yyyyMMdd").format(date));
            msgLog.setMsgProcTime(new SimpleDateFormat("HHmmss").format(date));

            index++;
            if (index == 1) {
                msgSn = msgLog.getMsgSn();
            } else {
                msgLog.setMsgSn(msgSn);
            }
            msgLog.setMsgSubSn(index);
            msgLog.setTxnCtlSts(TxnCtlSts.INIT.getCode());

            msgLogMapper.insert(msgLog);
        }
    }

}
