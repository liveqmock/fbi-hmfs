package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class AlphaParseInfo extends AlphaNumericFieldParseInfo {

	public AlphaParseInfo(int len) {
		super(IsoType.ALPHA, len);
	}

	public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom)
	throws ParseException, UnsupportedEncodingException {
		if (custom == null) {
			return new IsoValue<String>(type, new String(buf, pos, length, getCharacterEncoding()), length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(new String(buf, pos, length, getCharacterEncoding())), length, custom);
			if (v.getValue() == null) {
				return new IsoValue<String>(type, new String(buf, pos, length, getCharacterEncoding()), length, null);
			}
			return v;
		}
	}

}
