/*
j8583 A Java implementation of the ISO8583 protocol
Copyright (C) 2007 Enrique Zamudio Lopez

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/
package dep.gateway.hmb8583;

import dep.gateway.hmb8583.parse.FieldParseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * This class is used to create messages, either from scratch or from an existing String or byte
 * buffer. It can be configured to put default values on newly created messages, and also to know
 * what to expect when reading messages from an InputStream.
 * <p/>
 * The factory can be configured to know what values to set for newly created messages, both from
 * a template (useful for fields that must be set with the same value for EVERY message created)
 * and individually (for trace [field 11] and message date [field 7]).
 * <p/>
 * It can also be configured to know what fields to expect in incoming messages (all possible values
 * must be stated, indicating the date type for each). This way the messages can be parsed from
 * a byte buffer.
 *
 * @author Enrique Zamudio
 */
@Deprecated
public class MessageFactory {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * This map stores the message template for each message type.
     */
    private Map<Integer, IsoMessage> typeTemplates = new HashMap<Integer, IsoMessage>();
    /**
     * Stores the information needed to parse messages sorted by type.
     */
    private Map<Integer, Map<Integer, FieldParseInfo>> parseMap = new HashMap<Integer, Map<Integer, FieldParseInfo>>();
    /**
     * Stores the field numbers to be parsed, in order of appearance.
     */
    private Map<Integer, List<Integer>> parseOrder = new HashMap<Integer, List<Integer>>();

    private TraceNumberGenerator traceGen;
    /**
     * The ISO header to be included in each message type.
     */
    private Map<Integer, String> isoHeaders = new HashMap<Integer, String>();
    /**
     * A map for the custom field encoder/decoders, keyed by field number.
     */
    private Map<Integer, CustomField> customFields = new HashMap<Integer, CustomField>();
    /**
     * Indicates if the current date should be set on new messages (field 7).
     */
    private boolean setDate;
    /**
     * Indicates if the factory should create binary messages and also parse binary messages.
     */
    private boolean useBinary;
    private int etx = -1;
    /**
     * Flag to specify if missing fields should be ignored as long as they're at the end of the message.
     */
    private boolean ignoreLast;
    private boolean forceb2;
    private String encoding = System.getProperty("file.encoding");

    /**
     * Sets the character encoding used for parsing ALPHA, LLVAR and LLLVAR fields.
     */
    public void setCharacterEncoding(String value) {
        encoding = value;
        if (parseMap.size() > 0) {
            for (Map<Integer, FieldParseInfo> pt : parseMap.values()) {
                for (FieldParseInfo fpi : pt.values()) {
                    fpi.setCharacterEncoding(encoding);
                }
            }
        }
    }

    /**
     * Returns the encoding used to parse ALPHA, LLVAR and LLLVAR fields. The default is the file.encoding
     * system property.
     */
    public String getCharacterEncoding() {
        return encoding;
    }

    /**
     * Sets or clears the flag to pass to new messages, to include a secondary bitmap even if it's not needed.
     */
    public void setForceSecondaryBitmap(boolean flag) {
        forceb2 = flag;
    }

    public boolean getForceSecondaryBitmap() {
        return forceb2;
    }

    /**
     * Setting this property to true avoids getting a ParseException when parsing messages that don't have
     * the last field specified in the bitmap. This is common with certain providers where field 128 is
     * specified in the bitmap but not actually included in the messages. Default is false, which has
     * been the behavior in previous versions when this option didn't exist.
     */
    public void setIgnoreLastMissingField(boolean flag) {
        ignoreLast = flag;
    }

    /**
     * This flag indicates if the MessageFactory throws an exception if the last field of a message
     * is not really present even though it's specified in the bitmap. Default is false which means
     * an exception is thrown.
     */
    public boolean getIgnoreLastMissingField() {
        return ignoreLast;
    }

    /**
     * Specifies a map for custom field encoder/decoders. The keys are the field numbers.
     */
    public void setCustomFields(Map<Integer, CustomField> value) {
        customFields = value;
    }

    /**
     * Sets the CustomField encoder for the specified field number.
     */
    public void setCustomField(int index, CustomField<?> value) {
        customFields.put(index, value);
    }

