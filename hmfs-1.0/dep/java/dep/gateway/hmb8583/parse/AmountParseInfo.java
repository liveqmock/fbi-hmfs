package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.math.BigDecimal;
import java.text.ParseException;

public class AmountParseInfo extends FieldParseInfo {

	public AmountParseInfo() {
		super(IsoType.AMOUNT, 12);
	}

	public IsoValue<BigDecimal> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		if (pos < 0) {
			throw new ParseException(String.format("位置无效 %d", pos), pos);
		}
		if (pos+12 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for AMOUNT field, pos %d", pos), pos);
		}
		String c = new String(buf, pos, 12);
		try {
			return new IsoValue<BigDecimal>(type, new BigDecimal(c).movePointLeft(2), null);
		} catch (NumberFormatException ex) {
			throw new ParseException(String.format("不能读 amount '%s' pos %d", new String(c), pos), pos);
		}
	}

	public IsoValue<BigDecimal> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		char[] digits = new char[13];
		digits[10] = '.';
		int start = 0;
		for (int i = pos; i < pos + 6; i++) {
			digits[start++] = (char)(((buf[i] & 0xf0) >> 4) + 48);
			digits[start++] = (char)((buf[i] & 0x0f) + 48);
			if (start == 10) {
				start++;
			}
		}
		try {
			return new IsoValue<BigDecimal>(IsoType.AMOUNT, new BigDecimal(new String(digits)), null);
		} catch (NumberFormatException ex) {
			throw new ParseException(String.format("Cannot read amount '%s' pos %d", new String(digits), pos), pos);
		}
	}

}
