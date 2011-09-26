package au.org.ala.delta.intkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.translation.intkey.IntkeyItemsFileWriter;

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
		nextAvailableRecord();
		_header = new ItemsFileHeader();
		_header.setLRec(RECORD_LENGTH_INTEGERS);
		_header.setNItem(numItems);
		_header.setNChar(numCharacters);
		_header.setMajorVer(DATASET_MAJOR_VERSION);
		_header.setMinorVer(DATASET_MINOR_VERSION);
		_header.setMaxInt(IntkeyItemsFileWriter.INTEGER_RANGE_MAX_THRESHOLD);
		// Even if there is no data, we always mark that there is a second
		// section for simplicity (otherwise we don't know how many 
		// records to allocate)
		_header.setRpNext(2);
		// This is done to allocate the first record to the header.
		writeToRecord(1, _header.toInts());	
	}
	
	public void writeHeader() {
		overwriteRecord(1, _header.toInts());	
	}
	
	public void writeItemDescriptions(List<String> descriptions) {
		checkItemListLength(descriptions);
		checkEmpty(_header.getRpTnam());
		int startRecord = nextAvailableRecord();
		_header.setRpTnam(startRecord);
		
		writeStringsWithOffsetsToRecord(startRecord, descriptions);
	}

	public void writeCharacterSpecs(List<Integer> characterTypes, List<Integer> numStates, List<Float> characterReliabilities) {
		checkCharacterListLength(characterTypes);
		checkCharacterListLength(numStates);
		checkCharacterListLength(characterReliabilities);
		checkEmpty(_header.getRpSpec());
		int record = nextAvailableRecord();
		_header.setRpSpec(record);
		
		int maxStates = 0;
		for (int state : numStates) {
			maxStates = Math.max(maxStates, state);
		}
		_header.setMs(maxStates);
		
		record += writeToRecord(record, characterTypes);
		record += writeToRecord(record, numStates);
		writeFloatsToRecord(record, characterReliabilities);
	}
	
	public void changeCharacterType(int characterNumber, int newType) {
		int record = _header.getRpSpec();
		List<Integer> charTypes = readIntegerList(record, _header.getNChar());
		
		charTypes.set(characterNumber-1, newType);
		overwriteRecord(record, charTypes);
	}
	
	public void writeMinMaxValues(List<IntRange> minMaxValues) {
		checkCharacterListLength(minMaxValues);
		
		checkEmpty(_header.getRpMini());
		int record = nextAvailableRecord();
		_header.setRpMini(record);
		
		List<Integer> minValues = new ArrayList<Integer>();
		List<Integer> maxValues = new ArrayList<Integer>();
		for (IntRange range : minMaxValues) {
			minValues.add(range.getMinimumInteger());
			maxValues.add(range.getMaximumInteger());
			
		}
		record += writeToRecord(record, minValues);
		record += writeToRecord(record, maxValues);
	}
	
	public void writeCharacterDependencies(List<Integer> dependencyData, List<Integer> invertedDependencyData) {
		checkEmpty(_header.getRpCdep());
		int record = nextAvailableRecord();
		_header.setRpCdep(record);
		_header.setLDep(dependencyData.size());
		record += writeToRecord(record, dependencyData);
		
		_header.setRpInvdep(record);
		_header.setLinvdep(invertedDependencyData.size());
		writeToRecord(record, invertedDependencyData);
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
		List<Integer> indicies = null;
		int next = 0;
		if (indexRecord == 0) {
			indexRecord = nextAvailableRecord();
			_header.setRpCdat(indexRecord);
			Integer[] indiciesArray = new Integer[_header.getNChar()];
			Arrays.fill(indiciesArray, Integer.valueOf(0));
			indicies = Arrays.asList(indiciesArray);
			next = indexRecord + indiciesArray.length / RECORD_LENGTH_INTEGERS + 1;
			indicies.set(charNumber-1, next);
			writeToRecord(indexRecord, indicies);
		}
		else {
			indicies = readIntegerList(indexRecord, _header.getNChar());
			next = nextAvailableRecord();
			indicies.set(charNumber-1, next);
			overwriteRecord(indexRecord, indicies);
		}
		
		return next;
	}
	
	public void writeAttributeFloats(int charNumber, BitSet inapplicableBits, List<FloatRange> values) {
		int record = updateCharacterIndex(charNumber);
		List<Integer> inapplicable = bitSetToInts(inapplicableBits, _header.getNItem());
		record += writeToRecord(record, inapplicable);
		
		List<Float> floats = new ArrayList<Float>();
		for (FloatRange range : values) {
			// Special case - Float.MAX_VALUE indicates unknown.
			if (range.getMinimumFloat() == Float.MAX_VALUE) {
				floats.add(Float.MAX_VALUE);
				floats.add(-Float.MAX_VALUE);
			}
			else {
				floats.add(range.getMinimumFloat());
				floats.add(range.getMaximumFloat());
			}
		}
		writeFloatsToRecord(record, floats);
	}
	
	public void writeAttributeStrings(int charNumber, BitSet inapplicableBits, List<String> values) {
		int record = updateCharacterIndex(charNumber);
		List<Integer> inapplicable = bitSetToInts(inapplicableBits, _header.getNItem());
		record += writeToRecord(record, inapplicable);
		
		writeStringsWithOffsetsToRecord(record, values);
	}
	
	
	public void writeKeyStateBoundaries(List<List<Float>> keyStateBoundaries) {
		checkEmpty(_header.getRpNkbd());
		checkCharacterListLength(keyStateBoundaries);
		int sum = 0;
		int maxCount = 0;
		for (List<Float> boundaries : keyStateBoundaries) {
			sum += boundaries.size();
			maxCount = Math.max(maxCount, boundaries.size());
		}
		if (sum == 0) {
			// no key state boundaries.
			return;
		}
		
		int indexRecord = nextAvailableRecord();
		_header.setRpNkbd(indexRecord);
		_header.setLSbnd(sum);
		_header.setLkstat(maxCount);
		
		List<Integer> index = new ArrayList<Integer>();
		// Write the index to allocate the record.
		for (int i=0; i<keyStateBoundaries.size(); i++) {
			index.add(0);
		}
		writeToRecord(indexRecord, index);
		index = new ArrayList<Integer>();
		
		for (List<Float> charBoundaries : keyStateBoundaries) {
			if (charBoundaries.size() > 0) {
				int recordNum = nextAvailableRecord();
				index.add(recordNum);
				writeToRecord(recordNum, charBoundaries.size());
				writeFloatsToRecord(recordNum+1, charBoundaries);
				
			}
			else {
				index.add(0);
			}
		}
		
		// Now update the index
		overwriteRecord(indexRecord, index);
	}
	
	public void writeTaxonImages(List<String> images) {
		checkEmpty(_header.getRpTimages());
		checkItemListLength(images);
		
		int indexRecord = nextAvailableRecord();
		_header.setRpTimages(indexRecord);
		
		writeIndexedValues(indexRecord, images.toArray(new String[images.size()]));
	}
	
	public void writeEnableDeltaOutput(boolean enable) {
		int checkSum = 0;
		if (enable) {
			List<Integer> charTypes = readIntegerList(_header.getRpSpec(), _header.getNChar());
			for (int type : charTypes) {
				checkSum += type;
			}
		}
		_header.setEnableDeltaOutput(checkSum);
	}
	
	public void writeChineseFormat(boolean chineseFormat) {
		_header.setChineseFmt(toInt(chineseFormat));
	}
	
	public void writeCharacterSynonymy(List<Boolean> synonomy) {
		checkEmpty(_header.getRpCsynon());
		checkCharacterListLength(synonomy);
		
		int record = nextAvailableRecord();
		_header.setRpCsynon(record);
		
		writeBooleansToRecord(record, synonomy);
	}
	
	public void writeOmitOr(List<Boolean> omitOr) {
		checkEmpty(_header.getRpOmitOr());
		checkCharacterListLength(omitOr);
		
		int record = nextAvailableRecord();
		_header.setRpOmitOr(record);
		
		writeBooleansToRecord(record, omitOr);
	}
	
	public void writeUseControllingFirst(Set<Integer> useControllingChars) {
		checkEmpty(_header.getRpUseCc());
		int record = nextAvailableRecord();
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
	
	public void writeTaxonLinks(int index, List<String> taxonLinks) {
		int[] taxonLinksIndex = _header.getRpTlinks();
		checkEmpty(taxonLinksIndex[index]);
		checkItemListLength(taxonLinks);
		
		int indexRecord = nextAvailableRecord();
		taxonLinksIndex[index] = indexRecord;
		_header.setRpTlinks(taxonLinksIndex);
		
		writeIndexedValues(indexRecord, taxonLinks.toArray(new String[taxonLinks.size()]));
	}
	
	public void writeOmitPeriod(Set<Integer> omitPeriod) {
		checkEmpty(_header.getRpOmitPeriod());
		int record = nextAvailableRecord();
		_header.setRpOmitPeriod(record);
		
		writeAsBooleans(omitPeriod, record);
	}
	
	public void writeNewParagraph(Set<Integer> newParagraph) {
		checkEmpty(_header.getRpNewPara());
		int record = nextAvailableRecord();
		_header.setRpNewPara(record);
		
		writeAsBooleans(newParagraph, record);
	}
	
	public void writeNonAutoControllingChars(Set<Integer> nonAutoCC) {
		checkEmpty(_header.getRpNonAutoCc());
		int record = nextAvailableRecord();
		_header.setRpNonAutoCc(record);
		
		writeAsBooleans(nonAutoCC, record);
	}
	
	private int toInt(boolean b) {
		return b ? 1 : 0;
	}
	
	
	private void checkEmpty(int recordNum) {
		if (recordNum > 0) {
			throw new RuntimeException("This record has already been written");
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
