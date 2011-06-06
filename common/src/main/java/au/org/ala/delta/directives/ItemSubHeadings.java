package au.org.ala.delta.directives;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;

/**
 * This class parses the ITEM SUBHEADINGS directive.
 */
public class ItemSubHeadings extends AbstractTextDirective {

	/** These characters may not be used as delimiters in the directive */
	private static final String[] INAVLID_DELIMITERS = new String[] { "*", "#", "<", ">" };
	
	public ItemSubHeadings() {
		super("item", "subheadings");
	}

	@Override
	public void process(DeltaContext context, String data) throws Exception {
		super.process(context, data);
		StringReader reader = new StringReader(data);
		ItemSubHeadingParser parser = new ItemSubHeadingParser(context, reader);
		parser.parse();
	}
	
	
	
	private class ItemSubHeadingParser extends AbstractStreamParser {

		public ItemSubHeadingParser(DeltaContext context, Reader reader) {
			super(context, reader);
		}
		
		@Override
		public void parse() throws Exception {
			
			for (int i=0; i<getControlWords().length; i++) {
				skipToWhitespace();
				skipWhitespace();
			}
			
		    String delimiter = readDelimiter();
		   	checkDelimiter(delimiter);
			
		    while (_currentChar == '#') {
		    	readNext();
			    int characterNumber = readInteger();
			    readNext();  // current char is '.' and next is a space.
			   
			    String subheading = readToNext('#').trim();
			    
			    _context.itemSubheading(characterNumber, subheading);
		    }
		}
		
		/**
		 * The item subheadings directive allows a delimiter to be specified which can be used to
		 * surround an item subheading.
		 * @return the delimiter or an empty String if none was specified.
		 */
		private String readDelimiter() throws Exception {
			
			return readToNext('#').trim();
			
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
		 * Skips characters in the stream until the next whitespace character is reached.
		 * When this method returns _currentChar will be whitespace or the end of stream will
		 * have been reached (_currentInt = -1)
		 * @return true if whitespace was found.
		 */
		protected boolean skipToWhitespace() throws Exception {
			if (Character.isWhitespace(_currentChar)) {
				return true;
			}

			while (!Character.isWhitespace(_currentChar) && _currentInt >= 0) {
				readNext();
			}
			return _currentInt >= 0;
		}
		
		/**
		 * Checks if the supplied delimiter is valid for this directive and throws a
		 * ParseException if not.
		 * @param delimeter the delimiter to check
		 */
		private void checkDelimiter(String delimeter) throws ParseException {
			for (String invalid : INAVLID_DELIMITERS) {
				if (invalid.equals(delimeter)) {
					throw new ParseException("Invalid delimiter", _position);
				}
			}
		}
		
	}
}
