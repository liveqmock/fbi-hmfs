package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;
import dep.gateway.hmb8583.util.HexCodec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class BinaryParseInfo extends FieldParseInfo {

	public BinaryParseInfo(int len) {
		super(IsoType.BINARY, len);
	}

	@Override
	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
			throws ParseException, UnsupportedEncodingException {
		if (pos < 0) {
			throw new ParseException(String.format("位置无效 %d", pos), pos);
		}
		if (pos+(length*2) > buf.length) {
			throw new ParseException(String.format("数据长度错误 for BINARY field of length %d, pos %d",
				length, pos), pos);
		}
		byte[] binval = HexCodec.hexDecode(new String(buf, pos, length*2));
		if (custom == null) {
			return new IsoValue<byte[]>(type, binval, binval.length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(new String(buf, pos, length*2, getCharacterEncoding())), length, custom);
			if (v.getValue() == null) {
				return new IsoValue<byte[]>(type, binval, binval.length, null);
			}
			return v;
		}
	}

	@Override
	public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		byte[] _v = new byte[length];
		System.arraycopy(buf, pos, _v, 0, length);
		if (custom == null) {
			return new IsoValue<byte[]>(type, _v, length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(HexCodec.hexEncode(_v)), length, custom);
			if (v.getValue() == null) {
				return new IsoValue<byte[]>(type, _v, length, null);
			}
			return v;
		}
	}

}
