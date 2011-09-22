package au.org.ala.delta.translation.intkey;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.FilteredDataSet;

/**
 * Translates a DELTA data set into the format used by the INTKEY program.
 */
public class IntkeyTranslator implements DataSetTranslator {

	private DeltaContext _context;
	private FilteredDataSet _dataSet;
	
	public IntkeyTranslator(DeltaContext context, FilteredDataSet dataSet) {
		_context = context;
		_dataSet = dataSet;
	}

	@Override
	public void translateCharacters() {
		
		String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		WriteOnceIntkeyCharsFile charsFile = new WriteOnceIntkeyCharsFile(
				_dataSet.getNumberOfCharacters(), fileName , BinFileMode.FM_APPEND);
		IntkeyCharactersFileWriter charsWriter = new IntkeyCharactersFileWriter(_context, _dataSet, charsFile);
		charsWriter.writeAll();
		charsFile.close();
	}
	
	@Override
	public void translateItems() {
		String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		
		WriteOnceIntkeyItemsFile itemsFile = new WriteOnceIntkeyItemsFile(
				_dataSet.getNumberOfCharacters(), _dataSet.getMaximumNumberOfItems(), fileName, BinFileMode.FM_APPEND);
		IntkeyItemsFileWriter itemsWriter = new IntkeyItemsFileWriter(_context, _dataSet, itemsFile);
		itemsWriter.writeAll();
		itemsFile.close();
	}
}
