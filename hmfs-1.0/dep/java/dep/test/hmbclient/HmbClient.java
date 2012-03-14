package dep.test.hmbclient;

import common.enums.TxnCtlSts;
import common.repository.hmfs.dao.HisMsginLogMapper;
import common.repository.hmfs.dao.HisMsgoutLogMapper;
import common.repository.hmfs.model.HisMsginLog;
import common.repository.hmfs.model.HisMsgoutLog;
import dep.gateway.hmb8583.HmbMessageFactory;
import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;
import dep.hmfs.online.processor.hmb.domain.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-3-11
 * Time: 下午12:55
 * To change this template use File | Settings | File Templates.
 */
public class HmbClient {
    private static final Logger logger = LoggerFactory.getLogger(HmbClient.class);
    private  ApplicationContext context;
    private String packageName = "dep.hmfs.online.hmb.domain";
    private Map<String, Map<Integer, Field>> parseMap = new HashMap<String, Map<Integer, Field>>();


    private HmbMessageFactory mf = new HmbMessageFactory();
    public static void main(String[] args) throws Exception {
        HmbClient client = new HmbClient();
        client.context = new ClassPathXmlApplicationContext("applicationContext.xml");

//        client.testMarshal();
//        client.testUnmarshal();

        client.processMsgIn("5140");
//        client.processMsgIn("5210");
//        client.processMsgIn("5230");
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

    private void  processMsg004(){
        Msg004 msg004 = new Msg004();
    }

    private void  generateMsgSn(){

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
        HisMsginLogMapper msginLogMapper = context.getBean(HisMsginLogMapper.class);
        
        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
            HisMsginLog msginLog = new HisMsginLog();
            BeanUtils.copyProperties(msginLog, hmbMsg);
//            PropertyUtils.copyProperties(msginLog, hmbMsg);
            String guid = UUID.randomUUID().toString();
            msginLog.setPkid(guid);
            msginLog.setTxnCode(txnCode);
            Date date = new Date();
            msginLog.setMsgProcDate(new SimpleDateFormat("yyyyMMdd").format(date));
            msginLog.setMsgProcTime(new SimpleDateFormat("HHmmss").format(date));

            index++;
            if (index == 1) {
                msgSn = msginLog.getMsgSn();
            } else {
                msginLog.setMsgSn(msgSn);
            }
            msginLog.setMsgSubSn(StringUtils.leftPad(""+index, 6, '0'));
            msginLog.setTxnCtlSts(TxnCtlSts.TXN_INIT.getCode());

            msginLogMapper.insert(msginLog);
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
        HisMsgoutLogMapper msgLogMapper = context.getBean(HisMsgoutLogMapper.class);

        int index = 0;
        String msgSn = "";
        for (HmbMsg hmbMsg : rtnMap.get(txnCode)) {
            HisMsgoutLog msgLog = new HisMsgoutLog();
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
            msgLog.setMsgSubSn(StringUtils.leftPad("" + index, 6, '0'));
            msgLog.setTxnCtlSts(TxnCtlSts.TXN_INIT.getCode());

            msgLogMapper.insert(msgLog);
        }
    }

    private void initParseMap() {
        for (int i = 1; i <= 999; i++) {
            String sn = StringUtils.leftPad("" + i, 3, "0");
            try {
                Class clazz = Class.forName(packageName + ".Msg" + sn);
                HmbMessage hmbMessage = (HmbMessage) clazz.getAnnotation(HmbMessage.class);
                if (hmbMessage != null) {
                    String msgCode = hmbMessage.value();
                    Map<Integer, Field> annotatedFields = new TreeMap<Integer, Field>();
                    initOneClassFileds(clazz, annotatedFields);
                    parseMap.put(msgCode, annotatedFields);
                }
            } catch (ClassNotFoundException e) {
                //skip
            }
        }
    }

    /**
     * 递归查找父类域信息
     */
    private void initOneClassFileds(Class clazz, Map<Integer, Field> annotatedFields) {
        for (Field field : clazz.getDeclaredFields()) {
            Hmb8583Field msgField = field.getAnnotation(Hmb8583Field.class);
            if (msgField != null) {
                annotatedFields.put(msgField.value(), field);
            }
        }
        Class superclazz = clazz.getSuperclass();
        if (superclazz != Object.class) {
            initOneClassFileds(superclazz, annotatedFields);
        }
    }

}
