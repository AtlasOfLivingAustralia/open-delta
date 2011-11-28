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
package au.org.ala.delta.dist;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.io.BinaryKeyFile;

/**
 * Represents the header for the key items file.
 */
public class ItemsFileHeader {
	
	/** The size of the header in ints */
	public static final int SIZE = 18;
	
	private int _recordLength = BinaryKeyFile.RECORD_LENGTH_INTEGERS;
	private int _bitsPerWord = 32;
	private int _charsPerWord = 4;
	private int _numberOfCharacters;
	private int _numberOfItems;
	private int _maximumNumberOfStates;
	private int _lengthOfAttributeLists;
	private int _lengthOfHeading;
	private int _headerRecord;
	private int _characterTypesRecord;
	
	private int _stateNumbersRecord;
	private int _characterMaskRecord;
	private int _characterWeightsRecord;
	private int _itemCharacterIndexRecord;
	private int _itemCharacterBitOffsetsRecord;
	private int _itemMaskRecord;
	private int _itemsRecord;
	private int _lengthsOfItemNamesRecord;
	
	public int getMaximumNumberOfStates() {
		return _maximumNumberOfStates;
	}

	public void setMaximumNumberOfStates(int maximumNumberOfStates) {
		this._maximumNumberOfStates = maximumNumberOfStates;
	}

	public int getLengthOfAttributeLists() {
		return _lengthOfAttributeLists;
	}

	public void setLengthOfAttributeLists(int lengthOfAttributeLists) {
		this._lengthOfAttributeLists = lengthOfAttributeLists;
	}

	public int getLengthOfHeading() {
		return _lengthOfHeading;
	}

	public void setLengthOfHeading(int lengthOfHeading) {
		this._lengthOfHeading = lengthOfHeading;
	}

	public int getCharcterWeightsRecord() {
		return _characterWeightsRecord;
	}

	public void setCharcterWeightsRecord(int charcterWeightsRecord) {
		this._characterWeightsRecord = charcterWeightsRecord;
	}

	public int getItemCharacterIndexRecord() {
		return _itemCharacterIndexRecord;
	}

	public void setItemCharacterIndexRecord(int itemCharacterIndexRecord) {
		this._itemCharacterIndexRecord = itemCharacterIndexRecord;
	}

	public int getItemCharacterBitOffsetsRecord() {
		return _itemCharacterBitOffsetsRecord;
	}

	public void setItemCharacterBitOffsetsRecord(int itemCharacterBitOffsetsRecord) {
		this._itemCharacterBitOffsetsRecord = itemCharacterBitOffsetsRecord;
	}

	public int getItemMaskRecord() {
		return _itemMaskRecord;
	}

	public void setItemMaskRecord(int itemMaskRecord) {
		this._itemMaskRecord = itemMaskRecord;
	}

	public int getItemsRecord() {
		return _itemsRecord;
	}

	public void setItemsRecord(int itemsRecord) {
		this._itemsRecord = itemsRecord;
	}

	public int getLengthsOfItemNamesRecord() {
		return _lengthsOfItemNamesRecord;
	}

	public void setLengthsOfItemNamesRecord(int lengthsOfItemNamesRecord) {
		this._lengthsOfItemNamesRecord = lengthsOfItemNamesRecord;
	}

	public int getNumberOfCharacters() {
		return _numberOfCharacters;
	}

	public void setNumberOfCharacters(int numberOfCharacters) {
		this._numberOfCharacters = numberOfCharacters;
	}

	public int getNumberOfItems() {
		return _numberOfItems;
	}

	public void setNumberOfItems(int numberOfItems) {
		this._numberOfItems = numberOfItems;
	}

	public int getHeadingRecord() {
		return _headerRecord;
	}

	public void setHeadingRecord(int headerRecord) {
		this._headerRecord = headerRecord;
	}

	public int getCharacterMaskRecord() {
		return _characterMaskRecord;
	}

	public void setCharacterMaskRecord(int characterMaskRecord) {
		this._characterMaskRecord = characterMaskRecord;
	}

	public int getStateNumbersRecord() {
		return _stateNumbersRecord;
	}

	public void setStateNumbersRecord(int stateNumbersRecord) {
		this._stateNumbersRecord = stateNumbersRecord;
	}
	
	public int getCharacterTypesRecord() {
		return _characterTypesRecord;
	}

	public void setCharacterTypesRecord(int characterTypesRecord) {
		this._characterTypesRecord = characterTypesRecord;
	}


	/**
	 * Encodes the header data as a list of integers so it can be written
	 * to the dist items file.
	 */
	public List<Integer> toInts() {
		List<Integer> ints = new ArrayList<Integer>();
		ints.add(_recordLength);
		ints.add(_bitsPerWord);
		ints.add(_charsPerWord);
		ints.add(_numberOfItems);
		ints.add(_numberOfCharacters);
		ints.add(_maximumNumberOfStates);
		ints.add(_lengthOfAttributeLists);
		ints.add(_lengthOfHeading);
		ints.add(_headerRecord);
		ints.add(_characterTypesRecord);
		ints.add(_stateNumbersRecord);
		ints.add(_characterMaskRecord);
		ints.add(_characterWeightsRecord);
		ints.add(_itemCharacterIndexRecord);
		ints.add(_itemCharacterBitOffsetsRecord);
		ints.add(_itemMaskRecord);
		ints.add(_itemsRecord);
		ints.add(_lengthsOfItemNamesRecord);
		
		return ints;
	}
	
	public void fromInts(List<Integer> ints) {
		_recordLength = ints.get(0);
		_bitsPerWord = ints.get(1);
		_charsPerWord = ints.get(2);
		_numberOfItems = ints.get(3);
		_numberOfCharacters = ints.get(4);
		_maximumNumberOfStates = ints.get(5);
		_lengthOfAttributeLists = ints.get(6);
		_lengthOfHeading = ints.get(7);
		_headerRecord = ints.get(8);
		_characterTypesRecord = ints.get(9);
		_stateNumbersRecord = ints.get(10);
		_characterMaskRecord = ints.get(11);
		_characterWeightsRecord = ints.get(12);
		_itemCharacterIndexRecord = ints.get(13);
		_itemCharacterBitOffsetsRecord = ints.get(14);
		_itemMaskRecord = ints.get(15);
		_itemsRecord = ints.get(16);
		_lengthsOfItemNamesRecord = ints.get(17);
		
	}
}
