package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the header for the key characters file.
 */
public class CharactersFileHeader {
    
    /** The size of the header in ints */
    public static final int SIZE = 3;

	private int _numberOfCharacters;
	private int _keyStatesRecord;
	private int _characterDetailsRecord;

	public int getNumberOfCharacters() {
		return _numberOfCharacters;
	}

	public void setNumberOfCharacters(int numberOfCharacters) {
		this._numberOfCharacters = numberOfCharacters;
	}

	public int getKeyStatesRecord() {
		return _keyStatesRecord;
	}

	public void setKeyStatesRecord(int characterStatesRecord) {
		this._keyStatesRecord = characterStatesRecord;
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
		ints.add(_keyStatesRecord);
		ints.add(_characterDetailsRecord);
		return ints;
	}
	
	public void fromInts(List<Integer> ints) {
		_numberOfCharacters = ints.get(0);
		_keyStatesRecord = ints.get(1);
		_characterDetailsRecord = ints.get(2);
	}
}
