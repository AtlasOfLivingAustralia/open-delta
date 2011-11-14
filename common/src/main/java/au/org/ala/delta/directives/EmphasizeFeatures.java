package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;

/**
 * Processes the EMPHASIZE FEATURES directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*EMPHASIZE_FEATURES_
 */
public class EmphasizeFeatures extends AbstractCustomDirective {
	public EmphasizeFeatures() {
		super("emphasize", "features");
	}

	@Override
	protected DirectiveArgsParser createParser(DeltaContext context,
			StringReader reader) {
		return new IdListParser(context, reader);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			context.emphasizeFeature((Integer)arg.getId());
		}
		
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
