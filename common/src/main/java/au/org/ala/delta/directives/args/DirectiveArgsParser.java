package au.org.ala.delta.directives.args;

import java.io.Reader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractStreamParser;

public abstract class DirectiveArgsParser extends AbstractStreamParser {

	protected DirectiveArguments _args;
	
	public DirectiveArgsParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}
	
}
