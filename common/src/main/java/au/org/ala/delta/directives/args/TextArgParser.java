package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;

public class TextArgParser extends DirectiveArgsParser {

	public TextArgParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		String text;
		try {
			text = readToNextEndSlashSpace();
			_args.addTextArgument(text);
		}
		catch (Exception e) {
			throw new ParseException(e.getMessage(), 0);
		}
		
	}
}
