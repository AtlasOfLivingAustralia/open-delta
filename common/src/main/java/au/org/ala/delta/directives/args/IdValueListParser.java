package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;

/**
 * The IdValueList parses directive arguments in the form:
 * 
 * id1,value1 id2,value2 idn,valuen
 * where idx is a number or range of numbers.
 * 
 * This argument format is used by directives such as: CHARACTER WEIGHTS and
 * DECIMAL PLACES.
 * 
 */
public class IdValueListParser extends DirectiveArgsParser {
	
	public IdValueListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			IntRange ids = readIds();
			readValueSeparator();
			BigDecimal value = readValue();
			
			for (int id : ids.toArray()) {
				_args.addDirectiveArgument(id, value);
			}
			
			skipWhitespace();
		}
	}
	
}
