package au.org.ala.delta.rtf;

import java.io.IOException;


/**
 * A special keyword is given access to the parser internals to perform special processing.
 */
public abstract class SpecialKeyword extends Keyword {

	public SpecialKeyword(String keyword) {
		super(keyword, KeywordType.Special);
	}
	
	/**
	 * Process the keyword, returning an array of characters to output.
	 * @param param the keyword parameter.
	 * @param reader the RTFReader - provides access to the stream being read and the current state.
	 * @return an array of characters to be output.
	 */
	public abstract char[] process(int param, RTFReader reader) throws IOException;
	
}
