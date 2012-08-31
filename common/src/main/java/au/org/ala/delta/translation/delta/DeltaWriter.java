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
package au.org.ala.delta.translation.delta;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

/**
 * Base class for directive export functors.  Provides utility methods
 * for helping to write the directive.
 */
public class DeltaWriter {
	
	protected PrintFile _printer;
	
	public DeltaWriter() {
		_printer = null;
	}
	
	public DeltaWriter(StringBuilder buffer) {
		_printer = new PrintFile(buffer);
	}
	
	public DeltaWriter(PrintFile printer) {
		_printer = printer;
	}
	
	public void setIndent(int indent) {
		_printer.setIndent(indent);
	}
	
	public <T> String valueRangeToString(List<Pair<Integer, T>> values) {
		return valueRangeToString(values, '-', "," , true);
	}
	
	/**
	 * Converts a list of id/value pairs into a string of the format
	 * <id1>,<value1> <id2>,<value2>.  In addition, if sequential ids in 
	 * the list have the same value, they will be condensed in the form
	 * <id1-idx>,<value1>.
	 */
	public <T> String valueRangeToString(List<Pair<Integer, T>> values, 
			char rangeSeparator, String valueSeparator, boolean valueLast) {
		
		if (values.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		
		T previousValue = null;
		int firstInRange = -1;
		int previousNum = -1;
		for (Pair<Integer, T> value : values) {
			int id = value.getFirst();
			if (!value.getSecond().equals(previousValue) || (id != previousNum+1)) {
				
				if (firstInRange >= 0) {
					appendRange(builder, previousValue, firstInRange, previousNum,
							Character.toString(rangeSeparator), valueSeparator, valueLast);
				}
				firstInRange = value.getFirst();
				previousValue = value.getSecond();
			}
			previousNum = value.getFirst();
		}
		appendRange(builder, previousValue, firstInRange, previousNum, 
				Character.toString(rangeSeparator), valueSeparator, valueLast);
		
		return builder.toString();
	}
	
	public String rangeToString(List<Integer> values) {
		return rangeToString(values, ' ', "-");
	}
	
	public String rangeToString(List<Integer> values, String rangeSeparator) {
		return rangeToString(values, ' ', rangeSeparator);
	}
	
	public String rangeToString(List<Integer> values, char separator, char rangeSeparator) {
		return rangeToString(values, separator, Character.toString(rangeSeparator));
	}
	
	public String rangeToString(List<Integer> values, char separator, String rangeSeparator) {
		if (values.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		int firstInRange = -1;
		int previousNum = -1;
		for (int value : values) {
			if (value != previousNum+1) {
				
				if (firstInRange >= 0) {
					appendRange(builder, firstInRange, previousNum, separator, rangeSeparator);
				}
				firstInRange = value;
			}
			previousNum = value;
		}
		appendRange(builder, firstInRange, previousNum, separator, rangeSeparator);
		
		return builder.toString();
	}
  
	private <T> void appendRange(StringBuilder builder, T value,
			int firstInRange, int lastInRange, String rangeSeparator, String valueSeparator, boolean valueLast) {
		
		if (valueLast) {
			appendRange(builder, firstInRange, lastInRange, rangeSeparator);
			builder.append(valueSeparator).append(value);
		}
		else {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(value).append(valueSeparator);
			StringBuilder range = new StringBuilder();
			appendRange(range, firstInRange, lastInRange, rangeSeparator);
			builder.append(range.toString());
		}
	}
	
	private void appendRange(StringBuilder builder, int firstInRange, int lastInRange, String rangeSeparator) {
		appendRange(builder, firstInRange, lastInRange, ' ', rangeSeparator);
	}
	
	private void appendRange(StringBuilder builder, int firstInRange, int lastInRange, char separator, String rangeSeparator) {
		if (builder.length() > 0) {
			builder.append(separator);
		}
		builder.append(firstInRange);
		if (firstInRange != lastInRange) {
			builder.append(rangeSeparator).append(lastInRange);
		}
		
	}

	protected String despaceRTF(String temp) {
		return despaceRTF(temp, false);
	}

	protected String despaceRTF(String text, boolean quoteDelimiters) {
		return Utils.despaceRtf(text, quoteDelimiters);
	}

	public void outputTextBuffer(String buffer, int startIndent, int wrapIndent, boolean preserveNewLines) {
		_printer.setIndent(startIndent);
		_printer.indent();
		_printer.setIndent(wrapIndent);
		String[] lines;
		if (preserveNewLines) {
			lines = buffer.split("\n");
		}
		else {
			String text = buffer.replaceAll("\\s", " ");
			lines = new String[] {text};
		}
		
		for (int i=0; i<lines.length; i++) {
			
			if (preserveNewLines && i != 0 && StringUtils.isBlank(lines[i])) {
				_printer.writeBlankLines(1, 0);
			}
			else {
				_printer.writeJustifiedText(lines[i], -1);
				_printer.printBufferLine();
			}
		}
		_printer.printBufferLine();
	}
	
}
