/*
j8583 A Java implementation of the ISO8583 protocol
Copyright (C) 2011 Enrique Zamudio Lopez

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/
package dep.gateway.hmb8583.parse;

import dep.gateway.hmb8583.CustomField;
import dep.gateway.hmb8583.IsoType;
import dep.gateway.hmb8583.IsoValue;

import java.math.BigDecimal;
import java.text.ParseException;

/** This class is used to parse AMOUNT fields.
 * 
 * @author Enrique Zamudio
 */
public class AmountParseInfo extends FieldParseInfo {

	
	public AmountParseInfo() {
		super(IsoType.AMOUNT, 12);
	}

	public IsoValue<BigDecimal> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		if (pos < 0) {
			throw new ParseException(String.format("Invalid position %d", pos), pos);
		}
		if (pos+12 > buf.length) {
			throw new ParseException(String.format("Insufficient data for AMOUNT field, pos %d", pos), pos);
		}
		String c = new String(buf, pos, 12);
		try {
			return new IsoValue<BigDecimal>(type, new BigDecimal(c).movePointLeft(2), null);
		} catch (NumberFormatException ex) {
			throw new ParseException(String.format("Cannot read amount '%s' pos %d", new String(c), pos), pos);
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
