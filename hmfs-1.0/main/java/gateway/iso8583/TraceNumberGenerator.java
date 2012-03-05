/*
j8583 A Java implementation of the ISO8583 protocol
Copyright (C) 2007 Enrique Zamudio Lopez

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
package gateway.iso8583;

/** This interface defines the behavior needed to provide sequence numbers for newly created
 * messages. It must provide sequence numbers between 1 and 999999, as per the ISO standard.
 * This value is put in field 11.
 * A default version that simply iterates through an int in memory is provided.
 * 
 * @author Enrique Zamudio
 */
public interface TraceNumberGenerator {

	/** Returns the next trace number. */
	public int nextTrace();

	/** Returns the last number that was generated. */
	public int getLastTrace();

}
