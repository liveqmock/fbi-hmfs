package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Date4ParseInfo extends FieldParseInfo {

	public Date4ParseInfo() {
		super(IsoType.DATE4, 4);
	}

	@Override
	public IsoValue<Date> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		if (pos < 0) {
			throw new ParseException(String.format("位置无效 %d", pos), pos);
		}
		if (pos+4 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for DATE4 field, pos %d", pos), pos);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.MONTH, ((buf[pos] - 48) * 10) + buf[pos + 1] - 49);
		cal.set(Calendar.DATE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
		Date10ParseInfo.adjustWithFutureTolerance(cal);
		return new IsoValue<Date>(type, cal.getTime(), null);
	}

	@Override
	public IsoValue<Date> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		int[] tens = new int[2];
		int start = 0;
		for (int i = pos; i < pos + tens.length; i++) {
			tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MONTH, tens[0] - 1);
		cal.set(Calendar.DATE, tens[1]);
		cal.set(Calendar.MILLISECOND,0);
		Date10ParseInfo.adjustWithFutureTolerance(cal);
		return new IsoValue<Date>(type, cal.getTime(), null);
	}

}
