package dep.gateway.hmb8583;

import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;
import dep.hmfs.common.convertor.HmbMsgConvertor;
import dep.hmfs.online.hmb.domain.HmbMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 房产局接口报文CODEC处理.
 * User: zhanrui
 * Date: 12-3-9
 * Time: 下午4:36
 * To change this template use File | Settings | File Templates.
 */
public class HmbMessageFactory {
    private static final Logger logger = LoggerFactory.getLogger(HmbMsgConvertor.class);

    //8583域类型
    //1：LVAR   2:LLVAR  3:LLLVAR
    private final static int[]  Hmb8583FieldTypes = {
            1,2,1,1,1, 2,1,1,2,1, 3,1,1,1,1, 2,1,2,1,2,
            3,3,2,2,2, 1,1,2,1,2, 1,2,1,2,1, 1,2,2,1,3,
            3,2,1,1,2, 2,2,2,2,2, 2,2,1,2,1, 2,2,1,2,1,
            3,3,1,2,2, 2,2,2,2,1, 3,1,1,1,1, 1,2,3,1,3,
            2,2,3,2,3, 2,3,2,1,1, 2,1,1,3,2, 1,1,1,1,1,
            1,1,1,1,2, 2,1,1,1,1, 1,1,1,1,1, 1,1,1,1,1,
            1,1,1,1,1,1,1,1
    };
    private Map<Integer, Hmb8583Field> dataFields = new ConcurrentHashMap<Integer, Hmb8583Field>();
    private Map<Integer, Field> annotatedFields = new ConcurrentHashMap<Integer, Field>();
    private String encoding = System.getProperty("file.encoding");

    public  HmbMessageFactory(HmbMsg msg){
        try {
            initFieldDefinitions(msg);
        } catch (Exception e) {
            throw new RuntimeException("报文注解未定义！");
        }
    }

    public IsoMessage newMessage(HmbMsg hmbMsg) throws IllegalAccessException {
        String isoHeaders = "1234567";
        IsoMessage m = new IsoMessage(isoHeaders);
        m.setType(Integer.parseInt(hmbMsg.msgType.substring(2)));
        //m.setBinary(useBinary);
        // m.setForceSecondaryBitmap(forceb2);
        m.setForceSecondaryBitmap(true);
        m.setCharacterEncoding(encoding);

        for (Integer fieldno : annotatedFields.keySet()) {
            Field f = annotatedFields.get(fieldno);
            Class typeClass = f.getType();
            String fieldValue = "#";
            if (typeClass == String.class) {
                fieldValue = (String) f.get(hmbMsg);
            }else if (typeClass == int.class) {
                fieldValue =  String.valueOf(f.get(hmbMsg));
            }else if (typeClass == BigDecimal.class){
                fieldValue =  ((BigDecimal)f.get(hmbMsg)).toString();
            }else{
                logger.error("报文BEAN的字段类型不支持!" + typeClass.getName());
                throw new RuntimeException("报文BEAN的字段类型不支持!");
            }
            int pos = dataFields.get(fieldno).value();
            int isotype = Hmb8583FieldTypes[fieldno - 1];
            IsoType isoType;
            switch (isotype){
                case 1:
                    isoType = IsoType.LVAR;
                    break;
                case 2:    
                    isoType = IsoType.LLVAR;
                    break;
                case 3:    
                    isoType = IsoType.LLLVAR;
                    break;
                default:
                    throw new  RuntimeException("报文类型定义错误");
            }
            m.setValue(pos, fieldValue, isoType, 0);
        }
        return m;
    }

    private void initFieldDefinitions(HmbMsg msg) {
        Class clazz = msg.getClass();
        HmbMessage hmbMessage = (HmbMessage) clazz.getAnnotation(HmbMessage.class);
        if (hmbMessage == null) {
            logger.error("报文注解未定义！");
            throw new RuntimeException("报文注解未定义！");
        }
        initOneClassFileds(clazz);
    }

    /**
     * 递归查找父类域信息
     * @param clazz
     */
    private void initOneClassFileds(Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Hmb8583Field msgField = field.getAnnotation(Hmb8583Field.class);
            if (msgField != null) {
                dataFields.put(msgField.value(), msgField);
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
