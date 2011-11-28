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
package au.org.ala.delta.translation.dist;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.dist.WriteOnceDistItemsFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.util.FileUtils;

/**
 * Translates a DELTA data set into the format used by the DIST program.
 */
public class DistTranslator implements DataSetTranslator {

	private DeltaContext _context;
	private FilteredDataSet _dataSet;
	private ItemFormatter _itemFormatter;
	
	public DistTranslator(DeltaContext context, FilteredDataSet dataSet, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_itemFormatter = itemFormatter;
	}

	@Override
	public void translateCharacters() {}
	
	@Override
	public void translateItems() throws DirectiveException {
		String fileName = _context.getOutputFileSelector().getDistOutputFilePath();
		FileUtils.backupAndDelete(fileName);
		
		WriteOnceDistItemsFile itemsFile = new WriteOnceDistItemsFile(
				_dataSet.getMaximumNumberOfItems(), _dataSet.getNumberOfCharacters(), fileName, BinFileMode.FM_APPEND);
		DistItemsFileWriter itemsWriter = new DistItemsFileWriter(_context, _dataSet, _itemFormatter, itemsFile);
		itemsWriter.writeAll();
		itemsFile.close();
	}
	
	@Override
	public void translateOutputParameter(OutputParameter parameter) {}
}
