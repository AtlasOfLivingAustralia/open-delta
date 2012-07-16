/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.PrintActionType;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.attribute.AttributeTranslatorFactory;
import au.org.ala.delta.translation.delta.DeltaFormatDataSetFilter;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;
import au.org.ala.delta.translation.dist.DistTranslator;
import au.org.ala.delta.translation.henning86.Henning86Translator;
import au.org.ala.delta.translation.intkey.IntkeyTranslator;
import au.org.ala.delta.translation.key.KeyStateTranslator;
import au.org.ala.delta.translation.key.KeyTranslator;
import au.org.ala.delta.translation.naturallanguage.HtmlNaturalLanguageTranslator;
import au.org.ala.delta.translation.naturallanguage.ImplicitValuesTranslator;
import au.org.ala.delta.translation.naturallanguage.IndexWriter;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageDataSetFilter;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageTranslator;
import au.org.ala.delta.translation.nexus.NexusDataSetFilter;
import au.org.ala.delta.translation.nexus.NexusTranslator;
import au.org.ala.delta.translation.paup.PaupTranslator;
import au.org.ala.delta.translation.payne.PayneTranslator;
import au.org.ala.delta.translation.print.CharacterListPrinter;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.translation.print.ItemDescriptionsPrinter;
import au.org.ala.delta.translation.print.ItemNamesPrinter;
import au.org.ala.delta.translation.print.SummaryPrinter;
import au.org.ala.delta.translation.print.UncodedCharactersFilter;
import au.org.ala.delta.translation.print.UncodedCharactersPrinter;
import au.org.ala.delta.translation.print.UncodedCharactersTranslator;
import au.org.ala.delta.translation.print.UncodedCharactersTypeSetter;
import au.org.ala.delta.util.Pair;

import java.util.ArrayList;
import java.util.List;


/**
 * Creates appropriate instances of DataSetTranslator for the supplied DeltaContext.
 */
public class DataSetTranslatorFactory {
	
	/**
	 * Creates a DataSetTranslator instance that is appropriate for the
	 * supplied DeltaContext.  If more than one output action (e.g. 
	 * TRANSLATE INTO / PRINT ..) has been specified, the returned 
	 * DataSetTranslator will be a composite object that delegates to 
	 * multiple translators responsible for specific output formats.
	 * @param context determines the translators to create and the
	 * configuration to use when creating them.
	 */
	public DataSetTranslator createTranslator(DeltaContext context) throws DirectiveException {
		
		TranslateType translation = context.getTranslateType();
		
		FormatterFactory formatterFactory = new FormatterFactory(context);
		
		List<DataSetTranslator> translators = new ArrayList<DataSetTranslator>();
		
		if (translation.equals(TranslateType.NaturalLanguage)) {
			
			AbstractDataSetTranslator translator = new AbstractDataSetTranslator(context);
			translator.add(createNaturalLanguageTranslator(context, context.getPrintFile(), formatterFactory));
			addPrintActions(translator, context);
			translators.add(translator);
		}
		else {
			DataSetTranslator translator = null;
			
			if (translation.equals(TranslateType.Delta)) {
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
			else if (translation.equals(TranslateType.Payne)) {
				translator = createPayneFormatTranslator(context,  context.getOutputFileSelector().getOutputFile(), formatterFactory);
			}
			else if (translation.equals(TranslateType.Hennig86)) {
				translator = createHenningFormatTranslator(context,  context.getOutputFileSelector().getOutputFile(), formatterFactory);
			}
			else if (translation.equals(TranslateType.None)) {
				translator = new NullTranslator();
			}
			else {
				throw DirectiveError.asException(DirectiveError.Error.UNSUPPORTED_TRANSLATION, 0, translation);
			}
			translators.add(translator);
			translators.add(createPrintActions(context));
		}
		
		return new CompositeDataSetTranslator(translators);
	}
	
	/**
	 * Creates an appropriate instance of the ImplicitValuesTranslator for the
	 * supplied context.
	 * @param context contains the configuration for the desired ImplicitValuesTranslator.
	 */
	public ImplicitValuesTranslator createImplicitValuesTranslator(DeltaContext context) {
		
		FormatterFactory formatterFactory = new FormatterFactory(context);
		
		PrintFile output = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createTypeSetter(context, output);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter);
		CharacterFormatter characterFormatter = formatterFactory.createCharacterFormatter();
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		DataSetFilter filter = new NaturalLanguageDataSetFilter(context);
		
		
		return new ImplicitValuesTranslator(context, filter, typeSetter, output, itemFormatter, characterFormatter, attributeFormatter);
	
	}
	
	private DataSetTranslator createPrintActions(DeltaContext context) throws DirectiveException {
		AbstractDataSetTranslator translator = new AbstractDataSetTranslator(context);
		
		addPrintActions(translator, context);
		return translator;
	}
	
