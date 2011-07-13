package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;

/**
 * The IdValueList parses directive arguments in the form:
 * 
 * id1 id2 idn
 * where idx is a number or range of numbers.
 * 
 * This argument format is used by directives such as: EXCLUDE ITEMS and
 * INCLUDE CHARACTERS.
 * 
 */
public class IdListParser extends DirectiveArgsParser {
	
	public IdListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			IntRange ids = readIds();
			
			for (int id : ids.toArray()) {
				_args.addDirectiveArgument(id);
			}
			
			skipWhitespace();
		}
	}
	
}
