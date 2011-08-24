package au.org.ala.delta.translation.intkey;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.translation.DataSetTranslator;

public class IntkeyTranslator implements DataSetTranslator {

	private DeltaContext _context;
	private DeltaDataSet _dataSet;
	
	public IntkeyTranslator(DeltaContext context) {
		_context = context;
		_dataSet = context.getDataSet();
	}

	@Override
	public void translateCharacters() {
		
		String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		WriteOnceIntkeyCharsFile charsFile = new WriteOnceIntkeyCharsFile(
				_dataSet.getNumberOfCharacters(), fileName , BinFileMode.FM_APPEND);
		IntkeyCharactersFileWriter charsWriter = new IntkeyCharactersFileWriter(_context, charsFile);
	}
	
	@Override
	public void translateItems() {
		String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		
		WriteOnceIntkeyItemsFile itemsFile = new WriteOnceIntkeyItemsFile(
				_dataSet.getNumberOfCharacters(), _dataSet.getMaximumNumberOfItems(), fileName, BinFileMode.FM_APPEND);
		IntkeyItemsFileWriter itemWriter = new IntkeyItemsFileWriter(_context, itemsFile);
	}
}
