package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractStreamParser;

/**
 * The DirectiveArgsParser provides methods for parsing common formats
 * used in DELTA directives.
 */
public abstract class DirectiveArgsParser extends AbstractStreamParser {

	protected DirectiveArguments _args;
	
	public DirectiveArgsParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}
	
	protected String readFully() throws Exception {
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
			throw new ParseException("Invalid character found.", _position-1);
		}
	}
	
	protected BigDecimal readValue() throws ParseException {
		int startPosition = _position;
		try {
			String value = readToNextWhiteSpaceOrEnd();
			return new BigDecimal(value);
		}
		catch (Exception e) {
			throw new ParseException("Failed to read value: "+e.getMessage(), startPosition-1);
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

	protected IntRange readIds() throws ParseException {
		try {
			int first = readInteger();
			if (_currentChar == '-') {
				readNext();
				int last = readInteger();
				return new IntRange(first, last);
			}
			return new IntRange(first);
		}
		catch (Exception e) {
			throw new ParseException(e.getMessage(), _position-1);
		}
	}
	
	protected void readComma() throws ParseException {
		expect(',');
		// consume the comma.
		readNext();
	}
	
}
