package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class TimeParseInfo extends FieldParseInfo {

	public TimeParseInfo() {
		super(IsoType.TIME, 6);
	}

	@Override
	public IsoValue<Date> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		if (pos < 0) {
			throw new ParseException(String.format("位置无效 %d", pos), pos);
		}
		if (pos+6 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for TIME field, pos %d", pos), pos);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
		cal.set(Calendar.MINUTE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
		cal.set(Calendar.SECOND, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
		return new IsoValue<Date>(type, cal.getTime(), null);
	}

	@Override
	public IsoValue<Date> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		int[] tens = new int[3];
		int start = 0;
		for (int i = pos; i < pos + tens.length; i++) {
			tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, tens[0]);
		cal.set(Calendar.MINUTE, tens[1]);
		cal.set(Calendar.SECOND, tens[2]);
		return new IsoValue<Date>(type, cal.getTime(), null);
	}

}
