package dep.gateway.hmb8583;

import java.math.BigDecimal;
import java.util.Date;

public enum IsoType {
	NUMERIC(true, 0),
	ALPHA(true, 0),
    // 新增 1字节变长长度
    LVAR(false, 0),
	LLVAR(false, 0),
	LLLVAR(false, 0),
	DATE10(false, 10),
	DATE4(false, 4),
	DATE_EXP(false, 4),
	TIME(false, 6),
	AMOUNT(false, 12),
	BINARY(true, 0),
	LLBIN(false, 0),
	LLLBIN(false, 0);

	private boolean needsLen;
	private int length;

	IsoType(boolean flag, int l) {
		needsLen = flag;
		length = l;
	}

	public boolean needsLength() {
		return needsLen;
	}

	public int getLength() {
		return length;
	}

	public String format(Date value) {
		if (this == DATE10) {
			return String.format("%Tm%<Td%<TH%<TM%<TS", value);
		} else if (this == DATE4) {
			return String.format("%Tm%<Td", value);
		} else if (this == DATE_EXP) {
			return String.format("%Ty%<Tm", value);
		} else if (this == TIME) {
			return String.format("%TH%<TM%<TS", value);
		}
		throw new IllegalArgumentException("Cannot format date as " + this);
	}

	public String format(String value, int length) {
		if (this == ALPHA) {
	    	if (value == null) {
	    		value = "";
	    	}
	        if (value.length() > length) {
	            return value.substring(0, length);
	        } else if (value.length() == length) {
	        	return value;
	        } else {
	        	return String.format(String.format("%%-%ds", length), value);
	        }
		} else if (this == LLVAR || this == LLLVAR) {
			return value;
		} else if (this == NUMERIC) {
	        char[] c = new char[length];
	        char[] x = value.toCharArray();
	        if (x.length > length) {
	        	throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
	        }
	        int lim = c.length - x.length;
	        for (int i = 0; i < lim; i++) {
	            c[i] = '0';
	        }
	        System.arraycopy(x, 0, c, lim, x.length);
	        return new String(c);
		} else if (this == AMOUNT) {
			return IsoType.NUMERIC.format(new BigDecimal(value).movePointRight(2).longValue(), 12);
		} else if (this == BINARY) {

	    	if (value == null) {
	    		value = "";
	    	}
	        if (value.length() > length) {
	            return value.substring(0, length);
	        }
	        char[] c = new char[length];
	        int end = value.length();
	        if (value.length() % 2 == 1) {
	        	c[0] = '0';
		        System.arraycopy(value.toCharArray(), 0, c, 1, value.length());
		        end++;
	        } else {
		        System.arraycopy(value.toCharArray(), 0, c, 0, value.length());
	        }
	        for (int i = end; i < c.length; i++) {
	            c[i] = '0';
	        }
	        return new String(c);

		} else if (this == LLBIN || this == LLLBIN) {
			return value;
		}
		throw new IllegalArgumentException("Cannot format String as " + this);
	}

	public String format(long value, int length) {
		if (this == NUMERIC) {
			String x = String.format(String.format("%%0%dd", length), value);
	        if (x.length() > length) {
	        	throw new IllegalArgumentException("长度错误  length: " + value + " LEN " + length);
	        }
	        return x;
		} else if (this == ALPHA || this == LLVAR || this == LLLVAR) {
			return format(Long.toString(value), length);
		} else if (this == AMOUNT) {
			return String.format("%010d00", value);
		} else if (this == BINARY || this == LLBIN || this == LLLBIN) {
			//TODO
		}
		throw new IllegalArgumentException("Cannot format number as " + this);
	}

	public String format(BigDecimal value, int length) {
		if (this == AMOUNT) {
			return String.format("%012d", value.movePointRight(2).longValue());
		} else if (this == NUMERIC) {
			return format(value.longValue(), length);
		} else if (this == ALPHA || this == LLVAR || this == LLLVAR) {
			return format(value.toString(), length);
		} else if (this == BINARY || this == LLBIN || this == LLLBIN) {
			//TODO
		}
		throw new IllegalArgumentException("Cannot format BigDecimal as " + this);
	}

	public IsoValue<Object> value(Object val, int len) {
		return new IsoValue<Object>(this, val, len);
	}

	public IsoValue<Object> value(Object val) {
		return new IsoValue<Object>(this, val);
	}

	public IsoValue<Object> call(Object val, int len) {
		return new IsoValue<Object>(this, val, len);
	}

	public IsoValue<Object> call(Object val) {
		return new IsoValue<Object>(this, val);
	}

	public IsoValue<Object> apply(Object val, int len) {
		return new IsoValue<Object>(this, val, len);
	}
	public IsoValue<Object> apply(Object val) {
		return new IsoValue<Object>(this, val);
	}

}
