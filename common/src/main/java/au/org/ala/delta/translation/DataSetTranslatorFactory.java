package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;
import au.org.ala.delta.translation.intkey.IntkeyTranslator;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageTranslator;


/**
 * Creates appropriate instances of DataSetTranslator for the supplied DeltaContext.
 */
public class DataSetTranslatorFactory {
	
	public DataSetTranslator createTranslator(DeltaContext context) {
		return createTranslator(context, createPrinter(context));
	}
	
	public DataSetTranslator createTranslator(DeltaContext context, Printer printer) {
		
		DataSetTranslator translator = null;
		TranslateType translation = context.getTranslateType();
		
		if (translation.equals(TranslateType.NaturalLanguage) && context.getOutputHtml() == false) {
			translator = createNaturalLanguageTranslator(context, printer);
		}
		else if (translation.equals(TranslateType.Delta)) {
			translator = createDeltaFormatTranslator(context, printer);
		}
		else if (translation.equals(TranslateType.IntKey)) {
			translator = createIntkeyFormatTranslator(context);
		}
		else {
			throw new RuntimeException("(Currently) unsupported translation type: "+translation);
		}
		return translator;
	}
	
	

	private DataSetTranslator createIntkeyFormatTranslator(DeltaContext context) {
		return new IntkeyTranslator(context);
	}

	private AbstractDataSetTranslator createNaturalLanguageTranslator(
			DeltaContext context, Printer printer) {
		AbstractDataSetTranslator translator;
		TypeSetter typeSetter = createTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = createItemFormatter(context, typeSetter);
		CharacterFormatter characterFormatter = createCharacterFormatter(context);
		AttributeFormatter attributeFormatter = createAttributeFormatter(context, typeSetter);
		translator = new NaturalLanguageTranslator(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
		return translator;
	}
	
	private AbstractDataSetTranslator createDeltaFormatTranslator(DeltaContext context, Printer printer) {
		ItemFormatter itemFormatter  = createItemFormatter(context, null);
		return new DeltaFormatTranslator(context, printer, itemFormatter);
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
	
	private ItemFormatter createItemFormatter(DeltaContext context, TypeSetter typeSetter) {
		if (context.isOmitTypeSettingMarks()) {
			return new ItemFormatter(false, false, AngleBracketHandlingMode.RETAIN, true, false);
		}
		else if (typeSetter == null) {
			return new ItemFormatter(false, false, AngleBracketHandlingMode.RETAIN, false, false);
		}
		else {
			return new TypeSettingItemFormatter(typeSetter);
		}
	}
	
	private CharacterFormatter createCharacterFormatter(DeltaContext context) {
		return new CharacterFormatter(false, true, AngleBracketHandlingMode.RETAIN, context.isOmitTypeSettingMarks());
	}
	
	private AttributeFormatter createAttributeFormatter(DeltaContext context, TypeSetter typeSetter) {
		if (context.isOmitTypeSettingMarks()) {
			return new AttributeFormatter(false, true, false);
		}
		else {
			return new TypeSettingAttributeFormatter();
		}
	}
}
