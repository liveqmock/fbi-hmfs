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
import dep.gateway.hmb8583.util.HexCodec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/** This class is used to parse fields of type BINARY.
 * 
 * @author Enrique Zamudio
 */
public class BinaryParseInfo extends FieldParseInfo {

	
	public BinaryParseInfo(int len) {
		super(IsoType.BINARY, len);
	}

	@Override
	public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
			throws ParseException, UnsupportedEncodingException {
		if (pos < 0) {
			throw new ParseException(String.format("Invalid position %d", pos), pos);
		}
		if (pos+(length*2) > buf.length) {
			throw new ParseException(String.format("Insufficient data for BINARY field of length %d, pos %d",
				length, pos), pos);
		}
		byte[] binval = HexCodec.hexDecode(new String(buf, pos, length*2));
		if (custom == null) {
			return new IsoValue<byte[]>(type, binval, binval.length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(new String(buf, pos, length*2, getCharacterEncoding())), length, custom);
			if (v.getValue() == null) {
				return new IsoValue<byte[]>(type, binval, binval.length, null);
			}
			return v;
		}
	}

	@Override
	public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom) throws ParseException {
		byte[] _v = new byte[length];
		System.arraycopy(buf, pos, _v, 0, length);
		if (custom == null) {
			return new IsoValue<byte[]>(type, _v, length, null);
		} else {
			@SuppressWarnings("unchecked")
			IsoValue<?> v = new IsoValue(type, custom.decodeField(HexCodec.hexEncode(_v)), length, custom);
			if (v.getValue() == null) {
				return new IsoValue<byte[]>(type, _v, length, null);
			}
			return v;
		}
	}

}