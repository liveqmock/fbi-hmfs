package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public abstract class AlphaNumericFieldParseInfo extends FieldParseInfo {

	public AlphaNumericFieldParseInfo(IsoType t, int len) {
		super(t, len);
	}

	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException, UnsupportedEncodingException {
		if (pos < 0) {
			throw new ParseException(String.format("无效位置 %d", pos), pos);
		}
		if (pos+length > buf.length) {
			throw new ParseException(String.format("数据长度错误 for %s field of length %d, pos %d",
				type, length, pos), pos);
		}
		String _v = new String(buf, pos, length, getCharacterEncoding());
		if (_v.length() != length) {
			_v = new String(buf, pos, buf.length-pos, getCharacterEncoding()).substring(0, length);
		}
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

}
