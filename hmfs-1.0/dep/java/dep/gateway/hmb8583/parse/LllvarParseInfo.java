package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class LllvarParseInfo extends FieldParseInfo {

	public LllvarParseInfo() {
		super(IsoType.LLLVAR, 0);
	}

	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
	throws ParseException, UnsupportedEncodingException {
		if (pos < 0) {
			throw new ParseException(String.format("无效位置 %d", pos), pos);
		}
		if (!(Character.isDigit(buf[pos]) && Character.isDigit(buf[pos+1]) && Character.isDigit(buf[pos+2]))) {
			throw new ParseException(String.format("Invalid LLLVAR length '%s' pos %d",
				new String(buf, pos, 3), pos), pos);
		}
		length = ((buf[pos] - 48) * 100) + ((buf[pos + 1] - 48) * 10) + (buf[pos + 2] - 48);
		if (length < 0) {
			throw new ParseException(String.format("Invalid LLLVAR length %d pos %d", length, pos), pos);
		}
		String _v = length == 0 ? "" : new String(buf, pos + 3, length, getCharacterEncoding());
		if (custom == null) {
			return new IsoValue<String>(type, _v, length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(_v), length, custom);
			if (v.getValue() == null) {
				//problems decoding? return the string
				return new IsoValue<String>(type, _v, length, null);
			}
			return v;
		}
	}

	public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom)
			throws ParseException, UnsupportedEncodingException {
		length = ((buf[pos] & 0x0f) * 100) + (((buf[pos + 1] & 0xf0) >> 4) * 10) + (buf[pos + 1] & 0x0f);
		if (length < 0) {
			throw new ParseException(String.format("Invalid LLLVAR length %d pos %d", length, pos), pos);
		}
		if (length+pos+2 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for LLLVAR field, pos %d", pos), pos);
		}
		if (custom == null) {
			return new IsoValue<String>(type, new String(buf, pos + 2, length, getCharacterEncoding()), null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(new String(buf, pos + 2, length, getCharacterEncoding())), custom);
			if (v.getValue() == null) {
				return new IsoValue<String>(type, new String(buf, pos + 2, length, getCharacterEncoding()), null);
			}
			return v;
		}
	}

}
