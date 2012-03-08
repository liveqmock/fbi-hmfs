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

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/** This is the common abstract superclass to parse ALPHA and NUMERIC field types.
 * 
 * @author Enrique Zamudio
 */
public abstract class AlphaNumericFieldParseInfo extends FieldParseInfo {

	public AlphaNumericFieldParseInfo(IsoType t, int len) {
		super(t, len);
	}

	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom) throws ParseException, UnsupportedEncodingException {
		if (pos < 0) {
			throw new ParseException(String.format("Invalid position %d", pos), pos);
		}
		if (pos+length > buf.length) {
			throw new ParseException(String.format("Insufficient data for %s field of length %d, pos %d",
				type, length, pos), pos);
		}
		String _v = new String(buf, pos, length, getCharacterEncoding());
		if (_v.length() != length) {
			_v = new String(buf, pos, buf.length-pos, getCharacterEncoding()).substring(0, length);
		}
		if (custom == null) {
			return new IsoValue<String>(type, _v, length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(_v), length, custom);
			if (v.getValue() == null) {
				return new IsoValue<String>(type, _v, length, null);
			}
			return v;
		}
	}

}
