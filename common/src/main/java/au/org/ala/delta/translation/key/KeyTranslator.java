package au.org.ala.delta.translation.key;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.key.WriteOnceKeyCharsFile;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.FilteredDataSet;

/**
 * Translates a DELTA data set into the format used by the KEY program.
 */
public class KeyTranslator implements DataSetTranslator {

	private DeltaContext _context;
	private FilteredDataSet _dataSet;
	private CharacterFormatter _characterFormatter;
	
	public KeyTranslator(DeltaContext context, FilteredDataSet dataSet, CharacterFormatter characterFormatter) {
		_context = context;
		_dataSet = dataSet;
		_characterFormatter = characterFormatter;
	}

	@Override
	public void translateCharacters() {
		
		String fileName = _context.getOutputFileSelector().getKeyOutputFilePath();
		WriteOnceKeyCharsFile charsFile = new WriteOnceKeyCharsFile(
				_dataSet.getNumberOfCharacters(), fileName , BinFileMode.FM_APPEND);
		KeyCharactersFileWriter charsWriter = new KeyCharactersFileWriter(_context, _dataSet, _characterFormatter, charsFile);
		charsWriter.writeAll();
		charsFile.close();
	}
	
	@Override
	public void translateItems() {
		//String fileName = _context.getOutputFileSelector().getIntkeyOutputFilePath();
		
		//WriteOnceIntkeyItemsFile itemsFile = new WriteOnceIntkeyItemsFile(
		//		_dataSet.getNumberOfCharacters(), _dataSet.getMaximumNumberOfItems(), fileName, BinFileMode.FM_APPEND);
		//IntkeyItemsFileWriter itemsWriter = new IntkeyItemsFileWriter(_context, _dataSet, itemsFile);
		//itemsWriter.writeAll();
		//itemsFile.close();
	}
}
