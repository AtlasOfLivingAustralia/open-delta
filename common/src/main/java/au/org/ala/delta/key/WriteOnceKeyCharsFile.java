package au.org.ala.delta.key;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;

/**
 * Knows the format of the keys character file.  This class is designed to 
 * write the file once, not provide random access to it.
 */
public class WriteOnceKeyCharsFile extends BinaryKeyFile {
	
	private CharactersFileHeader _header;
	
	public WriteOnceKeyCharsFile(int numCharacters, String fileName, BinFileMode mode) {
		super(fileName, mode);
		createHeader(numCharacters);
	}
	
	public void createHeader(int numCharacters) {
		nextAvailableRecord();
		_header = new CharactersFileHeader();
		_header.setNumberOfCharacters(numCharacters);
		// This is done to allocate the first record to the header.
		writeToRecord(1, _header.toInts());
	}
	
	public void writeHeader() {
		overwriteRecord(1, _header.toInts());
	}
}
