package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;

/**
 * Indicates that implicit attributes should be included in natural language 
 * translations.
 * 
 * @see http://delta-intkey.com/www/uguide.htm#_*INSERT_IMPLICIT_VALUES
 */
public class InsertImplicitValues extends ConforDirective {

	public InsertImplicitValues() {
		super("insert", "implicit", "values");
	}
	
	@Override
	protected void doProcess(DeltaContext context, String data)
			throws Exception {
		
		context.setInsertImplicitValues(true);
	}

	@Override
	protected boolean ignoreEmptyArguments() {
		return false;
	}
}
