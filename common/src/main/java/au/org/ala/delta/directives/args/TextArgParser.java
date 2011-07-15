package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.directives.AbstractDeltaContext;

/**
 * The TextArgParser reads the directive argument as a single String.
 */
public class TextArgParser extends DirectiveArgsParser {

	public TextArgParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		
		String text;
		try {
			text = readFully();
			_args.addTextArgument(text);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage(), 0);
		}
		
	}
}
