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
package au.org.ala.delta.directives;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;

public abstract class AbstractStreamParser {

	// For now we use just the period for decimal numbers. In the future we may need to examine how locale should affect this.
	protected static final char DECIMAL_SEPARATOR = '.';

	protected Reader _reader;
	protected AbstractDeltaContext _context;
	protected char _currentChar;
	protected int _currentInt;
	protected int _position;
	

	public AbstractStreamParser(AbstractDeltaContext context, Reader reader) {
		_reader = reader;
		_context = context;
	}

	public abstract void parse() throws ParseException;

	protected AbstractDeltaContext getContext() {
		return _context;
	}

	protected char getCurrentChar() {
		return _currentChar;
	}

	// protected static final String WHITESPACE = " \t\r\n";

	protected int readNext() throws ParseException {
		try {
			_currentInt = _reader.read();
			_position++;
			_currentChar = (char) _currentInt;
			return _currentInt;
		}
		catch (IOException e) {
			throw new ParseException("Failed to read next char. "+e.getMessage(), _position);
		}
	}

	protected boolean skipWhitespace() throws ParseException {

		while (_currentInt > 0 && isWhiteSpace(_currentChar)) {
			readNext();
		}
		return _currentInt > 0;

	}

	protected boolean skipTo(char find) throws ParseException {
		if (_currentChar == find) {
			return true;
		}

		while (_currentInt != find && _currentInt >= 0) {
			readNext();
		}
		return _currentInt == find;
	}

	protected String readToNextEndSlashSpace() throws ParseException {
		StringBuilder b = new StringBuilder();

		char prev = 0;

		boolean finished = false;
		while (!finished && _currentInt >= 0) {
			if ((isWhiteSpace(_currentChar)) && prev == '/') {
				finished = true;
			} else {
				b.append(_currentChar);
				prev = _currentChar;
				readNext();
				if (_currentInt < 0) {
					finished = true;
				}
			}
		}

		String result = b.toString().trim();
		return (result.endsWith("/") ? result.substring(0, result.length() - 1) : result);
	}

	protected int readInteger() throws ParseException {
		StringBuilder b = new StringBuilder();
		while (Character.isDigit(_currentChar)) {
			b.append(_currentChar);
			readNext();
		}
		if (b.length() == 0) {
			throw new ParseException("Expected a number, got '" + _currentChar + "'", _position-1);
		}

		return Integer.parseInt(b.toString());
	}
	
	protected BigDecimal readReal() throws ParseException {
		int position = _position;
		StringBuilder b = new StringBuilder();
		while (Character.isDigit(_currentChar) || _currentChar == DECIMAL_SEPARATOR) {
			b.append(_currentChar);
			readNext();
		}
		if (b.length() == 0) {
			throw new ParseException("Expected a number, got '" + _currentChar + "'", _position-1);
		}
		BigDecimal real = null;
		try {
			real = new BigDecimal(b.toString());
		}
		catch (NumberFormatException e) {
			throw new ParseException("Expected real number, got: "+b.toString(), position);
		}
		return real;
		
	}

	protected String readComment() throws ParseException {
		assert _currentChar == '<';
		
		StringBuilder b = new StringBuilder("" + _currentChar);
		int commentNestLevel = 1;
		readNext();
		while (_currentInt >= 0 && commentNestLevel > 0) {
			switch (_currentChar) {
				case '>':
					commentNestLevel--;
					break;
				case '<':
					commentNestLevel++;
					break;
				default:
			}
			if (_currentInt >= 0) {
				b.append(_currentChar);
			}
			readNext();
		}

		return b.toString();
	}

	protected boolean isWhiteSpace(char ch) {
		return Character.isWhitespace(ch);
	}

	protected String readToNextSpaceComments() throws ParseException {
		StringBuilder b = new StringBuilder(_currentChar);
		int commentNestLevel = 0;
		
		while (_currentInt >= 0 && (commentNestLevel > 0 || !isWhiteSpace(_currentChar))) {
			switch (_currentChar) {
				case '>':
					commentNestLevel--;
					break;
				case '<':
					commentNestLevel++;
					break;
				default:
			}
			if (_currentInt >= 0) {
				b.append(_currentChar);
			}
			readNext();
		}

		return b.toString();

	}
	

	/**
	 * Character descriptions can span multiple lines and have whitespace designed to keep the
	 * chars file format looking nice.  This method turns any sequence of whitespace (including
	 * newlines) into a single space.
	 * @param description the description has read from the chars file.
	 * @return the description with whitespace tidied up.
	 */
	protected String cleanWhiteSpace(String description) {
		return description.replaceAll("\\s+", " ");
	}
}
