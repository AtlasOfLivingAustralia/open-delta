/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.translation.intkey.IntkeyItemsFileWriter;

/**
 * Encapsulates the way the various bits of data in the items file
 * are stored and accessed.
 * It does not support random access - it primarily supports the use case
 * of CONFOR creating the intkey characters file.
 */
public class WriteOnceIntkeyItemsFile extends BinaryKeyFile {

	public static final int CONFOR_INT_MAX = (int)Math.pow(2, 29);
	
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
		
		// This is done to allocate the first record to the header.
		// For binary compatibility with CONFOR we only write the first half
		// now.
		writeToRecord(1, _header.toInts().subList(0, RECORD_LENGTH_INTEGERS));	
	}
	
	public void writeHeader() {
		
		int part2 = nextAvailableRecord();
		_header.setRpNext(part2);
		List<Integer> header = _header.toInts();
		overwriteRecord(1, header.subList(0, RECORD_LENGTH_INTEGERS));
		
		List<Integer> headerPart2 = header.subList(RECORD_LENGTH_INTEGERS, header.size());
		writeToRecord(part2, headerPart2);
	}
	
	public void writeItemDescriptions(List<String> descriptions) {
		checkItemListLength(descriptions);
		checkEmpty(_header.getRpTnam());
		int startRecord = nextAvailableRecord();
		_header.setRpTnam(startRecord);
		
		writeStringsWithOffsetsToRecord(startRecord, descriptions);
	}

	public void writeCharacterSpecs(List<Integer> characterTypes, List<Integer> numStates, int maxStates, List<Float> characterReliabilities) {
		checkCharacterListLength(characterTypes);
		checkCharacterListLength(numStates);
		checkCharacterListLength(characterReliabilities);
		checkEmpty(_header.getRpSpec());
		int record = nextAvailableRecord();
		_header.setRpSpec(record);
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
			if (range.getMaximumInteger() == CONFOR_INT_MAX) {
				maxValues.add(-CONFOR_INT_MAX);
			}
			else {
				maxValues.add(range.getMaximumInteger());
			}
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

	private int[] _attributeIndex = null;
	
	private int updateCharacterIndex(int charNumber) {
		int next = 0;
		if (_attributeIndex == null) {
			_attributeIndex = new int[_header.getNChar()];
			Arrays.fill(_attributeIndex, 0);
		}
		next = nextAvailableRecord();
		_attributeIndex[charNumber-1] = next;
		
		return next;
	}
	
	public void writeAttributeIndex() {
		checkEmpty(_header.getRpCdat());
		int indexRecord = nextAvailableRecord();
		_header.setRpCdat(indexRecord);
		writeToRecord(indexRecord, _attributeIndex);
		
	}
	
	private int[] _keyStateBoundariesIndex;
	
	public void writeAttributeFloats(int charNumber, BitSet inapplicableBits, List<FloatRange> values, List<Float> keyStateBoundaries) {
		int record = updateCharacterIndex(charNumber);
		List<Integer> inapplicable = bitSetToInts(inapplicableBits, _header.getNItem());
		record += writeToRecord(record, inapplicable);
		
		List<Float> floats = new ArrayList<Float>();
		for (FloatRange range : values) {
			// Special cases, Float.MAX_VALUE indicates coded unknown.
			//               -Float.MIN_VALUE indicates uncoded unknown
			if (range.getMinimumFloat() == Float.MAX_VALUE) {
				// These somewhat strange values are for CONFOR compatibility
				floats.add((float)CONFOR_INT_MAX);
				floats.add(-(float)CONFOR_INT_MAX);
			}
			else if (range.getMaximumFloat() == -Float.MAX_VALUE) {
				floats.add(1f);
				floats.add(0f);
			}
			else {
				floats.add(range.getMinimumFloat());
				floats.add(range.getMaximumFloat());
			}
		}
		writeFloatsToRecord(record, floats);
		
		
			
		int recordNum = nextAvailableRecord();
		writeToRecord(recordNum, keyStateBoundaries.size());
		writeFloatsToRecord(recordNum+1, keyStateBoundaries);
		
		if (_keyStateBoundariesIndex == null) {
			_keyStateBoundariesIndex = new int[_header.getNChar()];
			Arrays.fill(_keyStateBoundariesIndex, 0);
		}
		_keyStateBoundariesIndex[charNumber-1] = recordNum;
		_header.setLSbnd(_header.getLSbnd()+keyStateBoundaries.size());
		_header.setLkstat(Math.max(_header.getLkstat(), keyStateBoundaries.size()));
		
	}
	
	public void writeAttributeStrings(int charNumber, BitSet inapplicableBits, List<String> values) {
		
		int record = updateCharacterIndex(charNumber);
		List<Integer> inapplicable = bitSetToInts(inapplicableBits, _header.getNItem());
		record += writeToRecord(record, inapplicable);
		
		int maxSingle = 0;
		int total = 0;
		for (String value : values) {
			total += value.length();
			maxSingle = Math.max(maxSingle, value.length());
		}
		_header.setMaxSingleText(Math.max(maxSingle, _header.getMaxSingleText()));
		_header.setTotalText(total+_header.getTotalText());
		
		writeStringsWithOffsetsToRecord(record, values);
	}
	
	
	public void writeKeyStateBoundariesIndex() {
		
		checkEmpty(_header.getRpNkbd());
		if (_keyStateBoundariesIndex != null) {
			
			int indexRecord = nextAvailableRecord();
			_header.setRpNkbd(indexRecord);
			
			writeToRecord(indexRecord, _keyStateBoundariesIndex);
		}
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
