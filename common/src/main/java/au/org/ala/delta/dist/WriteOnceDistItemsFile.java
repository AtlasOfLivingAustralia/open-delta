package au.org.ala.delta.dist;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;

/**
 * Knows the format of the keys items file.  This class is designed to 
 * write the file once, not provide random access to it.
 */
public class WriteOnceDistItemsFile extends BinaryKeyFile {
	
	private ItemsFileHeader _header;
	
	public WriteOnceDistItemsFile(int numItems, int numCharacters, String fileName, BinFileMode mode) {
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
	

	
}