	private void addPrintActions(AbstractDataSetTranslator translator, DeltaContext context) throws DirectiveException {
		for (PrintActionType action : context.getPrintActions()) {
			translator.add(createPrintAction(context, action));
		}
	}
	

	private DataSetTranslator createNexusFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		FilteredDataSet dataSet = new FilteredDataSet(context, new NexusDataSetFilter(context));
		CharacterFormatter stateFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.STRIP_ALL);
		
		AttributeTranslatorFactory attributeTranslatorFactory = new AttributeTranslatorFactory(
				context, 
				stateFormatter,
				formatterFactory.createAttributeFormatter(),
				new PlainTextTypeSetter());
		KeyStateTranslator keyStateTranslator = new KeyStateTranslator(attributeTranslatorFactory);
		return new NexusTranslator(context, dataSet, printFile, keyStateTranslator, charFormatter, itemFormatter);
	}
	
	private DataSetTranslator createPaupFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		FilteredDataSet dataSet = new FilteredDataSet(context, new NexusDataSetFilter(context));
		return new PaupTranslator(context, dataSet, printFile, charFormatter, itemFormatter);
	}
	
	private DataSetTranslator createPayneFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		FilteredDataSet dataSet = new FilteredDataSet(context, new AllPassFilter());
		CharacterFormatter stateFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.STRIP_ALL);
		
		AttributeTranslatorFactory attributeTranslatorFactory = new AttributeTranslatorFactory(
				context, 
				stateFormatter,
				formatterFactory.createAttributeFormatter(),
                new PlainTextTypeSetter());
		
		KeyStateTranslator keyStateTranslator = new KeyStateTranslator(attributeTranslatorFactory);
		
		return new PayneTranslator(context, dataSet, printFile, charFormatter, itemFormatter, keyStateTranslator);
	}
	
	private DataSetTranslator createHenningFormatTranslator(DeltaContext context, PrintFile printFile, FormatterFactory formatterFactory) {
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		ItemFormatter itemFormatter = formatterFactory.createItemFormatter(null, CommentStrippingMode.STRIP_ALL, false);
		DataSetFilter filter = new NexusDataSetFilter(context);
		FilteredDataSet dataSet = new FilteredDataSet(context, filter);
		return wrap(context, filter, new Henning86Translator(context, dataSet, printFile, charFormatter, itemFormatter));
	}

	private DataSetTranslator createIntkeyFormatTranslator(DeltaContext context, FormatterFactory formatterFactory) {
		FilteredDataSet dataSet = new FilteredDataSet(context, new DeltaFormatDataSetFilter(context));
		CharacterFormatter charFormatter =formatterFactory.createCharacterFormatter(false, false, CommentStrippingMode.RETAIN);
		charFormatter.setDespaceRtf(true);
		return new IntkeyTranslator(context, dataSet,
				charFormatter,
				formatterFactory.createAttributeFormatter());
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

	private Pair<IterativeTranslator, DataSetFilter> createNaturalLanguageTranslator(
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
		
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	 
	public AbstractDataSetTranslator createDeltaFormatTranslator(
			DeltaContext context, PrintFile printer, ItemListTypeSetter itemTypeSetter) {
		FormatterFactory factory = new FormatterFactory(context);
		return createDeltaFormatTranslator(context, printer, factory, itemTypeSetter);
	}
	
	public AbstractDataSetTranslator createDeltaFormatTranslator(
			DeltaContext context, 
			PrintFile printer, 
			FormatterFactory formatterFactory) {
		ItemListTypeSetter itemTypeSetter = new ItemListTypeSetterAdapter();
		
		return createDeltaFormatTranslator(context, printer, formatterFactory, itemTypeSetter);
	}
	
	public AbstractDataSetTranslator createDeltaFormatTranslator(
			DeltaContext context, 
			PrintFile printer, 
			FormatterFactory formatterFactory,
			ItemListTypeSetter itemTypeSetter) {
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(null);
		itemFormatter.setDespaceRtf(true);
		CharacterFormatter charFormatter = formatterFactory.createCharacterFormatter(true, false, CommentStrippingMode.RETAIN);
		charFormatter.setDespaceRtf(true);
		CharacterListTypeSetter typeSetter = new au.org.ala.delta.translation.print.PlainTextTypeSetter(printer);
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		return wrap(context, filter,new DeltaFormatTranslator(context, printer, itemFormatter, charFormatter, attributeFormatter, typeSetter, itemTypeSetter));
	}
	
	public Pair<IterativeTranslator, DataSetFilter> createPrintAction(DeltaContext context, PrintActionType printAction) throws DirectiveException {
		Pair<IterativeTranslator, DataSetFilter> translator;
		switch (printAction) {
		case PRINT_CHARACTER_LIST:
			translator = createCharacterListPrinter(context);
			break;
		case PRINT_ITEM_NAMES:
			translator = createItemNamesPrinter(context);
			break;
		case PRINT_ITEM_DESCRIPTIONS:
			translator = createItemDescriptionsPrinter(context);
			break;
		case PRINT_UNCODED_CHARACTERS:
			translator = createUncodedCharactersPrinter(context);
			break;	
		case TRANSLATE_UNCODED_CHARACTERS:
			translator = createUncodedCharactersTranslator(context);
			break;	
		case PRINT_SUMMARY:
			translator = createSummaryPrinter(context);
			break;
		default:
			throw DirectiveError.asException(DirectiveError.Error.UNSUPPORTED_TRANSLATION, 0, printAction.toString());
		}
		return translator;
	}
	
	private Pair<IterativeTranslator, DataSetFilter> createSummaryPrinter(DeltaContext context) {
		PrintFile printer = context.getPrintFile();
		DataSetFilter filter = new IncludeExcludeDataSetFilter(context);
		FilteredDataSet dataSet = new FilteredDataSet(context, filter);
		IterativeTranslator translator = new SummaryPrinter(context, dataSet, printer);
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	
	
	private Pair<IterativeTranslator, DataSetFilter> createCharacterListPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		CommentStrippingMode mode = CommentStrippingMode.RETAIN;
		if (context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}
		CharacterFormatter charFormatter  = formatterFactory.createCharacterFormatter(true, true, mode);
		charFormatter.setDespaceRtf(true);
		CharacterListTypeSetter typeSetter = new TypeSetterFactory().createCharacterListTypeSetter(context, printer);
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		IterativeTranslator translator = new CharacterListPrinter(context, printer, charFormatter, typeSetter);
		
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	
	private Pair<IterativeTranslator, DataSetFilter> createItemNamesPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, true);
		
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		IterativeTranslator translator = new ItemNamesPrinter(context, itemFormatter, printer, typeSetter);
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	
	private Pair<IterativeTranslator, DataSetFilter> createItemDescriptionsPrinter(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		PrintFile printer = context.getPrintFile();
		ItemListTypeSetter typeSetter = new TypeSetterFactory().createItemListTypeSetter(context, printer);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		AttributeFormatter attributeFormatter = formatterFactory.createAttributeFormatter();
		DataSetFilter filter = new DeltaFormatDataSetFilter(context);
		IterativeTranslator translator = new ItemDescriptionsPrinter(context, printer, itemFormatter, attributeFormatter, typeSetter);
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	
	private Pair<IterativeTranslator, DataSetFilter> createUncodedCharactersPrinter(DeltaContext context) {
		PrintFile printer = context.getPrintFile();
		TypeSetterFactory typeSetterFactory = new TypeSetterFactory();
		ItemListTypeSetter typeSetter = typeSetterFactory.createItemListTypeSetter(context, printer, 0);
		UncodedCharactersTypeSetter charTypeSetter = typeSetterFactory.createUncodedCharactersTypeSetter(context, printer);
		FormatterFactory formatterFactory = new FormatterFactory(context);
		DataSetFilter filter = new UncodedCharactersFilter(context, false);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		boolean omitItemDescriptions = context.getTranslateType() == TranslateType.NaturalLanguage;
		IterativeTranslator translator = new UncodedCharactersPrinter(context, printer, itemFormatter, typeSetter, charTypeSetter, omitItemDescriptions);
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	
	private Pair<IterativeTranslator, DataSetFilter> createUncodedCharactersTranslator(DeltaContext context) {
		PrintFile printer = context.getPrintFile();
		TypeSetterFactory typeSetterFactory = new TypeSetterFactory();
		ItemListTypeSetter typeSetter = typeSetterFactory.createItemListTypeSetter(context, printer, 0);
		UncodedCharactersTypeSetter charTypeSetter = typeSetterFactory.createUncodedCharactersTypeSetter(context, printer);
		
		FormatterFactory formatterFactory = new FormatterFactory(context);
		DataSetFilter filter = new UncodedCharactersFilter(context, true);
		
		ItemFormatter itemFormatter  = formatterFactory.createItemFormatter(typeSetter, false);
		CharacterFormatter characterFormatter = formatterFactory.createCharacterFormatter(true, true, CommentStrippingMode.RETAIN);
		characterFormatter.setUseBrackettedNumber(true);
		boolean omitItemDescriptions = context.getTranslateType() == TranslateType.NaturalLanguage;
		
		IterativeTranslator translator = new UncodedCharactersTranslator(context, printer, itemFormatter, characterFormatter, typeSetter, charTypeSetter, omitItemDescriptions);
		return new Pair<IterativeTranslator, DataSetFilter>(translator, filter);
	}
	
	private AbstractDataSetTranslator wrap(DeltaContext context, DataSetFilter filter, IterativeTranslator translator) {
		return new AbstractDataSetTranslator(context, filter, translator);
	}
	
}
