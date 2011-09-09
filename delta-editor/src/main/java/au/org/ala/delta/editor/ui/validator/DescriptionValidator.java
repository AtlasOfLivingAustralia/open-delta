package au.org.ala.delta.editor.ui.validator;

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
	 * To be interpreted as a delimiting bracket, an opening bracket must be at the start of a line, or be preceded by a blank, a left bracket, or a right bracket; and a closing bracket must be at the
	 * end of a line, or be followed by a blank, a right bracket, a left bracket, or the slash which terminates that part of the character description.
	 * <p>
	 * Nesting of comments is allowed, e.g. &lt;aaa &lt;bbb&gt;&gt;.
	 * 
	 * @param text
	 */
	protected ValidationResult validateComments(String text) {
		int commentDepth = 0;
		int lastSymbol = -1;
		
		ValidationResult result = null;

		for (int i = 0; i < text.length(); ++i) {
			char ch = text.charAt(i);
			if (ch == START_COMMENT_CHAR) {
				commentDepth++;
				lastSymbol = i;
				
			} else if (ch == END_COMMENT_CHAR) {
				commentDepth--;
				lastSymbol = i;
			}
		}
		
		if (commentDepth != 0) {
			result = ValidationResult.error(COMMENT_MISMATCH, lastSymbol);
		}

		return result == null ? new ValidationResult() : result;
	}

}
