package dep.gateway.hmb8583;

import dep.gateway.hmb8583.parse.FieldParseInfo;
import dep.gateway.hmb8583.parse.LllvarParseInfo;
import dep.gateway.hmb8583.parse.LlvarParseInfo;
import dep.gateway.hmb8583.parse.LvarParseInfo;
import dep.hmfs.common.annotation.Hmb8583Field;
import dep.hmfs.common.annotation.HmbMessage;
import dep.hmfs.online.hmb.domain.HmbMsg;
import dep.hmfs.online.hmb.domain.SummaryMsg;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
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
@Component
public class HmbMessageFactory {
    private static final Logger logger = LoggerFactory.getLogger(HmbMessageFactory.class);

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
    //����bean����package
    private String packageName = "dep.hmfs.online.hmb.domain";
    //���Ĵ���Ԫ��Ϣ
    private Map<String, Map<Integer, Field>> parseMap = new HashMap<String, Map<Integer, Field>>();

    public HmbMessageFactory() {
        try {
            initParseMap();
        } catch (Exception e) {
            throw new RuntimeException("����ע��δ���壡");
        }
    }

    /**
     * �������ױ���
     * @param txnCode  4λ������
     * @param hmbMsgList  �ӱ���bean List
     * @return   ȫ���ӱ�����ɵ�buffer
     */
    public byte[] marshal(String txnCode, List<HmbMsg> hmbMsgList){
        if (txnCode == null) {
            throw new IllegalArgumentException("������δ���壡");
        }
        if (hmbMsgList == null) {
            throw new IllegalArgumentException("�������ݲ����ڣ�");
        }
        IsoMessage message;
        List<IsoMessage>  messageList = new ArrayList<IsoMessage>();
        byte[] txnBuf;
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
                message = newMessage(hmbMsg);
                messageList.add(message);
            }
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            for (IsoMessage isoMessage : messageList) {
                logger.info(isoMessage.toString());
                isoMessage.write(byteOut);
            }
            byte[] txnBodyBuf = byteOut.toByteArray();
            byte[] txnHeaderBuf = (StringUtils.leftPad(String.valueOf(txnBodyBuf.length), 7, "0") + txnCode).getBytes();

