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
package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.io.OutputFileSelector;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the index file during a translate into natural language with
 * output format html.
 */
public class IndexWriter {

	private PrintFile _indexFile;
	private ItemFormatter _formatter;
	private DeltaContext _context;
	
	public IndexWriter(PrintFile indexFile, ItemFormatter itemFormatter, DeltaContext context) {
		_indexFile = indexFile;
		_context = context;
		_formatter = itemFormatter;
	}
	
	public void writeIndexText() {
		
	}
	
	public void addItemToIndex(Item item) {
		writeItemIndexHeading(item);
		writeItemIndexEntry(item);
	}
	
	private void writeItemIndexHeading(Item item) {
		
		String heading = _context.getIndexHeading(item.getItemNumber());
		if (StringUtils.isNotEmpty(heading)) {
			_indexFile.outputLine(heading);
		}
	}
	
	private void writeItemIndexEntry(Item item) {
        OutputFileSelector outputFileSelector = _context.getOutputFileSelector();
        String outputFile = outputFileSelector.getItemOutputFile(item.getItemNumber());
        if (StringUtils.isBlank(outputFile)) {
            outputFile = outputFileSelector.getPrintFileName();
        }
		
		StringBuilder indexEntry = new StringBuilder();
		indexEntry.append("&#149;&nbsp;<a href=\"");
		indexEntry.append(outputFile);
		indexEntry.append("\">");
		indexEntry.append(_formatter.formatItemDescription(item));
		indexEntry.append("</a>");
		
		_indexFile.outputLine(indexEntry.toString());
	}
	
}
