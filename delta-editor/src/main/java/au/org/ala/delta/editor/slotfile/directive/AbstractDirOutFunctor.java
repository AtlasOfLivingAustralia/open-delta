package au.org.ala.delta.editor.slotfile.directive;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

/**
 * Base class for directive export functors.  Provides utility methods
 * for helping to write the directive.
 */
public abstract class AbstractDirOutFunctor implements DirectiveFunctor {
	
	protected Printer _printer;
	protected StringBuilder _textBuffer;
	
	/**
	 * Writes the directive name then delegates to subclass to supply the
	 * arguments.
	 */
	@Override
	public void process(DirectiveInOutState state) {
		_printer = state.getPrinter();
		_textBuffer = new StringBuilder();
		writeDirective(state);
		writeDirectiveArguments(state);
	}
	
	/**
	 * Should be overridden by subclasses to write the directive arguments.
	 */
	protected abstract void writeDirectiveArguments(DirectiveInOutState state);
	
	/**
	 * Writes the name of the directive to the output supplied by the 
	 * DirectiveInOutState.
	 */
	protected void writeDirective(DirectiveInOutState state) {
		
		_textBuffer.append('*');
		DirectiveInstance directive = state.getCurrentDirective();
	    if (directive.isCommented()) {
	    	_textBuffer.append("COMMENT ");
	    }
	    
	    Directive directiveInfo = directive.getDirective();
	    _textBuffer.append(directiveInfo.joinNameComponents());
		state.getPrinter().writeJustifiedText(_textBuffer.toString(), -1);
		_textBuffer = new StringBuilder();
	}
	
	/**
	 * Writes a single line of text to the printer supplied by the 
	 * DirectiveInOutState.
	 */
	protected void writeLine(DirectiveInOutState state, String text) {
		Printer printer = state.getPrinter();
		printer.writeJustifiedText(text, -1);
		printer.printBufferLine();
	}
	
	/**
	 * Converts a list of id/value pairs into a string of the format
	 * <id1>,<value1> <id2>,<value2>.  In addition, if sequential ids in 
	 * the list have the same value, they will be condensed in the form
	 * <id1-idx>,<value1>.
	 */
	protected <T> String valueRangeToString(List<Pair<Integer, T>> values) {
		
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
	
	protected String rangeToString(List<Integer> values) {
		if (values.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		int firstInRange = -1;
		int previousNum = -1;
		for (int value : values) {
			if (value != previousNum+1) {
				
				if (firstInRange > 0) {
					appendRange(builder, firstInRange, previousNum);
				}
				firstInRange = value;
			}
			previousNum = value;
		}
		appendRange(builder, firstInRange, previousNum);
		
		return builder.toString();
	}
  
	private <T> void appendRange(StringBuilder builder, T value,
			int firstInRange, int lastInRange) {
		appendRange(builder, firstInRange, lastInRange);
		
		builder.append(",").append(value);
	}
	
	private void appendRange(StringBuilder builder, int firstInRange, int lastInRange) {
		if (builder.length() > 0) {
			builder.append(" ");
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

	protected void outputTextBuffer(int startIndent, int wrapIndent, boolean preserveNewLines) {
		_printer.setIndent(startIndent);
		_printer.indent();
		_printer.setIndent(wrapIndent);
		String[] lines;
		if (preserveNewLines) {
			lines = _textBuffer.toString().split("\n");
			System.out.println(Arrays.asList(lines));
		}
		else {
			String text = _textBuffer.toString().replaceAll("\\s", " ");
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
		_textBuffer = new StringBuilder();
	}
	
}
