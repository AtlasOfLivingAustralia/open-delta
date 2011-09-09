package au.org.ala.delta.editor.ui.validator;

import java.util.Stack;

public abstract class DescriptionValidator {
	
	private final static char START_COMMENT_CHAR = '<';
	private final static char END_COMMENT_CHAR = '>';
	
	private static final String COMMENT_MISMATCH = "COMMENT_MISMATCH";
	
	/**
	 * <p>
	 * Taken from the Delta User Guide:
	 * </p>
	 * The descriptions of items may contain comments delimited by angle brackets (&lt;&gt;).
	 * <p>
	 * To be interpreted as a delimiting bracket, an opening bracket must be at the start of a line, or be preceded by a blank, a left bracket, or a right bracket; 
	 * A closing bracket must be at the end of a line, or be followed by a blank, a right bracket, a left bracket, or a slash
	 * <p>
	 * Nesting of comments is allowed, e.g. &lt;aaa &lt;bbb&gt;&gt;.
	 * 
	 * @param text
	 */
	protected ValidationResult validateComments(String text) {
		Stack<Integer> posStack = new Stack<Integer>();
		int lastSymbol = -1;
		int commentDepth = 0;
		
		ValidationResult result = null;
		
		for (int i = 0; i < text.length(); ++i) {
			
			char current = text.charAt(i);
			
			if (current == START_COMMENT_CHAR) {
				Character prev = (i == 0 ? null : text.charAt(i - 1));
				// Start of line, or preceeded by blank, open or close bracket
				if (prev == null || " <>".indexOf(prev) >= 0) {
					// Start of line - is a delimiting bracket
					posStack.push(i);
					commentDepth++;
					lastSymbol = i;
				} 				
			} else if (current == END_COMMENT_CHAR) {
				// End of line, or succeeded by a blank, open or close bracket or slash
				Character next = i < text.length() - 1 ? text.charAt(i + 1) : null; 
				if (next == null || " <>\\".indexOf(next) >= 0) {
					commentDepth--;
					lastSymbol = i;
					if (posStack.size() > 0) {
						posStack.pop();
					}
				}
			}
		}
		
		if (commentDepth != 0) {
			if (posStack.size() > 0) {
				lastSymbol = posStack.pop();
			}
			result = ValidationResult.error(COMMENT_MISMATCH, lastSymbol);
		}

		return result == null ? new ValidationResult() : result;
	}

}
