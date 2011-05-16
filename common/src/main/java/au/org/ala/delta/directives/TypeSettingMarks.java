package au.org.ala.delta.directives;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

/**
 * Processes the TYPESETTING MARKS directive.
 */
public class TypeSettingMarks extends AbstractDirective<DeltaContext> {

	public TypeSettingMarks() {
		super("typesetting", "marks");
	}
	@Override
	public void process(DeltaContext context, String data) throws Exception {
		TypeSettingMarksParser parser = new TypeSettingMarksParser(context, new StringReader(data));
		
		parser.parse();
		
	}
	/** These characters may not be used as delimiters in the directive */
	private static final char[] INVALID_DELIMITERS = new char[] { '*', '#', '<', '>' };

	private static final char MARK_IDENTIFIER = '#';
	private static final char COMMENT_DELIMITER = '<';
	
	
	private class TypeSettingMarksParser extends AbstractStreamParser {

		private char _delimiter;
		
		public TypeSettingMarksParser(DeltaContext context, Reader reader) {
			super(context, reader);
		}
		
		@Override
		public void parse() throws Exception {
			
			_delimiter = readDelimiter();
		   	checkDelimiter(_delimiter);
			
		    while (_currentChar == MARK_IDENTIFIER) {
		    	
		    	int markNumber = readTypesettingMarkNumber();
		    	
		    	// read and throw away the comment...
		    	readOptionalComment();
			    
		    	// read the typesetting mark.
		    	TypeSettingMark mark = readTypeSettingMark(markNumber);
			    
			    _context.addTypeSettingMark(mark);
		    }
		}
		
		/**
		 * The item subheadings directive allows a delimiter to be specified which can be used to
		 * surround an item subheading.
		 * @return the delimiter or an empty String if none was specified.
		 */
		private char readDelimiter() throws Exception {
			
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
		private int readTypesettingMarkNumber() throws Exception {
			
			expect(MARK_IDENTIFIER);
			
			readNext();
			
			int markNumber = readInteger();
			
			expect('.');
		    readNext();  // consume the . character.
		    return markNumber;
		}
		
		private TypeSettingMark readTypeSettingMark(int markNumber) throws Exception {
			
			String mark = "";
			boolean allowWhiteSpace = false;
			if (_delimiter == 0) {
			   mark = readToNext(MARK_IDENTIFIER);
			}
			else {
				consumeWhiteSpace();
				
				// A typesetting mark may contain only a comment and no value.
				if (_currentChar == MARK_IDENTIFIER || _currentInt < 0) {
					mark = "";
					allowWhiteSpace = false;
				}
				else {
					expect(_delimiter);
					
					// Consume the delimiter.
					readNext();
					
					mark = cleanWhiteSpace(readToNext(_delimiter));
					
					expect(_delimiter);
					
					readNext();
					
					consumeWhiteSpace();
					
					expect(MARK_IDENTIFIER, true);
					
					allowWhiteSpace = mark.startsWith(" ");
				}
					
			}
			return new TypeSettingMark(MarkPosition.fromId(markNumber), mark.trim(), allowWhiteSpace);
		}
		
		protected String readOptionalComment() throws Exception {
			String comment = "";
			consumeWhiteSpace();
			if (_currentChar == COMMENT_DELIMITER) {
				comment = readComment();
			}
			return comment;
		}
		
		private void consumeWhiteSpace() throws Exception {

			while (Character.isWhitespace(_currentChar)) {
				readNext();
			}
		}
		
		/**
		 * Reads from the stream up the next character of the specified type or until the
		 * end of the stream is reached.
		 * @param character the character to read up to.
		 * @return the contents of the stream up to (but not including) the supplied character.
		 * @throws Exception if there is an error reading from the stream.
		 */
		private String readToNext(char character) throws Exception {
			if (_currentChar == character) {
				return "";
			}
			StringBuilder text = new StringBuilder();
			
			boolean finished = false;
			
			while (!finished && _currentInt >= 0) {
				
				if (_currentChar == character) {
					finished = true;
				}
				else {
					text.append(_currentChar);
					readNext();
				}
				
			}
			return text.toString();
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
		
		private void expect(char token) throws ParseException {
			expect(token, false);
		}
		
		private void expect(char token, boolean allowEndOfStream) throws ParseException {
			if (allowEndOfStream && _currentInt < 0) {
				return;
			}
			if (_currentChar != token) {
				throw new ParseException("Invalid character found.", _position);
			}
		}
		
		protected String cleanWhiteSpace(String input) {
			input = super.cleanWhiteSpace(input);
			Pattern p = Pattern.compile("(\\W)\\s(\\W)");
			Matcher m = p.matcher(input);
			input = m.replaceAll("$1$2");
			
			m = p.matcher(input);
			input = m.replaceAll("$1$2");
			
			p = Pattern.compile("(\\W)\\s(\\w)");
			m = p.matcher(input);
			input = m.replaceAll("$1$2");
			
			p = Pattern.compile("(\\w)\\s(\\W)");
			m = p.matcher(input);
			input = m.replaceAll("$1$2");
			
			return input;
		}
	
	}
	
}
