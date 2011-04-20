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
		Printer printer = createPrinter(context);
		
		if (translation.equals(TranslateType.NaturalLanguage) && context.getOutputHtml() == false) {
		
			TypeSetter typeSetter = createTypeSetter(context, printer);
			translator = new NaturalLanguageTranslator(context, typeSetter, printer);
		}
		else {
			throw new RuntimeException("Only natural language without typesetting is currently supported.");
		}
		return translator;
	}
	
	/**
	 * Creates a Printer configured from the supplied context.
	 * @param context the context in which the translation will run.
	 * @return a new Printer instance.
	 */
	private Printer createPrinter(DeltaContext context) {
		int printWidth = context.getPrintWidth();
		
		return new Printer(context.getPrintStream(), printWidth);
	}
	
	/**
	 * Creates the appropriate TypeSetter for the supplied context.  If the TYPESETTING MARKS
	 * directive has been specified a FormattedTypeSetter will be returned. Otherwise a
	 * PlainTextTypeSetter will be returned.
	 * @param context the context in which the translator will run.
	 * @param printer used for outputting to the print file.
	 * @return a new instance of TypeSetter.
	 */
	private TypeSetter createTypeSetter(DeltaContext context, Printer printer) {
		
		if (context.getTypeSettingMarks().isEmpty()) {
			return new PlainTextTypeSetter(printer);
		}
		else {
			return new FormattedTextTypeSetter(context.getTypeSettingMarks(), printer);
		}
		
	}
}
