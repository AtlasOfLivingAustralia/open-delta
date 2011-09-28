package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.util.Pair;

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
	

	public void writeItems(List<Pair<String, List<BitSet>>> items) {
		checkEmpty(_header.getCharacterDependencyRecord());
		List<Integer> itemDescriptionLengths = new ArrayList<Integer>();
		int record = nextAvailableRecord();
		for (Pair<String, List<BitSet>> item : items) {
			String description = item.getFirst();
			itemDescriptionLengths.add(description.length());
			record += writeToRecord(record, description);
			System.out.println("writing to record : "+description);
			List<Integer> attributes = new ArrayList<Integer>();
			for (BitSet attribute : item.getSecond()) {
				attributes.addAll(bitSetToInts(attribute, 32));
			}
			record += writeToRecord(record, attributes);
		}
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

	public void writeHeading(String heading) {
		checkEmpty(_header.getHeadingRecord());
		int record = nextAvailableRecord();
		_header.setHeadingRecord(record);
		
		writeStringWithLength(record, heading);
	}

	public void writeCharacterMask(List<Boolean> includedCharacters) {
		checkCharactersLength(includedCharacters);
		checkEmpty(_header.getCharacterMaskRecord());
		int record = nextAvailableRecord();
		_header.setCharacterMaskRecord(record);
		
		writeBooleansToRecord(record, includedCharacters);
	}

	public void writeNumbersOfStates(List<Integer> states) {
		checkCharactersLength(states);
		checkEmpty(_header.getStateNumbersRecord());
		int record = nextAvailableRecord();
		_header.setStateNumbersRecord(record);
		
		writeToRecord(record, states);
	}

}
