package au.org.ala.delta.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.io.BinFileMode;

/**
 * Encapsulates the way the various bits of data in the items file
 * are stored and accessed.
 * It does not support random access - it primarily supports the use case
 * of CONFOR creating the intkey characters file.
 */
public class WriteOnceIntkeyItemsFile extends IntkeyFile {

	private ItemsFileHeader _header;
	
	public WriteOnceIntkeyItemsFile(int numCharacters, int numItems, String fileName, BinFileMode mode) {
		super(fileName, mode);
		createHeader(numCharacters, numItems);
	}
	
	public void createHeader(int numCharacters, int numItems) {
		newRecord();
		_header = new ItemsFileHeader();
		_header.setLRec(RECORD_LENGTH_INTEGERS);
		_header.setNItem(numItems);
		_header.setNChar(numCharacters);
		_header.setMajorVer(DATASET_MAJOR_VERSION);
		_header.setMinorVer(DATASET_MINOR_VERSION);
	}
	
	public void writeItemDescrptions(List<String> descriptions) {
		checkItemListLength(descriptions);
		checkEmpty(_header.getRpTnam());
		int startRecord = newRecord();
		_header.setRpTnam(startRecord);
		
		writeStringsWithOffsetsToRecord(startRecord, descriptions);
	}

	public void writeCharacterSpecs(List<Integer> characterTypes, List<Integer> numStates, List<Float> characterReliabilities) {
		checkCharacterListLength(characterTypes);
		checkCharacterListLength(numStates);
		checkCharacterListLength(characterReliabilities);
		checkEmpty(_header.getRpSpec());
		int record = newRecord();
		_header.setRpSpec(record);
		
		record += writeToRecord(record, characterTypes);
		record += writeToRecord(record, numStates);
		writeFloatsToRecord(record, characterReliabilities);
	}
	
	public void writeMinMaxValues(List<Integer> minValues, List<Integer> maxValues) {
		checkCharacterListLength(minValues);
		checkCharacterListLength(maxValues);
		
		checkEmpty(_header.getRpMini());
		int record = newRecord();
		_header.setRpMini(record);
		
		record += writeToRecord(record, minValues);
		record += writeToRecord(record, maxValues);
	}
	
	public void writeCharacterDependencies() {
		throw new NotImplementedException();
	}
	
	public void writeAttributeData() {
		throw new NotImplementedException();
	}
	
	public void writeAttributeBits(int charNumber, List<BitSet> attributes, int numBits) {
		int record = updateCharacterIndex(charNumber);
		
		// Merge the list into a single BitSet.
		BitSet master = new BitSet();
		int offset = 0;
		for (BitSet set : attributes) {
			for (int i=0; i<numBits; i++) {
				if (set.get(i)) {
					master.set(i+offset);
				}
			}
			offset += numBits;
		}
		
		List<Integer> values = bitSetToInts(master, numBits*attributes.size());
		writeToRecord(record, values);
		
	}

	private int updateCharacterIndex(int charNumber) {
		int indexRecord = _header.getRpCdat();
		if (indexRecord == 0) {
			indexRecord = newRecord();
			_header.setRpCdat(indexRecord);
		}
		List<Integer> indicies = readIntegerList(indexRecord, _header.getNChar());
		
		int record = newRecord();
		indicies.set(charNumber-1, record);
		writeToRecord(indexRecord, indicies);
		return record;
	}
	
	public void writeAttributeFloats(int charNumber, BitSet inapplicableBits, List<FloatRange> values) {
		int record = updateCharacterIndex(charNumber);
		List<Integer> inapplicable = bitSetToInts(inapplicableBits, _header.getNItem());
		record += writeToRecord(record, inapplicable);
		
		List<Float> floats = new ArrayList<Float>();
		for (FloatRange range : values) {
			floats.add(range.getMinimumFloat());
			floats.add(range.getMaximumFloat());
		}
		writeFloatsToRecord(record, floats);
	}
	
