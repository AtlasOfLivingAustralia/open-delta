package au.org.ala.delta.key;

import java.util.List;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;

/**
 * Knows the format of the keys items file.  This class is designed to 
 * write the file once, not provide random access to it.
 */
public class WriteOnceKeyItemsFile extends BinaryKeyFile {
	
	private ItemsFileHeader _header;
	
	public WriteOnceKeyItemsFile(int numItems, int numCharacters, String fileName, BinFileMode mode) {
		super(fileName, mode);
		createHeader(numItems, numCharacters);
	}
	
	public void createHeader(int numItems, int numCharacters) {
		nextAvailableRecord();
		_header = new ItemsFileHeader();
		_header.setNumberOfCharacters(numCharacters);
		_header.setNumberOfItems(numItems);
		
		// This is done to allocate the first record to the header.
		writeToRecord(1, _header.toInts());
	}
	
	public void writeHeader() {
		overwriteRecord(1, _header.toInts());
	}
	
	public void writeCharacterDependencies(List<Integer> dependencyData) {
		checkEmpty(_header.getCharacterDependencyRecord());
		
		int record = nextAvailableRecord();
		_header.setCharacterDependencyRecord(record);
		_header.setCharacterDependenciesLength(dependencyData.size());
		
		writeToRecord(record, dependencyData);
	}
	
	
	private void checkEmpty(int recordNum) {
		if (recordNum > 0) {
			throw new RuntimeException("The record has already been allocated.");
		}
	}
	
	private void checkCharactersLength(List<?> values) {
		if (values.size() != _header.getNumberOfCharacters()) {
			throw new RuntimeException("There must be one value for each character");
		}
	}
}
