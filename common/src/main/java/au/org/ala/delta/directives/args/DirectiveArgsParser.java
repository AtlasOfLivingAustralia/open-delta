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

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.IntegerValidator;
import org.apache.commons.lang.math.IntRange;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DirectiveArgsParser provides methods for parsing common formats
 * used in DELTA directives.
 */
public abstract class DirectiveArgsParser extends AbstractStreamParser {

	protected static final char MARK_IDENTIFIER = '#';
	protected static final char VALUE_SEPARATOR = ',';
	public static final char SET_VALUE_SEPARATOR = ':';
	
	protected DirectiveArguments _args;
	protected int _markedInt;
	protected boolean _validateRangeOrder = true;

	public DirectiveArgsParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}
	
	protected String readFully() throws ParseException {
		int next = readNext();
		StringBuilder text = new StringBuilder();
		while (next != -1) {
			text.append((char)next);
			next = readNext();
		}
		return text.toString();
	}
	
	protected void expect(char token) throws ParseException {
		expect(token, false);
	}
	
	protected void expect(char token, boolean allowEndOfStream) throws ParseException {
		if (allowEndOfStream && _currentInt < 0) {
			return;
		}
		if (_currentChar != token) {
			throw DirectiveError.asException(DirectiveError.Error.ILLEGAL_SYMBOL_WITH_ARGS,_position-1, token, _currentChar);
		}
	}
	
	protected BigDecimal readValue() throws ParseException {
		int startPosition = _position;
		try {
			String value = readToNextWhiteSpaceOrEnd();
			return new BigDecimal(value);
		}
		catch (NumberFormatException e) {
			throw DirectiveError.asException(DirectiveError.Error.INVALID_REAL_NUMBER, startPosition-1);
		}
	}
	
	
	protected String readToNextWhiteSpaceOrEnd() throws ParseException {
		
		StringBuilder text = new StringBuilder();
		while (_currentInt >= 0 && !Character.isWhitespace(_currentChar)) {
			text.append(_currentChar);
			readNext();
		}
		
		return text.toString();
	}
	
	/**
	 * Reads from the stream up the next character of the specified type or until the
	 * end of the stream is reached.
	 * @param next the character to read up to.
	 * @return the contents of the stream up to (but not including) the supplied character.
	 * @throws Exception if there is an error reading from the stream.
	 */
	protected String readToNext(char next) throws ParseException {
		
		StringBuilder text = new StringBuilder();
		while (_currentInt >= 0 && _currentChar != next) {
			text.append(_currentChar);
			readNext();
		}
		
		return text.toString();
	}
	
	protected IntRange readIds(IntegerValidator validator) throws ParseException {
        int startPos = _position;
        int first = readInteger();
        validateId(first, validator, startPos);
        if (_currentChar == '-') {
            readNext();
            startPos = _position;
            int last = readInteger();
            validateId(last, validator, startPos);

            if (_validateRangeOrder && first > last) {
                throw DirectiveError.asException(DirectiveError.Error.RANGE_SEQUENCE_ERROR, startPos);
            }

            return new IntRange(first, last);
        }
        return new IntRange(first);
    }

    private void validateId(int id, IntegerValidator validator, int parsePosition) throws ParseException {
        if (validator != null) {
            DirectiveError result = validator.validateInteger(id);
            if (result != null) {
                result.setPosition(parsePosition);
                throw result.asException();
            }
        }
    }

	protected List<Integer> readSet(IntegerValidator validator) throws ParseException {
		List<Integer> values = new ArrayList<Integer>();
		while (_currentInt > 0 && !Character.isWhitespace(_currentChar)) {
            values.addAll(readSetComponent(validator));
		}
		
		return values;
	}

    /**
     * Reads a component of a set based directive, optionally starting with the SET_VALUE_SEPARATOR character.
     * @param validator validates the integers as they are read.
     * @return a List of integers read by this method.
     * @throws ParseException if the format of the set component is not as expected.
     */
    private List<Integer> readSetComponent(IntegerValidator validator) throws ParseException {
        List<Integer> values = new ArrayList<Integer>();
        if (_currentChar == SET_VALUE_SEPARATOR) {
            readNext();
        }
        IntRange ids = readIds(validator);
        for (int i : ids.toArray()) {
            values.add(i);
        }
        return values;
    }

    protected List<Integer> readSet(IntegerValidator validator, char setTerminatorChar) throws ParseException {
        List<Integer> values = new ArrayList<Integer>();
        while (_currentInt > 0 && (Character.isDigit(_currentChar) || _currentChar == SET_VALUE_SEPARATOR)) {
            values.addAll(readSetComponent(validator));
        }

        return values;
    }
	
	protected void readValueSeparator() throws ParseException {
		expect(VALUE_SEPARATOR);
		// consume the comma.
		readNext();
	}
	
	protected String readItemDescription() throws ParseException {
		expect(MARK_IDENTIFIER);
		
		readNext();
		char previousChar = (char)0;
		StringBuilder id = new StringBuilder();
		while (!(previousChar == '/' && (_currentChar == ' ' || _currentChar == '\r' || _currentChar == '\n'))) {
			id.append(_currentChar);
			previousChar = _currentChar;
			readNext();
		}
		// Delete the '/'
		if (id.charAt(id.length()-1) != '/') {
			throw DirectiveError.asException(DirectiveError.Error.ITEM_NAME_MISSING_SLASH, _position);
		}
		id.deleteCharAt(id.length()-1);
		
	    return id.toString().trim();
	}
	
	protected int readListId(IntegerValidator validator) throws ParseException {
		expect(MARK_IDENTIFIER);
		
		readNext();
		
		int id = readInteger();
		if (validator != null) {
            validator.validateInteger(id);
        }
		expect('.');
	    readNext();  // consume the . character.
	    return id;
	}
	
	
	protected void mark() throws ParseException {
		try {
			_reader.mark(2);
			_markedInt = _currentInt;
		}
		catch (IOException e) {
			throw DirectiveError.asException(DirectiveError.Error.FATAL_ERROR, _position);
		}
	}
	
	protected void reset() throws ParseException {
		try {
			_reader.reset();
			_currentChar = (char)_markedInt;
			_currentInt = _markedInt;
		}
		catch (IOException e) {
			throw DirectiveError.asException(DirectiveError.Error.FATAL_ERROR, _position);
		}
	}
}
