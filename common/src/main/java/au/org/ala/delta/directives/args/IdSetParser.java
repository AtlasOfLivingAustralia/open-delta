package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.AbstractDeltaContext;

/**
 * The IdSetParser parses sets of linked values, for example
 * the LINKED CHARACTERS directive.
 */
public class IdSetParser extends DirectiveArgsParser {
	
	public static final char SET_VALUE_SEPARATOR = ':';
	
	public IdSetParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			readSet();
			
			skipWhitespace();
		}
		
	}
	
	private void readSet() throws ParseException {
		List<Integer> values = new ArrayList<Integer>();
		while (_currentInt > 0 && !Character.isWhitespace(_currentChar)) {
			if (_currentChar == SET_VALUE_SEPARATOR) {
				readNext();
			}
			IntRange ids = readIds();
			for (int i : ids.toArray()) {
				values.add(i);
			}
		}
		
		if (values.size() > 0) {
			_args.addDirectiveArgument(values);
		}
	}
}
