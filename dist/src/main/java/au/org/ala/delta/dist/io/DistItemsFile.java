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
package au.org.ala.delta.dist.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.dist.ItemsFileHeader;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.util.Pair;

/**
 * Provides access to the data in the DIST items file.
 */
public class DistItemsFile extends BinaryKeyFile {
	
	private ItemsFileHeader _header;
	
	public DistItemsFile(String fileName) {
		super(fileName, BinFileMode.FM_READONLY);
		readHeader();
	}
	
	private void readHeader() {
		_header = new ItemsFileHeader();
		List<Integer> headerInts = readIntegerList(1, ItemsFileHeader.SIZE);
		_header.fromInts(headerInts);
	}
	
	public int getNumberOfItems() {
		return _header.getNumberOfItems();
	}
	
	public int getNumberOfCharacters() {
		return _header.getNumberOfCharacters();
	}
	
	public Pair<String, ByteBuffer> readItem(int itemNum) {
		int itemRecord = getItemRecord(itemNum);
		int nameLength = getItemNameLength(itemNum);
		
		int itemAttributeRecord = itemRecord+numRecords(nameLength);
		String itemDescription = readString(itemRecord, nameLength);
		ByteBuffer attributeData = readBytes(itemAttributeRecord, _header.getLengthOfAttributeLists()*4);
		
		return new Pair<String, ByteBuffer>(itemDescription, attributeData);
	}
	
	public Pair<List<Integer>, List<Integer>> getAttributeOffsets() {
		int record = _header.getItemCharacterIndexRecord();
		List<Integer> wordsOffset = readIntegerList(record, _header.getNumberOfCharacters());
		
		record = _header.getItemCharacterBitOffsetsRecord();
		List<Integer> bitsOffset = readIntegerList(record, _header.getNumberOfCharacters());
		
		return new Pair<List<Integer>, List<Integer>>(wordsOffset, bitsOffset);
	}
	
	private int numRecords(int numBytes) {
		if (numBytes % RECORD_LENGTH_BYTES == 0) {
			return numBytes / RECORD_LENGTH_BYTES;
		}
		else {
			return numBytes / RECORD_LENGTH_BYTES + 1;
		}
	}
	
	private int getItemNameLength(int itemNum) {
		List<Integer> nameLengths = readIntegerList(_header.getLengthsOfItemNamesRecord(), _header.getNumberOfItems());
		return nameLengths.get(itemNum-1);
	}
	
	private int getItemRecord(int itemNum) {
		List<Integer> itemRecords = readIntegerList(_header.getItemsRecord(), _header.getNumberOfItems());
		return itemRecords.get(itemNum-1);
	}
	
	public List<Integer> readCharacterTypes() {
		return readIntegerList(_header.getCharacterTypesRecord(), _header.getNumberOfCharacters());
	}
	
	public List<Integer> readNumbersOfStates() {
		return readIntegerList(_header.getStateNumbersRecord(), _header.getNumberOfCharacters());
	}
	
	public List<Float> readCharacterWeights() {
		return readFloatList(_header.getCharcterWeightsRecord(), _header.getNumberOfCharacters());
	}
	
	public List<Boolean> readCharacterMask() {
		List<Integer> maskInts = readIntegerList(_header.getCharacterMaskRecord(), _header.getNumberOfCharacters());
		List<Boolean> mask = new ArrayList<Boolean>(maskInts.size());
		for (int maskInt : maskInts) {
			mask.add(maskInt != 0);
		}
		return mask;
	}
	
	
}
