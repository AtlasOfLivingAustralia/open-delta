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
package au.org.ala.delta.dist.io;

import au.org.ala.delta.dist.DistContext;
import au.org.ala.delta.dist.DistanceMatrix;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the distance matrix and item names to the same file.
 */
public class PhylipFormatOutputWriter extends DistOutputWriter {
	
	public PhylipFormatOutputWriter(DistContext context, FilteredDataSet dataSet) {
		super(context, dataSet);		
	}
	
	
	public void writeOutput(DistanceMatrix matrix) throws Exception {
		
		PrintFile outputFile = getOutputFile();
		
		outputFile.outputLine("     "+Integer.toString(_dataSet.getNumberOfFilteredItems()));
		
		writeMatrix(matrix, outputFile);
	}
	
	protected void writeDistances(DistanceMatrix matrix, FilteredItem item1, PrintFile outputFile) {
		outputFile.setIndent(0);
		outputFile.outputLine(truncate(item1.getItem().getDescription()));
		super.writeDistances(matrix, item1, outputFile);
	}
	
	private String truncate(String item) {
		int length = item.length();
		if (length < OUTPUT_COLUMNS) {
			
			for (int i=0; i<OUTPUT_COLUMNS - length; i++) {
				item += " ";
			}
			return item;
		}
		return item.substring(0, OUTPUT_COLUMNS);
	}
}