	public void writeAttributeStrings(int charNumber, BitSet inapplicableBits, List<String> values) {
		int record = updateCharacterIndex(charNumber);
		List<Integer> inapplicable = bitSetToInts(inapplicableBits, _header.getNItem());
		record += writeToRecord(record, inapplicable);
		
		writeStringsWithOffsetsToRecord(record, values);
	}
	
	
	public void writeKeyStateBoundaries() {
		throw new NotImplementedException();
	}
	
	public void writeTaxonImages(List<String> images) {
		checkEmpty(_header.getRpTimages());
		checkItemListLength(images);
		
		int indexRecord = newRecord();
		_header.setRpTimages(indexRecord);
		
		writeIndexedValues(indexRecord, images.toArray(new String[images.size()]));
	}
	
	public void writeEnableDeltaOutput(boolean enable) {
		_header.setEnableDeltaOutput(toInt(enable));
	}
	
	public void writeChineseFormat(boolean chineseFormat) {
		_header.setChineseFmt(toInt(chineseFormat));
	}
	
	public void writeCharacterSynonymy(List<Boolean> synonomy) {
		checkEmpty(_header.getRpCsynon());
		checkCharacterListLength(synonomy);
		
		int record = newRecord();
		_header.setRpTimages(record);
		
		writeBooleansToRecord(record, synonomy);
	}
	
	public void writeOmitOr(List<Boolean> omitOr) {
		checkEmpty(_header.getRpOmitOr());
		checkCharacterListLength(omitOr);
		
		int record = newRecord();
		_header.setRpOmitOr(record);
		
		writeBooleansToRecord(record, omitOr);
	}
	
	public void writeUseControllingFirst(Set<Integer> useControllingChars) {
		checkEmpty(_header.getRpUseCc());
		int record = newRecord();
		_header.setRpUseCc(record);
		
		writeAsBooleans(useControllingChars, record);
	}

	private void writeAsBooleans(Set<Integer> useControllingChars, int record) {
		List<Boolean> values = new ArrayList<Boolean>(_header.getNChar());
		for (int i=0; i<_header.getNChar(); i++) {
			values.add(useControllingChars.contains(i+1));
		}
		writeBooleansToRecord(record, values);
	}
	
	public void writeTaxonLinks() {
		throw new NotImplementedException();
	}
	
	public void writeOmitPeriod(Set<Integer> omitPeriod) {
		checkEmpty(_header.getRpOmitPeriod());
		int record = newRecord();
		_header.setRpOmitPeriod(record);
		
		writeAsBooleans(omitPeriod, record);
	}
	
	public void writeNewParagraph(Set<Integer> newParagraph) {
		checkEmpty(_header.getRpNewPara());
		int record = newRecord();
		_header.setRpNewPara(record);
		
		writeAsBooleans(newParagraph, record);
	}
	
	public void writeNonAutoControllingChars(Set<Integer> nonAutoCC) {
		checkEmpty(_header.getRpNonAutoCc());
		int record = newRecord();
		_header.setRpNonAutoCc(record);
		
		writeAsBooleans(nonAutoCC, record);
	}
	
	public void writeSubjectForOutputFiles() {
		throw new NotImplementedException();
	}
	
	private int toInt(boolean b) {
		return b ? 1 : 0;
	}
	
	
	private void checkEmpty(int recordNum) {
		if (recordNum > 0) {
			throw new RuntimeException("Character images already exit");
		}
	}
	
	private void checkItemListLength(List<?> taxonList) {
		if (taxonList.size() != _header.getNItem()) {
			throw new IllegalArgumentException("There must be "+_header.getNItem()+" values in the list");
		}
	}
	
	private void checkCharacterListLength(List<?> characterList) {
		if (characterList.size() != _header.getNChar()) {
			throw new IllegalArgumentException("There must be "+_header.getNChar()+" values in the list");
		}
	}

	
}