    /**
     * Returns a custom field encoder/decoder for the specified field number, if one is available.
     */
    @SuppressWarnings("unchecked")
    public <T> CustomField<T> getCustomField(int index) {
        return customFields.get(index);
    }

    /**
     * Returns a custom field encoder/decoder for the specified field number, if one is available.
     */
    @SuppressWarnings("unchecked")
    public <T> CustomField<T> getCustomField(Integer index) {
        return customFields.get(index);
    }

    /**
     * Tells the receiver to read the configuration at the specified path. This just calls
     * ConfigParser.configureFromClasspathConfig() with itself and the specified path at arguments,
     * but is really convenient in case the MessageFactory is being configured from within, say, Spring.
     */
    public void setConfigPath(String path) throws IOException {
//        ConfigParser.configureFromClasspathConfig(this, path);
    }

    /**
     * Tells the receiver to create and parse binary messages if the flag is true.
     * Default is false, that is, create and parse ASCII messages.
     */
    public void setUseBinaryMessages(boolean flag) {
        useBinary = flag;
    }

    /**
     * Returns true is the factory is set to create and parse binary messages,
     * false if it uses ASCII messages. Default is false.
     */
    public boolean getUseBinaryMessages() {
        return useBinary;
    }

    /**
     * Sets the ETX character to be sent at the end of the message. This is optional and the
     * default is -1, which means nothing should be sent as terminator.
     *
     * @param value The ASCII value of the ETX character or -1 to indicate no terminator should be used.
     */
    public void setEtx(int value) {
        etx = value;
    }

    public int getEtx() {
        return etx;
    }

    /**
     * Creates a new message of the specified type, with optional trace and date values as well
     * as any other values specified in a message template. If the factory is set to use binary
     * messages, then the returned message will be written using binary coding.
     *
     * @param type The message type, for example 0x200, 0x400, etc.
     */
    public IsoMessage newMessage(int type) {
//        IsoMessage m = new IsoMessage(isoHeaders.get(type));
        IsoMessage m = new IsoMessage();
        //m.setType(type);
//        m.setEtx(etx);
        //m.setBinary(useBinary);
        // m.setForceSecondaryBitmap(forceb2);
//        m.setForceSecondaryBitmap(true);
        m.setCharacterEncoding(encoding);

        //Copy the values from the template
        IsoMessage templ = typeTemplates.get(type);
        if (templ != null) {
            for (int i = 2; i <= 128; i++) {
                if (templ.hasField(i)) {
                    //We could detect here if there's a custom object with a CustomField,
                    //but we can't copy the value so there's no point.
                    m.setField(i, templ.getField(i).clone());
                }
            }
        }
        /*if (traceGen != null) {
            m.setValue(11, traceGen.nextTrace(), IsoType.NUMERIC, 6);
        }
        if (setDate) {
            m.setValue(7, new Date(), IsoType.DATE10, 10);
        }*/
        return m;
    }


    public Map<String, List<IsoMessage>> parseTxnMessageMap(byte[] buf)
            throws ParseException, UnsupportedEncodingException {
        Map<String, List<IsoMessage>> txnMessageMap = new HashMap<String, List<IsoMessage>>();
        // 获取交易码
        String txnCode = new String(buf, 0, 4, "GBK");
        byte[] subBuf = new byte[buf.length - 4];
        System.arraycopy(buf, 4, subBuf, 0, subBuf.length);
        txnMessageMap.put(txnCode, parseMessageList(subBuf));
        return txnMessageMap;
    }

    private List<IsoMessage> parseMessageList(byte[] buf)
            throws ParseException, UnsupportedEncodingException {
        List<IsoMessage> isoMessageList = new ArrayList<IsoMessage>();
        IsoMessage isoMessage = null;
        int curntLength = 0;
        do {
            isoMessage = parseMessage(buf, 19 + curntLength, 16 + curntLength);
            isoMessageList.add(isoMessage);
            curntLength += isoMessage.getLength();
            if(!isoMessage.isHasNext()){
                break;
            }
        } while (curntLength <= buf.length);

        return isoMessageList;
    }

