package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Date10ParseInfo extends FieldParseInfo {

	private static final long FUTURE_TOLERANCE;

	static {
		FUTURE_TOLERANCE = Long.parseLong(System.getProperty("j8583.future.tolerance", "900000"));
	}
	public Date10ParseInfo() {
		super(IsoType.DATE10, 10);
	}

	@Override
	public IsoValue<Date> parse(byte[] buf, int pos, CustomField<?> custom)
			throws ParseException {
		if (pos < 0) {
			throw new ParseException(String.format("位置无效 %d", pos), pos);
		}
		if (pos+10 > buf.length) {
			throw new ParseException(String.format("数据长度错误 for DATE10 field, pos %d", pos), pos);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, ((buf[pos] - 48) * 10) + buf[pos + 1] - 49);
		cal.set(Calendar.DATE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
		cal.set(Calendar.HOUR_OF_DAY, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
		cal.set(Calendar.MINUTE, ((buf[pos + 6] - 48) * 10) + buf[pos + 7] - 48);
		cal.set(Calendar.SECOND, ((buf[pos + 8] - 48) * 10) + buf[pos + 9] - 48);
		cal.set(Calendar.MILLISECOND,0);
		adjustWithFutureTolerance(cal);
		return new IsoValue<Date>(type, cal.getTime(), null);
	}

	@Override
	public IsoValue<Date> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		int[] tens = new int[5];
		int start = 0;
		for (int i = pos; i < pos + tens.length; i++) {
			tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, tens[0] - 1);
		cal.set(Calendar.DATE, tens[1]);
		cal.set(Calendar.HOUR_OF_DAY, tens[2]);
		cal.set(Calendar.MINUTE, tens[3]);
		cal.set(Calendar.SECOND, tens[4]);
		cal.set(Calendar.MILLISECOND,0);
		adjustWithFutureTolerance(cal);
		return new IsoValue<Date>(type, cal.getTime(), null);
	}

	public static void adjustWithFutureTolerance(Calendar cal) {
		long now = System.currentTimeMillis();
		long then = cal.getTimeInMillis();
		if (then > now && then-now > FUTURE_TOLERANCE) {
			cal.add(Calendar.YEAR, -1);
		}
	}

}
