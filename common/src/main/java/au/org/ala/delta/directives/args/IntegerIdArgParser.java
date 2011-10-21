package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;

/**
 * The IntegerIdArgParser parses directive arguments in the form:
 * 
 * id
 * where id is a number.
 * 
 * This argument format is used by directives such as: 
 * CHARACTER FOR TAXON NAMES and STOP AFTER ITEM.
 * 
 */
public class IntegerIdArgParser extends DirectiveArgsParser {

	public IntegerIdArgParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		try {
			Integer id = Integer.parseInt(readFully().trim());
			_args.addDirectiveArgument(id);
		}
		catch (Exception e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}
}
