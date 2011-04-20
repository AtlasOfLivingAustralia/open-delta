package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.TranslateType;


/**
 * Creates appropriate instances of DataSetTranslator for the supplied DeltaContext.
 */
public class DataSetTranslatorFactory {

	public AbstractDataSetTranslator createTranslator(DeltaContext context) {
		
		AbstractDataSetTranslator translator = null;
		TranslateType translation = context.getTranslateType();
		Printer typeSetter = createTypeSetter(context);
		if (translation.equals(TranslateType.NaturalLanguage) && context.getOutputHtml() == false) {
			
			translator = new NaturalLanguageTranslator(context, typeSetter);
		}
		else {
			throw new RuntimeException("Only natural language without typesetting is currently supported.");
		}
		return translator;
	}
	
	private Printer createTypeSetter(DeltaContext context) {
		int printWidth = context.getPrintWidth();
		
		return new Printer(context.getPrintStream(), printWidth);
	}
}
