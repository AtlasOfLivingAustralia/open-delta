package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;

/**
 * Parser for directives of the form:
 * 
 * <directive> <optional delimiter>
 * #<id>. <optional comment> <optional delimiter><optional value><optional delimiter>
 * #<id>. <optional comment> <optional delimiter><optional value><optional delimiter>
 * ...
 * for example, TYPESETTING MARKS uses this format.
 */
public abstract class TextListParser<T> extends DirectiveArgsParser {
	
	/** These characters may not be used as delimiters in the directive */
	private static final char[] INVALID_DELIMITERS = new char[] { '*', '#', '<', '>' };

	private static final char COMMENT_DELIMITER = '<';
	
	private char _delimiter;
	
	public TextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		_delimiter = readDelimiter();
		
		String delimiter = "";
		if (_delimiter != 0) {
			delimiter = Character.toString(_delimiter);
		}
		checkDelimiter(_delimiter);
		
		DirectiveArgument<Integer> delimiterArg = new DirectiveArgument<Integer>(Integer.MIN_VALUE);
		delimiterArg.setText(delimiter);
		_args.add(delimiterArg);
		
	    while (_currentChar == MARK_IDENTIFIER) {
	    	
	    	readSingle();
	    }
	}

	protected void readSingle() throws ParseException {
		T id = readId();
		String comment = readOptionalComment();
		String value = readText();
		
		_args.addDirectiveArgument(id, comment, value);
	}
	
	
	/**
	 * The item subheadings directive allows a delimiter to be specified which can be used to
	 * surround an item subheading.
	 * @return the delimiter or an empty String if none was specified.
	 */
	private char readDelimiter() throws ParseException {
		
		String possibleDelimiter = readToNext(MARK_IDENTIFIER).trim();
		if (possibleDelimiter.length() > 1) {
			throw new ParseException("Invalid character at position: "+_position, _position);
		}
		if (possibleDelimiter.length() == 0) {
			return 0;
		}
		char delimiter = possibleDelimiter.charAt(0);
		checkDelimiter(delimiter);
		
		return delimiter;
	}
	
	/**
	 * Reads the typesetting number in the form: #<number>.
	 * @return the number.
	 * @throws Exception if there was a problem reading the number.
	 */
	protected abstract T readId() throws ParseException;
	
	protected String readText() throws ParseException {
		
		String value = "";
		
		if (_delimiter == 0) {
		   value = cleanWhiteSpace(readToNext(MARK_IDENTIFIER));
		}
		else {
			consumeWhiteSpace();
			
			// A typesetting mark may contain only a comment and no value.
			if (_currentChar == MARK_IDENTIFIER || _currentInt < 0) {
				value = "";
			}
			else {
				expect(_delimiter);
				
				// Consume the delimiter.
				readNext();
				
				value = cleanWhiteSpace(readToNext(_delimiter));
				
				expect(_delimiter);
				
				readNext();
				
				consumeWhiteSpace();
				
				expect(MARK_IDENTIFIER, true);
			}
				
		}
		return value;
	}
	
	protected String readOptionalComment() throws ParseException {
		String comment = "";
		consumeWhiteSpace();
		if (_currentChar == COMMENT_DELIMITER) {
			comment = readComment();
		}
		return comment;
	}
	
	private void consumeWhiteSpace() throws ParseException {

		while (Character.isWhitespace(_currentChar)) {
			readNext();
		}
	}
	
	/**
	 * Checks if the supplied delimiter is valid for this directive and throws a
	 * ParseException if not.
	 * @param delimeter the delimiter to check
	 */
	private void checkDelimiter(char delimiter) throws ParseException {
		for (char invalid : INVALID_DELIMITERS) {
			if (invalid == delimiter) {
				throw new ParseException("Invalid delimiter", _position);
			}
		}
	}
	
	
}
