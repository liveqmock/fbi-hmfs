package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public abstract class FieldParseInfo {

	protected IsoType type;
	protected int length;
	private String encoding = System.getProperty("file.encoding");

	public FieldParseInfo(IsoType t, int len) {
		if (t == null) {
			throw new IllegalArgumentException("IsoType ²»ÄÜÎª¿Õ");
		}
		type = t;
		length = len;
	}

	public void setCharacterEncoding(String value) {
		encoding = value;
	}
	public String getCharacterEncoding() {
		return encoding;
	}

	public int getLength() {
		return length;
	}

	public IsoType getType() {
		return type;
	}

	public abstract IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
	throws ParseException, UnsupportedEncodingException;

	public abstract IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom)
	throws ParseException, UnsupportedEncodingException;

	public static FieldParseInfo getInstance(IsoType t, int len, String encoding) {
		FieldParseInfo fpi = null;
		if (t == IsoType.ALPHA) {
			fpi = new AlphaParseInfo(len);
		} else if (t == IsoType.AMOUNT) {
			fpi = new AmountParseInfo();
		} else if (t == IsoType.BINARY) {
			fpi = new BinaryParseInfo(len);
		} else if (t == IsoType.DATE10) {
			fpi = new Date10ParseInfo();
		} else if (t == IsoType.DATE4) {
			fpi = new Date4ParseInfo();
		} else if (t == IsoType.DATE_EXP) {
			fpi = new DateExpParseInfo();
		} else if (t == IsoType.LLBIN) {
			fpi = new LlbinParseInfo();
		} else if (t == IsoType.LLLBIN) {
			fpi = new LllbinParseInfo();
		} else if (t == IsoType.LLLVAR) {
			fpi = new LllvarParseInfo();
		} else if (t == IsoType.LLVAR) {
			fpi = new LlvarParseInfo();
		}else if (t == IsoType.LVAR) {
			fpi = new LvarParseInfo();
		} else if (t == IsoType.NUMERIC) {
			fpi = new NumericParseInfo(len);
		} else if (t == IsoType.TIME) {
			fpi = new TimeParseInfo();
		}
		if (fpi == null) {
	 		throw new IllegalArgumentException(String.format("Can not parse type %s", t));
		}
		fpi.setCharacterEncoding(encoding);
		return fpi;
	}

}
