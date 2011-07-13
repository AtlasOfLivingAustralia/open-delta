package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;

public abstract class NumericArgParser extends DirectiveArgsParser {

	public NumericArgParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		BigDecimal value;
		try {
			value = new BigDecimal(readToNextEndSlashSpace());
			_args.addValueArgument(value);
		}
		catch (Exception e) {
			throw new ParseException(e.getMessage(), 0);
		}
		
	}
	
	protected abstract void createArgs(BigDecimal value);
}