    /**
     * 根据BEAN注解创建新8583报文
     */
    public IsoMessage parseMessage(byte[] buf, int isoHeaderLength, int pos)
            throws ParseException, UnsupportedEncodingException {
        //IsoMessage m = new IsoMessage(isoHeaderLength > 0 ? new String(buf, 0, isoHeaderLength) : null);
//        IsoMessage m = new IsoMessage(null);
        IsoMessage m = new IsoMessage();
        m.setCharacterEncoding(encoding);

        int type = 0;
        type = ((buf[isoHeaderLength] - 48) << 8)
                | ((buf[isoHeaderLength + 1] - 48) << 4)
                | (buf[isoHeaderLength + 2] - 48);
//        m.setType(type);
        //Parse the bitmap (primary first)
        BitSet bs = new BitSet(128);
        int bitIndex = 0;
        for (int i = 0; i < 16; i++) {
            int bit = 128;
            for (int b = 0; b < 8; b++) {
                bs.set(bitIndex++, (buf[i] & bit) != 0);
                bit >>= 1;
            }
        }

        //开始转换域信息
        Map<Integer, FieldParseInfo> parseGuide = parseMap.get(type);
        List<Integer> index = parseOrder.get(type);
        if (index == null) {
            log.error(String.format("ISO8583 MessageFactory 配置文件中未找到报文编号 %04x [%s]",
                    type, new String(buf)));
            return null;
        }

        // 检查转换模板中是否设置所需的域信息
        boolean abandon = false;
        for (int i = 0; i < bs.length(); i++) {
            if (bs.get(i) && !index.contains(i + 1)) {
                log.warn("ISO8583 MessageFactory 无法保存域 {}: 没有配置信息", i + 1);
                abandon = true;
            }
        }
        if (abandon) {
            return m;
        }

        //转换获取数据域值
        for (Integer i : index) {
            FieldParseInfo fpi = parseGuide.get(i);
            if (bs.get(i - 1)) {
                /* if (ignoreLast && pos >= buf.length && i == index.get(index.size() - 1)) {
                  log.warn("Field {} is not really in the message even though it's in the bitmap", i);
                  bs.clear(i - 1);
              } else {*/
                IsoValue<?> val = fpi.parse(buf, pos, getCustomField(i));
                m.setField(i, val);
                //To get the correct next position, we need to get the number of bytes, not chars
                pos += val.toString().getBytes(fpi.getCharacterEncoding()).length;
                if (val.getType() == IsoType.LLVAR || val.getType() == IsoType.LLBIN) {
                    pos += 2;
                } else if (val.getType() == IsoType.LLLVAR || val.getType() == IsoType.LLLBIN) {
                    pos += 3;
                } else if(val.getType() == IsoType.LVAR) {
                    pos++;
                }
                //}
            }
        }
        m.setLength(pos);
        m.setHasNext(bs.get(127));
//        m.setBinary(useBinary);
        return m;
    }

