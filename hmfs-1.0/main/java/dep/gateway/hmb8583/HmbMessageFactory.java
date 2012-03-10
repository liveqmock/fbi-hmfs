package dep.gateway.hmb8583;

import dep.gateway.hmb8583.parse.FieldParseInfo;
import dep.gateway.hmb8583.parse.LllvarParseInfo;
import dep.gateway.hmb8583.parse.LlvarParseInfo;
import dep.gateway.hmb8583.parse.LvarParseInfo;
import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;
import dep.hmfs.common.convertor.HmbMsgConvertor;
import dep.hmfs.online.hmb.domain.HmbMsg;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * �����ֽӿڱ���CODEC����.
 * User: zhanrui
 * Date: 12-3-5
 * Time: ����4:36
 * To change this template use File | Settings | File Templates.
 */
public class HmbMessageFactory {
    private static final Logger logger = LoggerFactory.getLogger(HmbMsgConvertor.class);

    //8583������
    //1��LVAR   2:LLVAR  3:LLLVAR
    private final static int[] Hmb8583FieldTypes = {
            1, 2, 1, 1, 1, 2, 1, 1, 2, 1, 3, 1, 1, 1, 1, 2, 1, 2, 1, 2,     //ÿ��20����
            3, 3, 2, 2, 2, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2, 2, 1, 3,
            3, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 1,
            3, 3, 1, 2, 2, 2, 2, 2, 2, 1, 3, 1, 1, 1, 1, 1, 2, 3, 1, 3,
            2, 2, 3, 2, 3, 2, 3, 2, 1, 1, 2, 1, 1, 3, 2, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1
    };
    //private String encoding = System.getProperty("file.encoding");
    private String encoding = "GBK";
    private String packageName = "dep.hmfs.online.hmb.domain";

    //private Map<String, Map<Integer, Hmb8583Field>> parseMap = new HashMap<String, Map<Integer, Hmb8583Field>>();
    private Map<String, Map<Integer, Field>> parseMap = new HashMap<String, Map<Integer, Field>>();


    public HmbMessageFactory() {
        try {
            initParseMap();
        } catch (Exception e) {
            throw new RuntimeException("����ע��δ���壡");
        }
    }

    /**
     * �����±���
     */
    public IsoMessage newMessage(HmbMsg hmbMsg) throws IllegalAccessException {
        IsoMessage m = new IsoMessage();
        String msgCode = hmbMsg.msgType.substring(2);
        m.setMsgCode(msgCode);
        Map<Integer, Field> annotatedFields = parseMap.get(msgCode);

        for (Integer fieldno : annotatedFields.keySet()) {
            Field f = annotatedFields.get(fieldno);
            Class typeClass = f.getType();
            String fieldValue = "#";
            if (typeClass == String.class) {
                fieldValue = (String) f.get(hmbMsg);
            } else if (typeClass == int.class) {
                fieldValue = String.valueOf(f.get(hmbMsg));
            } else if (typeClass == BigDecimal.class) {
                fieldValue = f.get(hmbMsg).toString();
            } else {
                logger.error("����BEAN���ֶ����Ͳ�֧��!" + typeClass.getName());
                throw new RuntimeException("����BEAN���ֶ����Ͳ�֧��!");
            }
            int isotype = Hmb8583FieldTypes[fieldno - 1];
            IsoType isoType;
            switch (isotype) {
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
                    logger.error("�������Ͷ������");
                    throw new RuntimeException("�������Ͷ������");
            }
            m.setValue(fieldno, fieldValue, isoType, 0);
        }
        return m;
    }

    /**
     * ���
     * @param buf  (����7λ���ȵı���)
     */
    public Map<String, List<IsoMessage>> parseTxnMessageMap(byte[] buf)
            throws ParseException, UnsupportedEncodingException {
        Map<String, List<IsoMessage>> txnMessageMap = new HashMap<String, List<IsoMessage>>();
        // ��ȡ������
        String txnCode = new String(buf, 0, 4, encoding);
        byte[] subBuf = new byte[buf.length - 4];
        System.arraycopy(buf, 4, subBuf, 0, subBuf.length);
        txnMessageMap.put(txnCode, parseMessageList(subBuf));
        return txnMessageMap;
    }

    private List<IsoMessage> parseMessageList(byte[] buf)
            throws ParseException, UnsupportedEncodingException {
        List<IsoMessage> isoMessageList = new ArrayList<IsoMessage>();
        IsoMessage isoMessage = null;
        int pos = 0;
        do {
            isoMessage = parseMessage(buf, 16 + 3 + pos, 16 + pos);
            isoMessageList.add(isoMessage);
            pos += isoMessage.getLength();
            if(!isoMessage.isHasNext()){
                break;
            }
        } while (pos <= buf.length);

        return isoMessageList;
    }


