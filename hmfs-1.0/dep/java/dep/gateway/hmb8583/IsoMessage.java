package dep.gateway.hmb8583;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Map;

public class IsoMessage {

    static final byte[] HEX = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private String msgCode;
    private IsoValue[] fields = new IsoValue[129];
    private String encoding = "GBK";
    
    // 包长度
    private int length;
    // 是否是报文中最后一个子报文
    private boolean isLastMsg = false;

    public IsoMessage() {
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setCharacterEncoding(String value) {
        encoding = value;
    }

    public String getCharacterEncoding() {
        return encoding;
    }


    public <T> T getObjectValue(int field) {
        @SuppressWarnings("unchecked")
        IsoValue<T> v = fields[field];
        return v == null ? null : v.getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> IsoValue<T> getField(int field) {
        return fields[field];
    }

    public IsoMessage setField(int index, IsoValue<?> field) {
        if (index < 1 || index > 129) {
            throw new IndexOutOfBoundsException("Field index must be between 1 and 128");
        }
        if (field != null) {
            field.setCharacterEncoding(encoding);
        }
        fields[index] = field;
        return this;
    }

    public IsoMessage setFields(Map<Integer, IsoValue<?>> values) {
        for (Map.Entry<Integer, IsoValue<?>> e : values.entrySet()) {
            setField(e.getKey(), e.getValue());
        }
        return this;
    }

    public IsoMessage setValue(int index, Object value, IsoType t, int length) {
        return setValue(index, value, null, t, length);
    }

    public <T> IsoMessage setValue(int index, T value, CustomField<T> encoder, IsoType t, int length) {
        if (index < 1 || index > 129) {
            throw new IndexOutOfBoundsException("Field index must be between 1 and 128");
        }
        if (value == null) {
            fields[index] = null;
        } else {
            IsoValue v = null;
            if (t.needsLength()) {
                v = new IsoValue<T>(t, value, length, encoder);
            } else {
                v = new IsoValue<T>(t, value, encoder);
            }
            v.setCharacterEncoding(encoding);
            fields[index] = v;
        }
        return this;
    }

    public boolean hasField(int idx) {
        return fields[idx] != null;
    }

    public void write(OutputStream outs) throws IOException {
        byte[] data = writeData();

        outs.write(data);
        outs.flush();
    }


    public byte[] writeData() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        //Bitmap
        BitSet bs = new BitSet(128);
        for (int i = 1; i < 129; i++) {
            if (fields[i] != null) {
                bs.set(i - 1);
            }
        }

        //Write bitmap
        /*
        //二进制处理
        int pos = 128;
        int b = 0;
        for (int i = 0; i < bs.size(); i++) {
            if (bs.get(i)) {
                b |= pos;
            }
            pos >>= 1;
            if (pos == 0) {
                bout.write(b);
                pos = 128;
                b = 0;
            }
        }
        */

        //BITMAP字符处理    zhanrui 20120312修改
        int pos = 0;
        int lim = bs.size() / 4;
        for (int i = 0; i < lim; i++) {
            int nibble = 0;
            if (bs.get(pos++))
                nibble |= 8;
            if (bs.get(pos++))
                nibble |= 4;
            if (bs.get(pos++))
                nibble |= 2;
            if (bs.get(pos++))
                nibble |= 1;
            bout.write(HEX[nibble]);
        }

        //Fields
        for (int i = 1; i < 129; i++) {
            IsoValue<?> v = fields[i];
            if (v != null) {
                try {
                    v.write(bout, false);
                } catch (IOException ex) {
                    //
                }
            }
        }
        return bout.toByteArray();
    }


    public void copyFieldsFrom(IsoMessage src, int... idx) {
        for (int i : idx) {
            IsoValue<Object> v = src.getField(i);
            if (v == null) {
                setField(i, null);
            } else {
                setValue(i, v.getValue(), v.getEncoder(), v.getType(), v.getLength());
            }
        }
    }

    public  String toString() {
        System.out.printf("MSGCODE: %s\n", this.getMsgCode());
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= 128; i++) {
            if (this.hasField(i)) {
                sb.append(String.format("F%3d(%s): %s -> '%s'\n", i, this.getField(i).getType(),
                        this.getObjectValue(i), this.getField(i).toString()));
            }
        }
        return sb.toString();
    }

    // ===========新增字段 ==================================
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isLastMsg() {
        return isLastMsg;
    }

    public void setLastMsg(boolean lastMsg) {
        isLastMsg = lastMsg;
    }
}
