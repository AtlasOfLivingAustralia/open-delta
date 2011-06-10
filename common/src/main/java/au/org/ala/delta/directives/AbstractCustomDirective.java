package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;


public abstract class AbstractCustomDirective extends AbstractDirective<DeltaContext> {

	protected DirectiveArguments _args;
	
	protected AbstractCustomDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		
		StringReader reader = new StringReader(data);
		DirectiveArgsParser parser = createParser(context, reader);
		
		try {
			parser.parse();
		}
		catch(Exception e) {
			if (e instanceof ParseException) {
				throw (ParseException)e;
			}
			else {
				throw new RuntimeException(e);
			}
		}
		
		_args = parser.getDirectiveArgs();
	}
	
	protected abstract DirectiveArgsParser createParser(DeltaContext context, StringReader reader);
}
