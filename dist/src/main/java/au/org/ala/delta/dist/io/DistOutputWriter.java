package au.org.ala.delta.dist.io;

import java.text.DecimalFormat;
import java.util.Iterator;

import au.org.ala.delta.dist.DistContext;
import au.org.ala.delta.dist.DistanceMatrix;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the distance matrix and item names to the files identified by their
 * respective directives.
 */
public class DistOutputWriter {

	protected static final int OUTPUT_COLUMNS = 10;
	protected DecimalFormat _decimalFormat;
	protected DistContext _context;
	protected FilteredDataSet _dataSet;
	
	public DistOutputWriter(DistContext context, FilteredDataSet dataSet) {
		_context = context;
		_dataSet = dataSet;
		_decimalFormat = new DecimalFormat(".00000");
		
	}
	
	
	public void writeOutput(DistanceMatrix matrix) throws Exception {
		DistOutputFileManager outputFileManager = _context.getOutputFileManager();
		
		PrintFile outputFile = outputFileManager.getOutputFile();
		
		if (!_context.isPhylipFormat()) {
			PrintFile namesFile = outputFileManager.getNamesFile();
			writeNames(namesFile);
		}
		
		writeMatrix(matrix, outputFile);
		
	}

	protected void writeMatrix(DistanceMatrix matrix, PrintFile outputFile) {
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			FilteredItem item1 = items.next();
			writeDistances(matrix, item1, outputFile);
			
			outputFile.printBufferLine();
		}
		
	}
	
	protected void writeDistances(DistanceMatrix matrix, FilteredItem item1, PrintFile outputFile) {
		Iterator<FilteredItem> itemsToCompareAgainst = _dataSet.filteredItems();
		while (itemsToCompareAgainst.hasNext()) {
			FilteredItem item2 = itemsToCompareAgainst.next();
			
			double value = matrix.get(item1.getItemNumber(), item2.getItemNumber());
			if (item2.getItemNumber() > item1.getItemNumber()) {
				outputFile.writeJustifiedText(_decimalFormat.format(value), -1);
			}
		}
	}

	private void writeNames(PrintFile namesFile) {
		
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			Item item = items.next().getItem();
			writeName(item, namesFile);
		}
	}
	
	protected void writeName(Item item, PrintFile namesFile) {
		namesFile.outputLine(item.getDescription());
	}
}
