package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.naturallanguage.ImplicitValuesTranslator;

/**
 * Handles the TRANSLATE IMPLICT VALUES directive.
 * @see http://delta-intkey.com/www/uguide.htm#_*INSERT_IMPLICIT_VALUES
 */
public class TranslateImplicitValues extends AbstractNoArgDirective {

	DataSetTranslatorFactory factory;
	
	public TranslateImplicitValues() {
		super("translate", "implicit", "values");
		factory = new DataSetTranslatorFactory();
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments data) throws Exception {
		ImplicitValuesTranslator translator = factory.createImplicitValuesTranslator(context);
		translator.translateImplicitValues();
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}
