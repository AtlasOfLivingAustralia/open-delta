package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Handles the TRANSLATE IMPLICT VALUES directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*INSERT_IMPLICIT_VALUES
 */
public class TranslateImplicitValues extends AbstractNoArgDirective {

	
	public TranslateImplicitValues() {
		super("translate", "implicit", "values");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments data) throws Exception {
		context.setTranslateImplicitValues(true);
	}
}
