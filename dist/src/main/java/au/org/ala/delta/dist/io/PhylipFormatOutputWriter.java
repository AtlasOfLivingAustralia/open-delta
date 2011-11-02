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
		
		outputFile.outputLine(Integer.toString(_dataSet.getNumberOfFilteredItems()));
		
		writeMatrix(matrix, outputFile);
	}
	
	protected void writeDistances(DistanceMatrix matrix, FilteredItem item1, PrintFile outputFile) {
		outputFile.setIndent(0);
		outputFile.outputLine(truncate(item1.getItem().getDescription()));
		outputFile.setIndent(1);
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
