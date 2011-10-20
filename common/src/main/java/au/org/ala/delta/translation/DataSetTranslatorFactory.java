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
import au.org.ala.delta.translation.key.KeyStateTranslator;
import au.org.ala.delta.translation.key.KeyTranslator;
import au.org.ala.delta.translation.naturallanguage.HtmlNaturalLanguageTranslator;
import au.org.ala.delta.translation.naturallanguage.IndexWriter;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageDataSetFilter;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageTranslator;
import au.org.ala.delta.translation.nexus.NexusDataSetFilter;
import au.org.ala.delta.translation.nexus.NexusTranslator;
import au.org.ala.delta.translation.print.CharacterListPrinter;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.translation.print.ItemDescriptionsPrinter;
import au.org.ala.delta.translation.print.ItemNamesPrinter;
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
		
		if (translation.equals(TranslateType.NaturalLanguage)) {
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
		else if (translation.equals(TranslateType.NexusFormat)) {
			translator = createNexusFormatTranslator(context, printFile, formatterFactory);
		}
		else {
			throw new RuntimeException("(Currently) unsupported translation type: "+translation);
		}
		return translator;
	}
	
	

	private DataSetTranslator createNexusFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		FilteredDataSet dataSet = new FilteredDataSet(context, new NexusDataSetFilter(context));
		KeyStateTranslator keyStateTranslator = new KeyStateTranslator(formatterFactory);
		return new NexusTranslator(context, dataSet, printFile, keyStateTranslator, charFormatter, itemFormatter);
	}

	private DataSetTranslator createIntkeyFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new IntkeyTranslator(context, dataSet, formatterFactory.createCharacterFormatter());
	}
	
	private DataSetTranslator createKeyFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, null);
		
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new KeyTranslator(context, dataSet,
				formatterFactory.createItemFormatter(typeSetter), 
				formatterFactory.createCharacterFormatter(),
				formatterFactory);
	}
	
	private DataSetTranslator createDistFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, null);
		
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new DistTranslator(context, dataSet,
				formatterFactory.createItemFormatter(typeSetter, CommentStrippingMode.STRIP_ALL, false));
	}

	private AbstractDataSetTranslator createNaturalLanguageTranslator(
			DeltaContext context, PrintFile printer, FormatterFactory formatterFactory) {
		AbstractDataSetTranslator translator;
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter);
		CharacterFormatter characterFormatter = formatterFactory.createCharacterFormatter();
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		DataSetFilter filter = new NaturalLanguageDataSetFilter(context);
		
		if (context.getOutputHtml() == false) {
			translator = new NaturalLanguageTranslator(context, filter, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
		}
		else {
			PrintFile indexFile = context.getOutputFileSelector().getIndexFile();
			IndexWriter indexWriter = new IndexWriter(indexFile, itemFormatter, context);
			translator = new HtmlNaturalLanguageTranslator(
					context, filter, typeSetter, printer, itemFormatter,
					characterFormatter, attributeFormatter, indexWriter);
		}
		return translator;
	}
	
	private AbstractDataSetTranslator createDeltaFormatTranslator(
			DeltaContext context, PrintFile printer, FormatterFactory formatterFactory) {
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(null);
		itemFormatter.setDespaceRtf(true);
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(true, false, CommentStrippingMode.RETAIN);
		charFormatter.setDespaceRtf(true);
		CharacterListTypeSetter typeSetter = new au.org.ala.delta.translation.print.PlainTextTypeSetter(printer);
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		return new DeltaFormatTranslator(context, filter, printer, itemFormatter, charFormatter, typeSetter);
	}
	
	public PrintAction createPrintAction(DeltaContext context, PrintActionType printAction) {
		PrintAction action = null;
		switch (printAction) {
		case PRINT_CHARACTER_LIST:
			action = createCharacterListPrinter(context);
			break;
		case PRINT_ITEM_NAMES:
			action = createItemNamesPrinter(context);
			break;
		case PRINT_ITEM_DESCRIPTIONS:
			action = createItemDescriptionsPrinter(context);
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
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		return new CharacterListPrinter(context, filter, printer, charFormatter, typeSetter);
	}
	
	private PrintAction createItemNamesPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, true);
		
		return new ItemNamesPrinter(context, new DeltaFormatDataSetFilter(context), itemFormatter, printer, typeSetter);
	}
	
	private PrintAction createItemDescriptionsPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		return new ItemDescriptionsPrinter(context, filter, printer, itemFormatter, attributeFormatter, typeSetter);
	}
	
}
