package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the header for the key characters file.
 */
public class CharactersFileHeader {

	private int _numberOfCharacters;
	private int _characterStatesRecord;
	private int _characterDetailsRecord;

	public int getNumberOfCharacters() {
		return _numberOfCharacters;
	}

	public void setNumberOfCharacters(int numberOfCharacters) {
		this._numberOfCharacters = numberOfCharacters;
	}

	public int getCharacterStatesRecord() {
		return _characterStatesRecord;
	}

	public void setCharacterStatesRecord(int characterStatesRecord) {
		this._characterStatesRecord = characterStatesRecord;
	}

	public int getCharacterDetailsRecord() {
		return _characterDetailsRecord;
	}

	public void setCharacterDetailsRecord(int characterDetailsRecord) {
		this._characterDetailsRecord = characterDetailsRecord;
	}

	/**
	 * Encodes the header data as a list of integers so it can be written
	 * to the key characters file.
	 */
	public List<Integer> toInts() {
		List<Integer> ints = new ArrayList<Integer>();
		ints.add(_numberOfCharacters);
		ints.add(_characterStatesRecord);
		ints.add(_characterDetailsRecord);
		return ints;
	}
	
	public void fromInts(List<Integer> ints) {
		_numberOfCharacters = ints.get(0);
		_characterStatesRecord = ints.get(1);
		_characterDetailsRecord = ints.get(2);
	}
}
