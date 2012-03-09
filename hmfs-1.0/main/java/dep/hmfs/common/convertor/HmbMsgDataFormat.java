package dep.hmfs.common.convertor;

import dep.gateway.hmb8583.IsoMessage;
import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;
import dep.hmfs.online.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * �����ֽӿڱ���CODEC����.
 * User: zhanrui
 * Date: 12-3-9
 * Time: ����4:36
 * To change this template use File | Settings | File Templates.
 */
public class HmbMsgDataFormat {
    private static final Logger logger = LoggerFactory.getLogger(HmbMsgConvertor.class);

    //8583������
    //1��LVAR   2:LLVAR  3:LLLVAR
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

    //�����루��Ӧ�ӱ��ĺţ�
    HmbMsgDataFormat(String msgType){
        try {
//            HmbMsg hmbMsg =
            initFieldDefinitions(msgType);
        } catch (Exception e) {
            throw new RuntimeException("����ע��δ���壡");
        }
    }


    public IsoMessage marshal(HmbMsg hmbMsg){

         return null;
    }

    private void initFieldDefinitions(String msgType) {
//        Class clazz = txnmsg.getClass();
        Class clazz = null;
        try {
            clazz = Class.forName(msgType);
        } catch (ClassNotFoundException e) {
            logger.error("�˽�������δ���壡", e);
            throw new RuntimeException("�˽�������δ���壡", e);
        }
        HmbMessage hmbMessage = (HmbMessage) clazz.getAnnotation(HmbMessage.class);
        if (hmbMessage == null) {
            logger.error("����ע��δ���壡");
            throw new RuntimeException("����ע��δ���壡");
        }

        initOneClassFileds(clazz);

/*
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
*/
    }

    /**
     * �ݹ���Ҹ�������Ϣ
     * @param clazz
     */
    private void initOneClassFileds(Class clazz) {
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
            initOneClassFileds(superclazz);
        }
    }

}
