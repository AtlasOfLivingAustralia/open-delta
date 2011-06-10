package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Indicates that implicit attributes should be included in natural language 
 * translations.
 * 
 * @see http://delta-intkey.com/www/uguide.htm#_*INSERT_IMPLICIT_VALUES
 */
public class InsertImplicitValues extends AbstractNoArgDirective {

	public InsertImplicitValues() {
		super("insert", "implicit", "values");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments data) throws Exception {
		context.setInsertImplicitValues(true);
	}
}
