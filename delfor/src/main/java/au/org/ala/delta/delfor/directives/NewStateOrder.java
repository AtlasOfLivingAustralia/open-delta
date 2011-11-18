package au.org.ala.delta.delfor.directives;

import java.text.ParseException;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the NEW STATE ORDER directive.
 */
public class NewStateOrder extends AbstractDirective<DelforContext> {

	public NewStateOrder() {
		super("new", "state", "order");
	}
	
	
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void parse(DelforContext context, String data) throws ParseException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void process(DelforContext context, DirectiveArguments directiveArguments) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getArgType() {
	     return DirectiveArgType.DIRARG_OTHER;
	}

	@Override
	public int getOrder() {
		return 4;
	}
}
