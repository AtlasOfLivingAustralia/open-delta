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

import java.io.Reader;

import au.org.ala.delta.DeltaContext;

public abstract class AbstractStreamParser {

	private Reader _reader;
	protected DeltaContext _context;
	protected char _currentChar;
	protected int _currentInt;

	public AbstractStreamParser(DeltaContext context, Reader reader) {
		_reader = reader;
		_context = context;
	}

	public abstract void parse() throws Exception;

	protected DeltaContext getContext() {
		return _context;
	}

	protected char getCurrentChar() {
		return _currentChar;
	}

	// protected static final String WHITESPACE = " \t\r\n";

	protected int readNext() throws Exception {
		_currentInt = _reader.read();
		_currentChar = (char) _currentInt;
		return _currentInt;
	}

	protected boolean skipWhitespace() throws Exception {

		while (_currentInt > 0 && isWhiteSpace(_currentChar)) {
			readNext();
		}
		return _currentInt > 0;

	}

	protected boolean skipTo(char find) throws Exception {
		if (_currentChar == find) {
			return true;
		}

		while (_currentInt != find && _currentInt >= 0) {
			readNext();
		}
		return _currentInt == find;
	}

	protected String readToNextEndSlashSpace() throws Exception {
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

	protected int readInteger() throws Exception {
		StringBuilder b = new StringBuilder();
		while (Character.isDigit(_currentChar)) {
			b.append(_currentChar);
			readNext();
		}
		if (b.length() == 0) {
			throw new RuntimeException("Expected a number, got '" + _currentChar + "'");
		}

		return Integer.parseInt(b.toString());
	}

	protected String readComment() throws Exception {
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

	protected String readToNextSpaceComments() throws Exception {
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

}
