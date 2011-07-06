package au.org.ala.delta.editor.slotfile.directive;

import java.util.List;

import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.util.Pair;

/**
 * Base class for directive export functors.  Provides utility methods
 * for helping to write the directive.
 */
public abstract class AbstractDirOutFunctor implements DirectiveFunctor {
	
	/**
	 * Writes the directive name then delegates to subclass to supply the
	 * arguments.
	 */
	@Override
	public void process(DirectiveInOutState state) {
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
		String directive = state.getCurrentDirective().getDirective().joinNameComponents();
		state.getPrinter().writeJustifiedText("*"+directive, -1);
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
	protected <T> String rangeToString(List<Pair<Integer, T>> values) {
		
		if (values.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		
		T previousValue = null;
		int firstInRange = -1;
		int previousNum = -1;
		for (Pair<Integer, T> value : values) {
			
			if (!value.getSecond().equals(previousValue)) {
				
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
		if (builder.charAt(builder.length()-1) == ' ') {
			builder.setLength(builder.length() - 1);
		}
		return builder.toString();
	}

	private <T> void appendRange(StringBuilder builder, T value,
			int firstInRange, int lastInRange) {
		builder.append(firstInRange);
		if (firstInRange != lastInRange) {
			builder.append("-").append(lastInRange);
		}
		
		builder.append(",").append(value);
		builder.append(" ");
	}
	
}
