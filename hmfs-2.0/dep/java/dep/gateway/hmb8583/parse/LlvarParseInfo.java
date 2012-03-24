package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class LlvarParseInfo extends FieldParseInfo {

	public LlvarParseInfo() {
		super(IsoType.LLVAR, 0);
	}

	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
			throws ParseException, UnsupportedEncodingException {
		if (pos < 0) {
			throw new ParseException(String.format("无效位置 %d", pos), pos);
		}
		length = ((buf[pos] - 48) * 10) + (buf[pos + 1] - 48);
		if (length < 0) {
			throw new ParseException(String.format("Invalid LLVAR length %d pos %d", length, pos), pos);
		}
		String _v = length == 0 ? "" : new String(buf, pos + 2, length, getCharacterEncoding());
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
		
		length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);
		if (length < 0) {
			throw new ParseException(String.format("Invalid LLVAR length %d pos %d", length, pos), pos);
		}
		if (pos+1 > buf.length || length+pos+1 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for LLVAR field, pos %d", pos), pos);
		}
		if (custom == null) {
			return new IsoValue<String>(type, new String(buf, pos + 1, length, getCharacterEncoding()), null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(new String(buf, pos + 1, length, getCharacterEncoding())), custom);
			if (v.getValue() == null) {
				return new IsoValue<String>(type, new String(buf, pos + 1, length, getCharacterEncoding()), null);
			}
			return v;
		}
	}

}
