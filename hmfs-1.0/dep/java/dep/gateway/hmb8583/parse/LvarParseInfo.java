package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class LvarParseInfo extends FieldParseInfo {

    public LvarParseInfo() {
        super(IsoType.LVAR, 0);
    }

    public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
            throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("位置无效 %d", pos), pos);
        }
        length = buf[pos] - 48;
        if (length < 0) {
            throw new ParseException(String.format("Invalid LVAR length %d pos %d", length, pos), pos);
        }

        String _v = length == 0 ? "" : new String(buf, pos + 1, length, getCharacterEncoding());

        if (custom == null) {
            return new IsoValue<String>(type, _v, length, null);
        } else {
            @SuppressWarnings("unchecked")
            IsoValue<?> v = new IsoValue(type, custom.decodeField(_v), length, custom);
            if (v.getValue() == null) {
                return new IsoValue<String>(type, _v, length, null);
            }
            return v;
        }
    }

    public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom)
            throws ParseException, UnsupportedEncodingException {
        // 暂时无此应用
        return null;
    }

}
