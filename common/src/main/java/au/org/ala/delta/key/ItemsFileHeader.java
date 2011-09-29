package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the header for the key items file.
 */
public class ItemsFileHeader {

	private int _numberOfCharacters;
	private int _numberOfItems;
	private int _characterDependenciesLength;
	private int _headerRecord;
	private int _characterMaskRecord;
	private int _stateNumbersRecord;
	private int _characterDependencyRecord;
	private int _charcterReliabilitiesRecord;
	private int _taxonMaskRecord;
	private int _itemNameLengthsRecord;
	private int _itemAbundanciesRecord;
	
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

	public int getCharacterDependenciesLength() {
		return _characterDependenciesLength;
	}

	public void setCharacterDependenciesLength(int characterDependenciesLength) {
		this._characterDependenciesLength = characterDependenciesLength;
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

	public int getCharacterDependencyRecord() {
		return _characterDependencyRecord;
	}

	public void setCharacterDependencyRecord(int characterDependencyRecord) {
		this._characterDependencyRecord = characterDependencyRecord;
	}

	public int getCharcterReliabilitiesRecord() {
		return _charcterReliabilitiesRecord;
	}

	public void setCharcterReliabilitiesRecord(int charcterReliabilitiesRecord) {
		this._charcterReliabilitiesRecord = charcterReliabilitiesRecord;
	}

	public int getTaxonMaskRecord() {
		return _taxonMaskRecord;
	}

	public void setTaxonMaskRecord(int taxonMaskRecord) {
		this._taxonMaskRecord = taxonMaskRecord;
	}

	public int getItemNameLengthsRecord() {
		return _itemNameLengthsRecord;
	}

	public void setItemNameLengthsRecord(int itemNameLengthsRecord) {
		this._itemNameLengthsRecord = itemNameLengthsRecord;
	}

	public int getItemAbundancesRecord() {
		return _itemAbundanciesRecord;
	}

	public void setItemAbundancesRecord(int itemAbundanciesRecord) {
		this._itemAbundanciesRecord = itemAbundanciesRecord;
	}

	/**
	 * Encodes the header data as a list of integers so it can be written
	 * to the key characters file.
	 */
	public List<Integer> toInts() {
		List<Integer> ints = new ArrayList<Integer>();
		
		ints.add(_numberOfItems);
		ints.add(_numberOfCharacters);
		ints.add(_characterDependenciesLength);
		ints.add(_headerRecord);
		ints.add(_characterMaskRecord);
		ints.add(_stateNumbersRecord);
		ints.add(_characterDependencyRecord);
		ints.add(_charcterReliabilitiesRecord);
		ints.add(_taxonMaskRecord);
		ints.add(_itemNameLengthsRecord);
		ints.add(_itemAbundanciesRecord);
	
		return ints;
	}
	
	public void fromInts(List<Integer> ints) {
		_numberOfItems = ints.get(0);
		_numberOfCharacters = ints.get(1);
		_characterDependenciesLength = ints.get(2);
		_headerRecord = ints.get(3);
		_characterMaskRecord = ints.get(4);
		_stateNumbersRecord = ints.get(5);
		_characterDependencyRecord = ints.get(6);
		_charcterReliabilitiesRecord = ints.get(7);
		_taxonMaskRecord = ints.get(8);
		_itemNameLengthsRecord = ints.get(9);
		_itemAbundanciesRecord = ints.get(10);
		
	}
}
