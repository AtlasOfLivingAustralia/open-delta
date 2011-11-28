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
package au.org.ala.delta.translation.intkey;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.util.FileUtils;

/**
 * Translates a DELTA data set into the format used by the INTKEY program.
 */
public class IntkeyTranslator implements DataSetTranslator {

	private DeltaContext _context;
	private FilteredDataSet _dataSet;
	private CharacterFormatter _characterFormatter;
	private AttributeFormatter _attributeFormatter;
	
	public IntkeyTranslator(
			DeltaContext context, 
			FilteredDataSet dataSet, 
			CharacterFormatter characterFormatter,
			AttributeFormatter attributeFormatter) {
		_context = context;
		_dataSet = dataSet;
		_characterFormatter = characterFormatter;
		_attributeFormatter = attributeFormatter;
	}

	@Override
	public void translateCharacters() throws DirectiveException {
		
		String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		FileUtils.backupAndDelete(fileName);
		
		WriteOnceIntkeyCharsFile charsFile = new WriteOnceIntkeyCharsFile(
				_dataSet.getNumberOfFilteredCharacters(), fileName , BinFileMode.FM_APPEND);
		IntkeyCharactersFileWriter charsWriter = new IntkeyCharactersFileWriter(_context, _dataSet, _characterFormatter, charsFile);
		charsWriter.writeAll();
		charsFile.close();
	}
	
	@Override
	public void translateItems() throws DirectiveException {
		String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		FileUtils.backupAndDelete(fileName);
		
		WriteOnceIntkeyItemsFile itemsFile = new WriteOnceIntkeyItemsFile(
				_dataSet.getNumberOfFilteredCharacters(), _dataSet.getNumberOfFilteredItems(), fileName, BinFileMode.FM_APPEND);
		IntkeyItemsFileWriter itemsWriter = new IntkeyItemsFileWriter(_context, _dataSet, itemsFile, _attributeFormatter);
		itemsWriter.writeAll();
		itemsFile.close();
	}
	
	@Override
	public void translateOutputParameter(OutputParameter parameter) {}
}
