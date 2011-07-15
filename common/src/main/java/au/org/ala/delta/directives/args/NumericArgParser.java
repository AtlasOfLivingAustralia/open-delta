package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;

import au.org.ala.delta.directives.AbstractDeltaContext;

public class NumericArgParser extends DirectiveArgsParser {

	public NumericArgParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		BigDecimal value;
		try {
			value = new BigDecimal(readFully());
			_args.addValueArgument(value);
		}
		catch (Exception e) {
			throw new ParseException(e.getMessage(), 0);
		}
		
	}
}
