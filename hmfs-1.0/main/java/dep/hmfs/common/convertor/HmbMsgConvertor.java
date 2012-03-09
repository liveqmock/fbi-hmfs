package dep.hmfs.common.convertor;

import dep.hmfs.online.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private HmbMsgDataFormat dataFormat;
/*
    //8583域类型
    //1：LVAR   2:LLVAR  3:LLLVAR
    private final static int[]  Hmb8583Types = {
            1,2,1,1,1, 2,1,1,2,1, 3,1,1,1,1, 2,1,2,1,2,
            3,3,2,2,2, 1,1,2,1,2, 1,2,1,2,1, 1,2,2,1,3,
            3,2,1,1,2, 2,2,2,2,2, 2,2,1,2,1, 2,2,1,2,1,
            3,3,1,2,2, 2,2,2,2,1, 3,1,1,1,1, 1,2,3,1,3,
            2,2,3,2,3, 2,3,2,1,1, 2,1,1,3,2, 1,1,1,1,1,
            1,1,1,1,2, 2,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1,
            1,1,1,1,1,1,1,1
    };
    private Map<Integer, Hmb8583Field> msgFields = new ConcurrentHashMap<Integer, Hmb8583Field>();
    private Map<Integer, Field> annotatedFields = new ConcurrentHashMap<Integer, Field>();
*/
    
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
        for (HmbMsg hmbMsg : hmbMsgList) {
            String newSubTxnCode = hmbMsg.msgType.substring(2);
            if (!newSubTxnCode.equals(subTxnCode)) {
                  dataFormat = new HmbMsgDataFormat(newSubTxnCode);
            }
//            dataFormat.
        }
      return null;
    }
/*

    private void marshalOneMsg(HmbMsg txnmsg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class clazz = txnmsg.getClass();
        HmbMessage hmbMessage = (HmbMessage) clazz.getAnnotation(HmbMessage.class);
        if (hmbMessage != null) {
            System.out.println("txncode:"+hmbMessage.value());
        }else{
            throw new RuntimeException("报文注解未定义！");
        }

        getAllFields(clazz);

        for (Integer i : msgFields.keySet()) {
            Hmb8583Field msgField = msgFields.get(i);
            System.out.println(msgField.value());
        }
        for (Integer i : annotatedFields.keySet()) {
            Field f = annotatedFields.get(i);
            System.out.println("" + i + f.getType());
            Class typeClass = f.getType();
            //Constructor con = typeClass.getConstructor(typeClass);
            //Object o = con.newInstance("20");
            if (typeClass == String.class) {
                f.set(txnmsg, "20");
            }else if (typeClass == int.class) {
                f.set(txnmsg, 200);
            }
        }
    }

    private void getAllFields(Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Hmb8583Field msgField = field.getAnnotation(Hmb8583Field.class);
            if (msgField != null) {
                msgFields.put(msgField.value(), msgField);
                annotatedFields.put(msgField.value(), field);
            }
            System.out.println(field.getName());
        }
        Class superclazz = clazz.getSuperclass();
        if (superclazz != Object.class) {
            getAllFields(superclazz);
        }
    }
*/

}
