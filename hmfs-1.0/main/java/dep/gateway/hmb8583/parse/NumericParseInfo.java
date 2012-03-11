package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.math.BigInteger;
import java.text.ParseException;

public class NumericParseInfo extends AlphaNumericFieldParseInfo {

	public NumericParseInfo(int len) {
		super(IsoType.NUMERIC, len);
	}

	public IsoValue<Number> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		//A long covers up to 18 digits
		if (length < 19) {
			long l = 0;
			long power = 1L;
			for (int i = pos + (length / 2) + (length % 2) - 1; i >= pos; i--) {
				l += (buf[i] & 0x0f) * power;
				power *= 10L;
				l += ((buf[i] & 0xf0) >> 4) * power;
				power *= 10L;
			}
			return new IsoValue<Number>(IsoType.NUMERIC, l, length, null);
		} else {
			//Use a BigInteger
			char[] digits = new char[length];
			int start = 0;
			for (int i = pos; i < pos + (length / 2) + (length % 2); i++) {
				digits[start++] = (char)(((buf[i] & 0xf0) >> 4) + 48);
				digits[start++] = (char)((buf[i] & 0x0f) + 48);
			}
			return new IsoValue<Number>(IsoType.NUMERIC, new BigInteger(new String(digits)), length, null);
		}
	}

}
