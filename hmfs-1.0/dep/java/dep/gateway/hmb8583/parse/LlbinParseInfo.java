package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;
import dep.gateway.hmb8583.util.HexCodec;

import java.text.ParseException;

public class LlbinParseInfo extends FieldParseInfo {

	
	public LlbinParseInfo() {
		super(IsoType.LLBIN, 0);
	}

	@Override
	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		if (pos < 0) {
			throw new ParseException(String.format("无效位置 %d", pos), pos);
		}
		length = ((buf[pos] - 48) * 10) + (buf[pos + 1] - 48);
		if (length < 0) {
			throw new ParseException(String.format("Invalid LLBIN length %d pos %d", length, pos), pos);
		}
		if (pos+2 > buf.length || length+pos+2 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for LLBIN field, pos %d (LEN states '%s')", pos, new String(buf, pos, 2)), pos);
		}
		byte[] binval = length == 0 ? new byte[0] : HexCodec.hexDecode(new String(buf, pos + 2, length));
		if (custom == null) {
			return new IsoValue<byte[]>(type, binval, binval.length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(
				new String(buf, pos + 2, length)), binval.length, custom);
			if (v.getValue() == null) {
				return new IsoValue<byte[]>(type, binval, binval.length, null);
			}
			return v;
		}
	}

	@Override
	public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);
		if (length < 0) {
			throw new ParseException(String.format("Invalid LLBIN length %d pos %d", length, pos), pos);
		}
		if (pos+1 > buf.length || length+pos+1 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for LLBIN field, pos %d", pos), pos);
		}
		byte[] _v = new byte[length];
		System.arraycopy(buf, pos+1, _v, 0, length);
		if (custom == null) {
			return new IsoValue<byte[]>(type, _v, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(HexCodec.hexEncode(_v)), custom);
			if (v.getValue() == null) {
				return new IsoValue<byte[]>(type, _v, null);
			}
			return v;
		}
	}

}