            txnBuf = new byte[txnBodyBuf.length + 7 + 4];
            System.arraycopy(txnHeaderBuf, 0, txnBuf, 0, txnHeaderBuf.length);
            System.arraycopy(txnBodyBuf, 0, txnBuf, 11, txnBodyBuf.length);
        } catch (Exception e) {
            logger.error("���ݴ�����ִ���!", e);
            throw new RuntimeException("���ݴ�����ִ���!", e);
        }
        return txnBuf;
    }

    /**
     * ���
     * @param buf  (����7λ���ȵı���)
     */
    public Map<String, List<HmbMsg>> unmarshal(byte[] buf) {
        Map<String, List<IsoMessage>> txnMsgMap = parseTxnBuf(buf);
        String txnCode = (String) txnMsgMap.keySet().toArray()[0];

        List<IsoMessage> isoMessageList = txnMsgMap.get(txnCode);
        List<HmbMsg> msgBeanList = new ArrayList<HmbMsg>();
        for (IsoMessage message : isoMessageList) {
            String msgCode = message.getMsgCode();
            try {
                Class<?> clazz = Class.forName(packageName + ".Msg" + msgCode);
                Object msgBean = clazz.newInstance();
                assembleMsgBean(msgBean, message);
                msgBeanList.add((HmbMsg)msgBean);
            } catch (Exception e) {
                logger.error("���ʱ���ִ���", e);
                throw new RuntimeException("���ʱ���ִ���", e);
            }
        }
        Map<String, List<HmbMsg>> rtnMap = new HashMap<String, List<HmbMsg>>();
        rtnMap.put(txnCode, msgBeanList);
        return rtnMap;
    }

    public Map<String, List<IsoMessage>> parseTxnBuf(byte[] buf) {
        Map<String, List<IsoMessage>> txnMessageMap = new HashMap<String, List<IsoMessage>>();
        // ��ȡ������
        try {
            String txnCode = new String(buf, 0, 4, encoding);
            byte[] subBuf = new byte[buf.length - 4];
            System.arraycopy(buf, 4, subBuf, 0, subBuf.length);
            txnMessageMap.put(txnCode, parseMessageList(subBuf));
        } catch (Exception e) {
            logger.error("���ʱ���ִ���", e);
            throw new RuntimeException("���ʱ���ִ���", e);
        }
        return txnMessageMap;
    }

    /**
     * ����������IsoMessage
     */
    private IsoMessage newMessage(HmbMsg hmbMsg) throws IllegalAccessException {
        HmbMessage hmbMessage = (HmbMessage) hmbMsg.getClass().getAnnotation(HmbMessage.class);
        if (hmbMessage == null) {
            logger.error("����BEANע�ⶨ�����!" );
            throw new RuntimeException("����BEANע�ⶨ�����!");
        }
        String msgCode = hmbMessage.value();

        IsoMessage m = new IsoMessage();
        m.setMsgCode(msgCode);
        Map<Integer, Field> annotatedFields = parseMap.get(msgCode);

        for (Integer fieldno : annotatedFields.keySet()) {
            Field f = annotatedFields.get(fieldno);
            Class typeClass = f.getType();
            String fieldValue;
            Object objFieldValue = f.get(hmbMsg);
            if (typeClass == String.class) {
                fieldValue = objFieldValue == null ? "#" : (String) objFieldValue;
            } else if (typeClass == int.class) {
                fieldValue = objFieldValue == null ? "0" : String.valueOf(objFieldValue);
            } else if (typeClass == BigDecimal.class) {
                fieldValue = objFieldValue == null ? "0.00" : objFieldValue.toString();
            } else {
                logger.error("����BEAN���ֶ����Ͳ�֧��!" + typeClass.getName());
                throw new RuntimeException("����BEAN���ֶ����Ͳ�֧��!");
            }
            //���⴦�����ĵ�һ���ֶΣ�msgType������
            if (fieldno == 1) {
                if (hmbMsg instanceof SummaryMsg) {
                    fieldValue = "00" + msgCode;
                } else {
                    fieldValue = "01" + msgCode;
                }
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

    private List<IsoMessage> parseMessageList(byte[] buf)
            throws ParseException, UnsupportedEncodingException {
        List<IsoMessage> isoMessageList = new ArrayList<IsoMessage>();
        IsoMessage isoMessage = null;
        int pos = 0;
        do {
            isoMessage = parseMessage(buf, pos);
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
    private IsoMessage parseMessage(byte[] buf, int pos)
            throws ParseException, UnsupportedEncodingException {
        IsoMessage m = new IsoMessage();
        String msgCode = new String(buf, pos + 16 + 1 + 2, 3, encoding);
        m.setMsgCode(msgCode);
        int dataFieldPos = pos + 16;

        //����Bitmap ��128b��
        BitSet bs = new BitSet(128);
        int bitIndex = 0;
        for (int i = pos; i < pos + 16; i++) {
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
        m.setLength(dataFieldPos - pos);
        String nextflag = (String) m.getField(128).getValue();
        m.setHasNext("1".equals(nextflag));
        return m;
    }


    /**
     * ����isomessage��������дmsgBean������
     * @param msgBean
     * @param message
     * @throws IllegalAccessException
     */
    private void assembleMsgBean(Object msgBean, IsoMessage message) throws IllegalAccessException {
        Map<Integer, Field> fieldMap = parseMap.get(message.getMsgCode());
        for (Integer fieldno : fieldMap.keySet()) {
            Field field = fieldMap.get(fieldno);

            String value = (String) message.getField(fieldno).getValue();
            Class typeClass = field.getType();
            if (typeClass == String.class) {
                field.set(msgBean, value);
            } else if (typeClass == int.class) {
                field.set(msgBean, Integer.parseInt(value));
            } else if (typeClass == BigDecimal.class) {
                field.set(msgBean, new BigDecimal(value));
            } else {
                logger.error("����BEAN���ֶ����Ͳ�֧��!" + typeClass.getName());
                throw new RuntimeException("����BEAN���ֶ����Ͳ�֧��!");
            }
        }
    }

    //===============================================================================

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
     * �ݹ���Ҹ�������Ϣ
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
