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
package au.org.ala.delta.directives.args;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;

import java.io.Reader;
import java.text.ParseException;

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
	
	private boolean _cleanWhiteSpace;
	
	public TextListParser(DeltaContext context, Reader reader) {
		this(context, reader, true);
	}
	
	public TextListParser(DeltaContext context, Reader reader, boolean cleanWhiteSpace) {
		super(context, reader);
		_cleanWhiteSpace = cleanWhiteSpace;
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
	 * Various directives allow a delimiter to be specified which can be used to
	 * surround text containing CONFOR special characters.
	 * @return the delimiter or an empty String if none was specified.
	 */
	private char readDelimiter() throws ParseException {
		
		String possibleDelimiter = readToNext(MARK_IDENTIFIER).trim();
		if (possibleDelimiter.length() > 1) {
			throw DirectiveError.asException(DirectiveError.Error.DELIMITER_ONE_CHARACTER, _position);
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
		   value = readToNext(MARK_IDENTIFIER);
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
				
				value = readToNext(_delimiter);
				
				expect(_delimiter);
				
				readNext();
				
				consumeWhiteSpace();
				
				expect(MARK_IDENTIFIER, true);
			}
				
		}
		if (_cleanWhiteSpace) {
			value = cleanWhiteSpace(value);
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
	 * @param delimiter the delimiter to check
	 */
	private void checkDelimiter(char delimiter) throws ParseException {
		for (char invalid : INVALID_DELIMITERS) {
			if (invalid == delimiter) {
				throw DirectiveError.asException(DirectiveError.Error.ILLEGAL_DELIMETER, _position);
			}
		}
	}
	
	
}
