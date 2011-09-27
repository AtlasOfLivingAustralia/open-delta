package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

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
	
	public void writeKeyStates(List<Integer> states) {
		checkEmpty(_header.getKeyStatesRecord());
		checkLength(states);
		
		int record = nextAvailableRecord();
		_header.setKeyStatesRecord(record);
		
		writeToRecord(record, states);	
	}

	public void writeCharacterFeatures(List<List<String>> features) {
		checkEmpty(_header.getCharacterDetailsRecord());
		checkLength(features);
		
		List<Integer> index = new ArrayList<Integer>(features.size());
		int record = nextAvailableRecord();
		for (int i=0; i<features.size(); i++) {
			index.add(record);
			record += writeAsContinousString(record, features.get(i).toArray(new String[0]));
		}
		
		_header.setCharacterDetailsRecord(record);
		writeToRecord(record, index);
	}
	
	private void checkEmpty(int recordNum) {
		if (recordNum > 0) {
			throw new RuntimeException("The record has already been allocated.");
		}
	}
	
	private void checkLength(List<?> values) {
		if (values.size() != _header.getNumberOfCharacters()) {
			throw new RuntimeException("There must be one value for each character");
		}
	}
}
