package au.org.ala.delta.directives;

import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;


public class ApplicableCharacters extends AbstractDirective<DeltaContext> {

	private DirectiveArguments _args;
	
	public ApplicableCharacters() {
		super("applicable", "characters");
	}
	
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args = new DirectiveArguments();
		_args.addTextArgument(data);
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		
		
	}

	
	
	
}
