package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.delta.DeltaFormatDataSetFilter;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;
import au.org.ala.delta.translation.dist.DistTranslator;
import au.org.ala.delta.translation.intkey.IntkeyTranslator;
import au.org.ala.delta.translation.key.KeyTranslator;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageTranslator;
import au.org.ala.delta.translation.print.CharacterListPrinter;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.translation.print.PrintAction;


/**
 * Creates appropriate instances of DataSetTranslator for the supplied DeltaContext.
 */
public class DataSetTranslatorFactory {
	
	public DataSetTranslator createTranslator(DeltaContext context) {
		return createTranslator(context, context.getPrintFile());
	}
	
	public DataSetTranslator createTranslator(DeltaContext context, PrintFile printFile) {
		
		DataSetTranslator translator = null;
		TranslateType translation = context.getTranslateType();
		
		if (translation == null) {
			return new NullTranslator();
		}
		
		FormatterFactory formatterFactory = new FormatterFactory(context);
		
		if (translation.equals(TranslateType.NaturalLanguage) && context.getOutputHtml() == false) {
			translator = createNaturalLanguageTranslator(context, printFile, formatterFactory);
		}
		else if (translation.equals(TranslateType.Delta)) {
			translator = createDeltaFormatTranslator(context, printFile, formatterFactory);
		}
		else if (translation.equals(TranslateType.IntKey)) {
			translator = createIntkeyFormatTranslator(context, formatterFactory);
		}
		else if (translation.equals(TranslateType.Key)) {
			translator = createKeyFormatTranslator(context, formatterFactory);
		}
		else if (translation.equals(TranslateType.Dist)) {
			translator = createDistFormatTranslator(context, formatterFactory);
		}
		else {
			throw new RuntimeException("(Currently) unsupported translation type: "+translation);
		}
		return translator;
	}
	
	

	private DataSetTranslator createIntkeyFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new IntkeyTranslator(context, dataSet, formatterFactory.createCharacterFormatter());
	}
	
	private DataSetTranslator createKeyFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		NaturalLanguageTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, null);
		
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new KeyTranslator(context, dataSet,
				formatterFactory.createItemFormatter(typeSetter), 
				formatterFactory.createCharacterFormatter(),
				formatterFactory);
	}
	
	private DataSetTranslator createDistFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		NaturalLanguageTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, null);
		
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new DistTranslator(context, dataSet,
				formatterFactory.createItemFormatter(typeSetter, CommentStrippingMode.STRIP_ALL));
	}

	private AbstractDataSetTranslator createNaturalLanguageTranslator(
			DeltaContext context, PrintFile printer, FormatterFactory formatterFactory) {
		AbstractDataSetTranslator translator;
		NaturalLanguageTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter);
		CharacterFormatter characterFormatter = formatterFactory.createCharacterFormatter();
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		translator = new NaturalLanguageTranslator(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
		return translator;
	}
	
	private AbstractDataSetTranslator createDeltaFormatTranslator(
			DeltaContext context, PrintFile printer, FormatterFactory formatterFactory) {
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(null);
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter();
		CharacterListTypeSetter typeSetter = new au.org.ala.delta.translation.print.PlainTextTypeSetter(printer);
		return new DeltaFormatTranslator(context, printer, itemFormatter, charFormatter, typeSetter);
	}
	
	public PrintAction createPrintAction(DeltaContext context, PrintActionType printAction) {
		PrintAction action = null;
		switch (printAction) {
		case PRINT_CHARACTER_LIST:
			action = createCharacterListPrinter(context);
			break;
		default:
			throw new UnsupportedOperationException(printAction+" is not yet implemented.");	
		}
		return action;
	}
	
	private PrintAction createCharacterListPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		CommentStrippingMode mode = CommentStrippingMode.RETAIN;
		if (context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}
		CharacterFormatter charFormatter  = formatterFactory.createCharacterFormatter(true, true, mode);
		CharacterListTypeSetter typeSetter = new TypeSetterFactory().createCharacterListTypeSetter(context, printer);
		
		return new CharacterListPrinter(context, printer, charFormatter, typeSetter);
	}
	
}
