package au.org.ala.delta.translation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.attribute.AttributeTranslatorFactory;
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
import au.org.ala.delta.translation.paup.PaupTranslator;
import au.org.ala.delta.translation.print.CharacterListPrinter;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.translation.print.ItemDescriptionsPrinter;
import au.org.ala.delta.translation.print.ItemNamesPrinter;
import au.org.ala.delta.translation.print.UncodedCharactersFilter;
import au.org.ala.delta.translation.print.UncodedCharactersPrinter;
import au.org.ala.delta.translation.print.UncodedCharactersTranslator;


/**
 * Creates appropriate instances of DataSetTranslator for the supplied DeltaContext.
 */
public class DataSetTranslatorFactory {
	
	public DataSetTranslator createTranslator(DeltaContext context) {
		
		DataSetTranslator translator = null;
		TranslateType translation = context.getTranslateType();
		
		FormatterFactory formatterFactory = new FormatterFactory(context);
		
		if (translation == null) {
			translator = new NullTranslator();
		}
		else if (translation.equals(TranslateType.NaturalLanguage)) {
			translator = createNaturalLanguageTranslator(context, context.getPrintFile(), formatterFactory);
		}
		else if (translation.equals(TranslateType.Delta)) {
			translator = createDeltaFormatTranslator(context, context.getOutputFileSelector().getOutputFile(), formatterFactory);
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
			translator = createNexusFormatTranslator(context,  context.getOutputFileSelector().getOutputFile(), formatterFactory);
		}
		else if (translation.equals(TranslateType.PAUP)) {
			translator = createPaupFormatTranslator(context,  context.getOutputFileSelector().getOutputFile(), formatterFactory);
		}
		else {
			throw new RuntimeException("(Currently) unsupported translation type: "+translation);
		}
		
		List<DataSetTranslator> translators = new ArrayList<DataSetTranslator>();
		translators.add(translator);
		
		for (PrintActionType action : context.getPrintActions()) {
			translators.add(createPrintAction(context, action));
		}
		
		return new CompositeDataSetTranslator(translators);
	}
	
	

	private DataSetTranslator createNexusFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		FilteredDataSet dataSet = new FilteredDataSet(context, new NexusDataSetFilter(context));
		AttributeTranslatorFactory attributeTranslatorFactory = new AttributeTranslatorFactory(
				context, 
				charFormatter,
				formatterFactory.createAttributeFormatter(),
				null);
		KeyStateTranslator keyStateTranslator = new KeyStateTranslator(attributeTranslatorFactory);
		return new NexusTranslator(context, dataSet, printFile, keyStateTranslator, charFormatter, itemFormatter);
	}
	
	private DataSetTranslator createPaupFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		FilteredDataSet dataSet = new FilteredDataSet(context, new NexusDataSetFilter(context));
		return new PaupTranslator(context, dataSet, printFile, charFormatter, itemFormatter);
	}

	private DataSetTranslator createIntkeyFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		return new IntkeyTranslator(context, dataSet, formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN));
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
		IterativeTranslator translator;
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter);
		CharacterFormatter characterFormatter = formatterFactory.createCharacterFormatter();
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		DataSetFilter filter = new NaturalLanguageDataSetFilter(context);
		
		if (context.getOutputHtml() == false) {
			translator = new NaturalLanguageTranslator(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
		}
		else {
			PrintFile indexFile = context.getOutputFileSelector().getIndexFile();
			IndexWriter indexWriter = new IndexWriter(indexFile, itemFormatter, context);
			translator = new HtmlNaturalLanguageTranslator(
					context, typeSetter, printer, itemFormatter,
					characterFormatter, attributeFormatter, indexWriter);
		}
		return wrap(context, filter, translator);
	}
	
	private AbstractDataSetTranslator createDeltaFormatTranslator(
			DeltaContext context, PrintFile printer, FormatterFactory formatterFactory) {
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(null);
		itemFormatter.setDespaceRtf(true);
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(true, false, CommentStrippingMode.RETAIN);
		charFormatter.setDespaceRtf(true);
		CharacterListTypeSetter typeSetter = new au.org.ala.delta.translation.print.PlainTextTypeSetter(printer);
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		return wrap(context, filter,new DeltaFormatTranslator(context, printer, itemFormatter, charFormatter, typeSetter));
	}
	
	public DataSetTranslator createPrintAction(DeltaContext context, PrintActionType printAction) {
		DataSetTranslator action = null;
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
		case PRINT_UNCODED_CHARACTERS:
			action = createUncodedCharactersPrinter(context);
			break;	
		case TRANSLATE_UNCODED_CHARACTERS:
			action = createUncodedCharactersTranslator(context);
			break;	
		default:
			throw new UnsupportedOperationException(printAction+" is not yet implemented.");	
		}
		return action;
	}
	
	private DataSetTranslator createCharacterListPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		CommentStrippingMode mode = CommentStrippingMode.RETAIN;
		if (context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}
		CharacterFormatter charFormatter  = formatterFactory.createCharacterFormatter(true, true, mode);
		CharacterListTypeSetter typeSetter = new TypeSetterFactory().createCharacterListTypeSetter(context, printer);
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		return wrap(context, filter,new CharacterListPrinter(context, printer, charFormatter, typeSetter));
	}
	
	private DataSetTranslator createItemNamesPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, true);
		
		return wrap(context, new DeltaFormatDataSetFilter(context),new ItemNamesPrinter(context, itemFormatter, printer, typeSetter));
	}
	
	private DataSetTranslator createItemDescriptionsPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		return wrap(context, filter,new ItemDescriptionsPrinter(context, printer, itemFormatter, attributeFormatter, typeSetter));
	}
	
	private DataSetTranslator createUncodedCharactersPrinter(DeltaContext context) {
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer, true);
		FormatterFactory formatterFactory = new FormatterFactory(context);
		DataSetFilter filter = new UncodedCharactersFilter(context);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		
		return wrap(context, filter, new UncodedCharactersPrinter(context, printer, itemFormatter, typeSetter));
	}
	
	private DataSetTranslator createUncodedCharactersTranslator(DeltaContext context) {
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer, true);
		FormatterFactory formatterFactory = new FormatterFactory(context);
		DataSetFilter filter = new UncodedCharactersFilter(context);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		CharacterFormatter characterFormatter = formatterFactory.createCharacterFormatter(true, true, CommentStrippingMode.RETAIN);
		return wrap(context, filter, new UncodedCharactersTranslator(context, printer, itemFormatter, characterFormatter, typeSetter));
	}
	
	private AbstractDataSetTranslator wrap(DeltaContext context, DataSetFilter filter, IterativeTranslator translator) {
		return new AbstractDataSetTranslator(context, filter, translator);
	}
	
}
