package dep.gateway.hmb8583;

import dep.gateway.hmb8583.util.HexCodec;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;


public class IsoValue<T> implements Cloneable {

    private IsoType type;
    private T value;
    private CustomField<T> encoder;
    private int length;
    private String encoding = "GBK";

    public IsoValue(IsoType t, T value) {
        this(t, value, null);
    }

    public IsoValue(IsoType t, T value, CustomField<T> custom) {
        if (t.needsLength()) {
            throw new IllegalArgumentException("长度未指定");
        }
        encoder = custom;
        type = t;
        this.value = value;
        if (type == IsoType.LVAR || type == IsoType.LLVAR || type == IsoType.LLLVAR) {
            if (custom == null) {
                length = value.toString().length();
            } else {
                String enc = custom.encodeField(value);
                if (enc == null) {
                    enc = value == null ? "" : value.toString();
                }
                length = enc.length();
            }
            if (t == IsoType.LVAR && length > 9) {
                throw new IllegalArgumentException("LVAR 长度超过 9 chars");
            } else if (t == IsoType.LLVAR && length > 99) {
                throw new IllegalArgumentException("LLVAR 长度超过 99 chars");
            } else if (t == IsoType.LLLVAR && length > 999) {
                throw new IllegalArgumentException("LLLVAR 长度超过 999 chars");
            }
        } else if (type == IsoType.LLBIN || type == IsoType.LLLBIN) {
            if (custom == null) {
                if (value instanceof byte[]) {
                    length = ((byte[]) value).length;
                } else {
                    length = value.toString().length() / 2 + (value.toString().length() % 2);
                }
            } else {
                //TODO special encoder, NO strings
                String enc = custom.encodeField(value);
                if (enc == null) {
                    enc = value == null ? "" : value.toString();
                }
                length = enc.length();
            }
            if (t == IsoType.LLBIN && length > 99) {
                throw new IllegalArgumentException("LLBIN 长度超过 to 99 chars");
            } else if (t == IsoType.LLLBIN && length > 999) {
                throw new IllegalArgumentException("LLLBIN 长度超过 999 chars");
            }
        } else {
            length = type.getLength();
        }
    }

    public IsoValue(IsoType t, T val, int len) {
        this(t, val, len, null);
    }

    public IsoValue(IsoType t, T val, int len, CustomField<T> custom) {
        type = t;
        value = val;
        length = len;
        encoder = custom;
        if (length == 0 && t.needsLength()) {
            throw new IllegalArgumentException(String.format("长度错误  type %s (value '%s')", t, val));
        } else if (t == IsoType.LVAR || t == IsoType.LLVAR || t == IsoType.LLLVAR) {
            if (len == 0) {
                length = custom == null ? val.toString().length() : custom.encodeField(value).length();
            }
            if (t == IsoType.LVAR && length > 9) {
                throw new IllegalArgumentException("LVAR 长度超过 99 chars");
            } else if (t == IsoType.LLVAR && length > 99) {
                throw new IllegalArgumentException("LLVAR 长度超过 99 chars");
            } else if (t == IsoType.LLLVAR && length > 999) {
                throw new IllegalArgumentException("LLLVAR 长度超过 999 chars");
            }
        } else if (t == IsoType.LLBIN || t == IsoType.LLLBIN) {
            if (len == 0) {
                //TODO customfield binary!
                length = custom == null ? ((byte[]) val).length : custom.encodeField(value).length();
            }
            if (t == IsoType.LLBIN && length > 99) {
                throw new IllegalArgumentException("LLBIN 长度超过 99 chars");
            } else if (t == IsoType.LLLBIN && length > 999) {
                throw new IllegalArgumentException("LLLBIN 长度超过 999 chars");
            }
        }
    }

    public IsoType getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public T getValue() {
        return value;
    }

    public void setCharacterEncoding(String value) {
        encoding = value;
    }

    public String getCharacterEncoding() {
        return encoding;
    }

    public String toString() {
        if (value == null) {
            return "ISOValue<null>";
        }
        if (type == IsoType.NUMERIC || type == IsoType.AMOUNT) {
            if (type == IsoType.AMOUNT) {
                if (value instanceof BigDecimal) {
                    return type.format((BigDecimal) value, 12);
                } else {
                    return type.format(value.toString(), 12);
                }
            } else if (value instanceof Number) {
                return type.format(((Number) value).longValue(), length);
            } else {
                return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length);
            }
        } else if (type == IsoType.ALPHA) {
            return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length);
        } else if (type == IsoType.LVAR || type == IsoType.LLVAR || type == IsoType.LLLVAR) {
            return encoder == null ? value.toString() : encoder.encodeField(value);
        } else if (value instanceof Date) {
            return type.format((Date) value);
        } else if (type == IsoType.BINARY) {
            if (value instanceof byte[]) {
                return type.format(encoder == null ? HexCodec.hexEncode((byte[]) value) : encoder.encodeField(value), length * 2);
            } else {
                return type.format(encoder == null ? value.toString() : encoder.encodeField(value), length * 2);
            }
        } else if (type == IsoType.LLBIN || type == IsoType.LLLBIN) {
            if (value instanceof byte[]) {
                return encoder == null ? HexCodec.hexEncode((byte[]) value) : encoder.encodeField(value);
            } else {
                String _s = encoder == null ? value.toString() : encoder.encodeField(value);
                return (_s.length() % 2 == 1) ? String.format("0%s", _s) : _s;
            }
        }
        return encoder == null ? value.toString() : encoder.encodeField(value);
    }

    @SuppressWarnings("unchecked")
    public IsoValue<T> clone() {
        try {
            return (IsoValue<T>) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof IsoValue<?>)) {
            return false;
        }
        IsoValue<?> comp = (IsoValue<?>) other;
        return (comp.getType() == getType() && comp.getValue().equals(getValue()) && comp.getLength() == getLength());
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : toString().hashCode();
    }

    public CustomField<T> getEncoder() {
        return encoder;
    }

    public void write(OutputStream outs, boolean binary) throws IOException {
        if (type == IsoType.LLLVAR || type == IsoType.LLVAR || type == IsoType.LVAR) {
            if (type == IsoType.LLLVAR) {
                outs.write((length / 100) + 48);
            }
            if (length >= 10) {
                outs.write(((length % 100) / 10) + 48);
            } else {
                if (type != IsoType.LVAR) {
                    outs.write(48);
                }
            }
            outs.write((length % 10) + 48);
        } else {
            throw new RuntimeException("域类型定义错误！");
        }
        outs.write(encoding == null ? toString().getBytes() : toString().getBytes(encoding));
    }
}