    /**
     * 根据XML中的定义生成新报文
     * @param buf
     * @param isoHeaderLength
     * @param pos
     * @return
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public IsoMessage parseMessageByXmlDef(byte[] buf, int isoHeaderLength, int pos)
            throws ParseException, UnsupportedEncodingException {
        //IsoMessage m = new IsoMessage(isoHeaderLength > 0 ? new String(buf, 0, isoHeaderLength) : null);
//        IsoMessage m = new IsoMessage(null);
        IsoMessage m = new IsoMessage();
        m.setCharacterEncoding(encoding);

        int type = 0;
        type = ((buf[isoHeaderLength] - 48) << 8)
                | ((buf[isoHeaderLength + 1] - 48) << 4)
                | (buf[isoHeaderLength + 2] - 48);
//        m.setType(type);
        //Parse the bitmap (primary first)
        BitSet bs = new BitSet(128);
        int bitIndex = 0;
        for (int i = 0; i < 16; i++) {
            int bit = 128;
            for (int b = 0; b < 8; b++) {
                bs.set(bitIndex++, (buf[i] & bit) != 0);
                bit >>= 1;
            }
        }

        //开始转换域信息
        Map<Integer, FieldParseInfo> parseGuide = parseMap.get(type);
        List<Integer> index = parseOrder.get(type);
        if (index == null) {
            log.error(String.format("ISO8583 MessageFactory 配置文件中未找到报文编号 %04x [%s]",
                    type, new String(buf)));
            return null;
        }

        // 检查转换模板中是否设置所需的域信息
        boolean abandon = false;
        for (int i = 0; i < bs.length(); i++) {
            if (bs.get(i) && !index.contains(i + 1)) {
                log.warn("ISO8583 MessageFactory 无法保存域 {}: 没有配置信息", i + 1);
                abandon = true;
            }
        }
        if (abandon) {
            return m;
        }

        //转换获取数据域值
        for (Integer i : index) {
            FieldParseInfo fpi = parseGuide.get(i);
            if (bs.get(i - 1)) {
                /* if (ignoreLast && pos >= buf.length && i == index.get(index.size() - 1)) {
                  log.warn("Field {} is not really in the message even though it's in the bitmap", i);
                  bs.clear(i - 1);
              } else {*/
                IsoValue<?> val = fpi.parse(buf, pos, getCustomField(i));
                m.setField(i, val);
                //To get the correct next position, we need to get the number of bytes, not chars
                pos += val.toString().getBytes(fpi.getCharacterEncoding()).length;
                if (val.getType() == IsoType.LLVAR || val.getType() == IsoType.LLBIN) {
                    pos += 2;
                } else if (val.getType() == IsoType.LLLVAR || val.getType() == IsoType.LLLBIN) {
                    pos += 3;
                } else if(val.getType() == IsoType.LVAR) {
                    pos++;
                }
                //}
            }
        }
        m.setLength(pos);
        m.setHasNext(bs.get(127));
//        m.setBinary(useBinary);
        return m;
    }

    public void setAssignDate(boolean flag) {
        setDate = flag;
    }

    public boolean getAssignDate() {
        return setDate;
    }

    public void setTraceNumberGenerator(TraceNumberGenerator value) {
        traceGen = value;
    }

    public TraceNumberGenerator getTraceNumberGenerator() {
        return traceGen;
    }

    public void setIsoHeaders(Map<Integer, String> value) {
        isoHeaders.clear();
        isoHeaders.putAll(value);
    }

    public void setIsoHeader(int type, String value) {
        if (value == null) {
            isoHeaders.remove(type);
        } else {
            isoHeaders.put(type, value);
        }
    }

    public String getIsoHeader(int type) {
        return isoHeaders.get(type);
    }

    public void addMessageTemplate(IsoMessage templ) {
        if (templ != null) {
//            typeTemplates.put(templ.getType(), templ);
        }
    }

    public void removeMessageTemplate(int type) {
        typeTemplates.remove(type);
    }

    public IsoMessage getMessageTemplate(int type) {
        return typeTemplates.get(type);
    }

    /**
     * Invoke this method in case you want to freeze the configuration, making message and parsing
     * templates, as well as iso headers and custom fields, immutable.
     */
    public void freeze() {
        typeTemplates = Collections.unmodifiableMap(typeTemplates);
        parseMap = Collections.unmodifiableMap(parseMap);
        parseOrder = Collections.unmodifiableMap(parseOrder);
        isoHeaders = Collections.unmodifiableMap(isoHeaders);
        customFields = Collections.unmodifiableMap(customFields);
    }

    /**
     * Sets a map with the fields that are to be expected when parsing a certain type of
     * message.
     *
     * @param type The message type.
     * @param map  A map of FieldParseInfo instances, each of which define what type and length
     *             of field to expect. The keys will be the field numbers.
     */
    public void setParseMap(int type, Map<Integer, FieldParseInfo> map) {
        parseMap.put(type, map);
        ArrayList<Integer> index = new ArrayList<Integer>();
        index.addAll(map.keySet());
        Collections.sort(index);
        log.trace(String.format("ISO8583 MessageFactory adding parse map for type %04x with fields %s", type, index));
        parseOrder.put(type, index);
    }

}