    /**
     * ����BEANע�ⴴ����8583����
     */
    private IsoMessage parseMessage(byte[] buf, int msgCodePos, int dataFieldPos)
            throws ParseException, UnsupportedEncodingException {
        IsoMessage m = new IsoMessage();
        String msgCode = new String(buf, msgCodePos, 3, encoding);
        m.setMsgCode(msgCode);
        //����Bitmap ��128b��
        BitSet bs = new BitSet(128);
        int bitIndex = 0;
        for (int i = 0; i < 16; i++) {
            int bit = 128;
            for (int b = 0; b < 8; b++) {
                bs.set(bitIndex++, (buf[i] & bit) != 0);
                bit >>= 1;
            }
        }
        Map<Integer, Field> annotatedFields = parseMap.get(msgCode);
        if (annotatedFields == null) {
            String info = "�ӱ�����" + msgCode + "��Ϣδ���壡";
            logger.error(info);
            throw new RuntimeException(info);
        }
        // ���ת��ģ�����Ƿ��������������Ϣ
        for (int i = 0; i < bs.length(); i++) {
            if (bs.get(i)) {
                if (!annotatedFields.containsKey(i+1)) {
                    logger.error("ISO8583 MessageFactory �޷������� {}: û��������Ϣ", i + 1);
                    throw new RuntimeException("ISO8583 ��������Ϣ���ִ���");
                }
            }
        }
        //ת����ȡ������ֵ
        for (Integer fieldno : annotatedFields.keySet()) {
            int isotype = Hmb8583FieldTypes[fieldno - 1];
            FieldParseInfo fpi;
            switch (isotype) {
                case 1:
                    fpi = new LvarParseInfo();
                    break;
                case 2:
                    fpi = new LlvarParseInfo();
                    break;
                case 3:
                    fpi = new LllvarParseInfo();
                    break;
                default:
                    logger.error("�������Ͷ������");
                    throw new RuntimeException("�������Ͷ������");
            }
            IsoValue<?> val = fpi.parse(buf, dataFieldPos, null);
            m.setField(fieldno, val);
            dataFieldPos += val.toString().getBytes(fpi.getCharacterEncoding()).length;
            if (val.getType() == IsoType.LLVAR) {
                dataFieldPos += 2;
            } else if (val.getType() == IsoType.LLLVAR) {
                dataFieldPos += 3;
            } else if(val.getType() == IsoType.LVAR) {
                dataFieldPos++;
            }
        }
        //��������
        m.setLength(dataFieldPos);
        String nextflag = (String) m.getField(128).getValue();
        m.setHasNext("1".equals(nextflag));
        return m;
    }

    //===============================================================================

    public static void main(String[] args) {
        HmbMessageFactory factory = new HmbMessageFactory();
        factory.initParseMap();

    }

    private void initParseMap() {
        for (int i = 1; i <= 999; i++) {
            String sn = StringUtils.leftPad("" + i, 3, "0");
            //logger.info(sn);
            try {
                Class clazz = Class.forName(packageName + ".Msg" + sn);
                HmbMessage hmbMessage = (HmbMessage) clazz.getAnnotation(HmbMessage.class);
                if (hmbMessage != null) {
                    String msgCode = hmbMessage.value();
                    //Map<Integer, Hmb8583Field> dataFields = new HashMap<Integer, Hmb8583Field>();
                    Map<Integer, Field> annotatedFields = new HashMap<Integer, Field>();
                    initOneClassFileds(clazz, annotatedFields);
                    parseMap.put(msgCode, annotatedFields);
                }
            } catch (ClassNotFoundException e) {
                //skip
            }
        }
    }

    /**
     * �ݹ���Ҹ�������Ϣ
     */
    private void initOneClassFileds(Class clazz, Map<Integer, Field> annotatedFields) {
        for (Field field : clazz.getDeclaredFields()) {
            Hmb8583Field msgField = field.getAnnotation(Hmb8583Field.class);
            if (msgField != null) {
                //dataFields.put(msgField.value(), msgField);
                annotatedFields.put(msgField.value(), field);
            }
            //System.out.println(field.getName());
        }
        Class superclazz = clazz.getSuperclass();
        if (superclazz != Object.class) {
            initOneClassFileds(superclazz, annotatedFields);
        }
    }

    public  void print(IsoMessage m) {
        System.out.printf("MSGCODE: %s\n", m.getMsgCode());
        for (int i = 1; i <= 128; i++) {
            if (m.hasField(i)) {
                System.out.printf("F%3d(%s): %s -> '%s'\n", i, m.getField(i).getType(),
                        m.getObjectValue(i), m.getField(i).toString());
            }
        }
    }
}
