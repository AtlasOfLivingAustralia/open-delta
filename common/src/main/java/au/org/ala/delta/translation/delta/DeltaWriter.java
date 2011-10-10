package au.org.ala.delta.translation.delta;

import java.util.Arrays;
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
	
	/**
	 * Converts a list of id/value pairs into a string of the format
	 * <id1>,<value1> <id2>,<value2>.  In addition, if sequential ids in 
	 * the list have the same value, they will be condensed in the form
	 * <id1-idx>,<value1>.
	 */
	public <T> String valueRangeToString(List<Pair<Integer, T>> values) {
		
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
				
				if (firstInRange > 0) {
					appendRange(builder, previousValue, firstInRange,
							previousNum);
				}
				firstInRange = value.getFirst();
				previousValue = value.getSecond();
			}
			previousNum = value.getFirst();
		}
		appendRange(builder, previousValue, firstInRange, previousNum);
		
		return builder.toString();
	}
	
	public String rangeToString(List<Integer> values) {
		return rangeToString(values, ' ');
	}
	
	public String rangeToString(List<Integer> values, char separator) {
		if (values.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		int firstInRange = -1;
		int previousNum = -1;
		for (int value : values) {
			if (value != previousNum+1) {
				
				if (firstInRange > 0) {
					appendRange(builder, firstInRange, previousNum, separator);
				}
				firstInRange = value;
			}
			previousNum = value;
		}
		appendRange(builder, firstInRange, previousNum, separator);
		
		return builder.toString();
	}
  
	private <T> void appendRange(StringBuilder builder, T value,
			int firstInRange, int lastInRange) {
		appendRange(builder, firstInRange, lastInRange);
		
		builder.append(",").append(value);
	}
	
	private void appendRange(StringBuilder builder, int firstInRange, int lastInRange) {
		appendRange(builder, firstInRange, lastInRange, ' ');
	}
	
	private void appendRange(StringBuilder builder, int firstInRange, int lastInRange, char separator) {
		if (builder.length() > 0) {
			builder.append(separator);
		}
		builder.append(firstInRange);
		if (firstInRange != lastInRange) {
			builder.append("-").append(lastInRange);
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
			System.out.println(Arrays.asList(lines));
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
