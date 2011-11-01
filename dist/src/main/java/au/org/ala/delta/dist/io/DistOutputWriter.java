package au.org.ala.delta.dist.io;

import java.text.DecimalFormat;

import au.org.ala.delta.dist.DistContext;
import au.org.ala.delta.dist.DistanceMatrix;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.translation.PrintFile;

public class DistOutputWriter {

	private DecimalFormat _decimalFormat;
	private DistContext _context;
	
	public DistOutputWriter(DistContext context) {
		_context = context;
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

	private void writeMatrix(DistanceMatrix matrix, PrintFile outputFile) {
		DeltaDataSet dataSet = _context.getDataSet();
		int maxItems = dataSet.getMaximumNumberOfItems();
		for (int i=1; i<=maxItems-1; i++) {
			for (int j=maxItems-1; j > i; j--) {
				double value = matrix.get(i,j);
				outputFile.writeJustifiedText(_decimalFormat.format(value), -1);
			}
			outputFile.printBufferLine();
		}
		
	}

	private void writeNames(PrintFile namesFile) {
		
		DeltaDataSet dataSet = _context.getDataSet();
		for (int i=1; i<=dataSet.getMaximumNumberOfItems(); i++) {
			namesFile.outputLine(dataSet.getItem(i).getDescription());
		}
	}
}
