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
		int record = nextAvailableRecord();
		for (Pair<String, List<BitSet>> item : items) {
			String description = item.getFirst();
			record += writeToRecord(record, description);
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
	
	private void checkTaxaLength(List<?> values) {
		if (values.size() != _header.getNumberOfItems()) {
			throw new RuntimeException("There must be one value for each item");
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

	public void writeCharacterReliabilities(List<Float> reliabilities) {
		checkCharactersLength(reliabilities);
		checkEmpty(_header.getCharcterReliabilitiesRecord());
		int record = nextAvailableRecord();
		_header.setCharcterReliabilitiesRecord(record);
		
		writeFloatsToRecord(record, reliabilities);

	}
	
	public void writeTaxonMask(List<Boolean> includedTaxa) {
		checkTaxaLength(includedTaxa);
		checkEmpty(_header.getTaxonMaskRecord());
		int record = nextAvailableRecord();
		_header.setTaxonMaskRecord(record);
		
		writeBooleansToRecord(record, includedTaxa);
	}

	public void writeItemLengths(List<Integer> lengths) {
		checkTaxaLength(lengths);
		checkEmpty(_header.getItemNameLengthsRecord());
		int record = nextAvailableRecord();
		_header.setItemNameLengthsRecord(record);
		
		writeToRecord(record, lengths);
	}

	public void writeItemAbundances(List<Float> abundances) {
		checkTaxaLength(abundances);
		checkEmpty(_header.getItemAbundancesRecord());
		int record = nextAvailableRecord();
		_header.setItemAbundancesRecord(record);
		
		writeFloatsToRecord(record, abundances);
	}
}
