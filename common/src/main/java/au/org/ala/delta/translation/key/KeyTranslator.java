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
package au.org.ala.delta.translation.key;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.key.WriteOnceKeyCharsFile;
import au.org.ala.delta.key.WriteOnceKeyItemsFile;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.attribute.AttributeTranslatorFactory;
import au.org.ala.delta.util.FileUtils;

/**
 * Translates a DELTA data set into the format used by the KEY program.
 */
public class KeyTranslator implements DataSetTranslator {

	private DeltaContext _context;
	private FilteredDataSet _dataSet;
	private CharacterFormatter _characterFormatter;
	private ItemFormatter _itemFormatter;
	private FormatterFactory _formatterFactory;
	private AttributeTranslatorFactory _attributeTranslatorFactory;
	
	public KeyTranslator(DeltaContext context, FilteredDataSet dataSet, 
			ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
			FormatterFactory formatterFactory) {
		_context = context;
		_dataSet = dataSet;
		_characterFormatter = characterFormatter;
		_itemFormatter = itemFormatter;
		_formatterFactory = formatterFactory;
		_attributeTranslatorFactory = new AttributeTranslatorFactory(
				_context, 
				_formatterFactory.createCharacterFormatter(),
				_formatterFactory.createAttributeFormatter(), null);
		
	}

	@Override
	public void translateCharacters() throws DirectiveException {
		
		String fileName = _context.getOutputFileSelector().getKeyOutputFilePath();
		FileUtils.backupAndDelete(fileName);
		
		WriteOnceKeyCharsFile charsFile = new WriteOnceKeyCharsFile(
				_dataSet.getNumberOfCharacters(), fileName , BinFileMode.FM_APPEND);
		KeyStateTranslator keyStateTranslator = new KeyStateTranslator(_attributeTranslatorFactory);
		KeyCharactersFileWriter charsWriter = new KeyCharactersFileWriter(
				_dataSet, _characterFormatter, keyStateTranslator, charsFile);
		charsWriter.writeAll();
		charsFile.close();
	}
	
	@Override
	public void translateItems() throws DirectiveException {
		String fileName = _context.getOutputFileSelector().getKeyOutputFilePath();
		FileUtils.backupAndDelete(fileName);
		
		WriteOnceKeyItemsFile itemsFile = new WriteOnceKeyItemsFile(
				_dataSet.getMaximumNumberOfItems(), _dataSet.getNumberOfCharacters(), fileName, BinFileMode.FM_APPEND);
		KeyItemsFileWriter itemsWriter = new KeyItemsFileWriter(_context, _dataSet, _itemFormatter, itemsFile);
		itemsWriter.writeAll();
		itemsFile.close();
	}
	
	@Override
	public void translateOutputParameter(OutputParameter parameter) {}
}
