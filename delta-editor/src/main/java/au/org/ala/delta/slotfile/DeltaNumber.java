/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.slotfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormatSymbols;

/**
 * Stores a numeric value (as a float) and the number of decimal places associated with that 
 * value.
 */
public class DeltaNumber {

	private float _value;
	private byte _decimal;

	public DeltaNumber() {
		this((float) 0.0, (byte) 0);
	}

	public DeltaNumber(float value) {
		this(value, (byte) 0);
	}

	public DeltaNumber(float Value, byte Decimal) {
		_value = Value;
		_decimal = Decimal;
	}

	public DeltaNumber(String src) {
		setFromString(src);
	}

	public DeltaNumber(DeltaNumber orig) {
		this(orig._value, orig._decimal);
	}

	public boolean lessThan(DeltaNumber other) {
		return _value < other._value;
	}

	public boolean lessThan(float other) {
		return _value < other;
	}

	public boolean greaterThan(DeltaNumber other) {
		return _value > other._value;
	}

	public boolean greaterThan(float other) {
		return _value > other;
	}

	public boolean equals(DeltaNumber other) {
		return _value == other._value;
	}

	public boolean equals(float other) {
		return _value == other;
	}

	public float asFloat() {
		return _value;
	}
	
	void setFromValue(float Value) {
		_value = Value;
		_decimal = 0;
	}

	void setFromValue(float Value, byte Decimal) {
		_value = Value;
		_decimal = Decimal;
	}

	public void setFromString(String src) {
		char decimal = new DecimalFormatSymbols().getDecimalSeparator();
		int effectiveLength = parseFloat(src);	
		int pos = src.indexOf(decimal);
		if (pos > 0) {
			_decimal = (byte) (effectiveLength - 1 - pos);
		}
	}
	
	/**
	 * A slightly more tolerant version of Float.parseFloat - we allow the valid number to be followed
	 * by non-numeric characters at the end of the string.  This is to partially emulate the behaviour of 
	 * the c library function srctod which parses a source String up until it stops making sense.  
	 * Unfortunately the parser routine can pass values for parsing with a trailing non-digit character so
	 * this is necessary.
	 * @param src the String to parse into a float.
	 * @return the length of the portion of the string containing a parsable number.
	 */
	private int parseFloat(String src) {
		int endIndex = src.length();
		while (!Character.isDigit(src.charAt(endIndex-1))) {
			endIndex--;
		}
		_value = Float.parseFloat(src.substring(0, endIndex));
		return endIndex;
	}
	
	public String asString() {
		String format = String.format("%%.%df", _decimal);
		return String.format(format, _value);
	}
	
	@Override
	public String toString() {
		return asString();
	}

	/**
	 * Buffer should hold 5 bytes - 4 bytes for value, and one byte for decimal
	 * 
	 * @param buf
	 */
	void fromBinary(byte[] buf) {
		ByteBuffer b = ByteBuffer.wrap(buf);
		b.order(ByteOrder.LITTLE_ENDIAN);
		_value = b.getFloat();
		_decimal = b.get();
	}
	
	void fromBinary(byte[] buf, int offset) {
		ByteBuffer b = ByteBuffer.wrap(buf, offset, 5);
		b.order(ByteOrder.LITTLE_ENDIAN);
		_value = b.getFloat();
		_decimal = b.get();
	}
	

	byte[] toBinary() {
		ByteBuffer b = ByteBuffer.allocate(5);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.putFloat(_value);
		b.put(_decimal);
		return b.array();
	}

	public static int size() {
		return 5;
	}

	public byte getDecimal() {
		return _decimal;
	}

}
