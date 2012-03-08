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

/**
 * This class is used to parse fields of type LLVAR.
 *
 * @author Enrique Zamudio
 */
public class LvarParseInfo extends FieldParseInfo {

    public LvarParseInfo() {
        super(IsoType.LVAR, 0);
    }

    public IsoValue<?> parse(byte[] buf, int pos, CustomField<?> custom)
            throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid position %d", pos), pos);
        }
        length = buf[pos] - 48;
        if (length < 0) {
            throw new ParseException(String.format("Invalid LVAR length %d pos %d", length, pos), pos);
        }

        String _v = length == 0 ? "" : new String(buf, pos + 1, length, getCharacterEncoding());

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

    public IsoValue<?> parseBinary(byte[] buf, int pos, CustomField<?> custom)
            throws ParseException, UnsupportedEncodingException {
        // 暂时无此应用
        return null;
    }

}
