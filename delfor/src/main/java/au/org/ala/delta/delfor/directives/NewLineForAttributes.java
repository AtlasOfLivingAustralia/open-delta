package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.directives.AbstractNoArgDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class NewLineForAttributes extends AbstractNoArgDirective {

	public NewLineForAttributes() {
		super("new", "line", "for", "attributes");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		((DelforContext)context).newLineForAttributes();
	}

}
