package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.directives.AbstractDeltaContext;

/**
 * The IdSetParser parses sets of linked values, for example
 * the LINKED CHARACTERS directive.
 */
public class IdSetParser extends DirectiveArgsParser {
	
	public IdSetParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			addSet();
			
			skipWhitespace();
		}
		
	}
	
	private void addSet() throws ParseException {
		List<Integer> values = readSet();
		
		if (values.size() > 0) {
			_args.addDirectiveArgument(values);
		}
	}
}
