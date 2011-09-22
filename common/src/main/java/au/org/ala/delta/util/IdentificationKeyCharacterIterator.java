package au.org.ala.delta.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.translation.DataSetFilter;

/**
 * Iterates through the Characters in a data set, possibly excluding some, 
 * and presenting them to the client as IdentificationKeyCharacters.
 */
public class IdentificationKeyCharacterIterator implements Iterator<IdentificationKeyCharacter>{

	private int _realIndex;
	private int _filteredIndex;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	private DataSetFilter _filter;
	
	public IdentificationKeyCharacterIterator(DeltaContext context, DataSetFilter filter) {
		_context = context;
		_dataSet = context.getDataSet();
		_realIndex = 1;
		_filteredIndex = 1;
		_filter = filter;
	}
	
	@Override
	public boolean hasNext() {
		if (_realIndex > _dataSet.getNumberOfCharacters()) {
			return false;
		}
		int tmpRealIndex = _realIndex;
		int tmpFilteredIndex = _filteredIndex;
		if (next() != null) {
			_realIndex = tmpRealIndex;
			_filteredIndex = tmpFilteredIndex;
			return true;
		}
		return false;
	}

	@Override
	public IdentificationKeyCharacter next() {
		if (_realIndex > _dataSet.getNumberOfCharacters()) {
			throw new NoSuchElementException();
		}
		
		Character character = _dataSet.getCharacter(_realIndex);
		while ( !_filter.filter(character) && _realIndex < _dataSet.getNumberOfCharacters()) {
			_realIndex++;
			character = _dataSet.getCharacter(_realIndex);
		}
		if (!_filter.filter(character) || character == null) {
			return null;
		}
		
		IdentificationKeyCharacter keyChar = _context.getIdentificationKeyCharacter(_realIndex);
		if (keyChar == null) {
			keyChar = new IdentificationKeyCharacter(character);
		}
		_realIndex++;
		
		
		if (keyChar != null) {
			keyChar.setFilteredCharacterNumber(_filteredIndex);
		}
		_filteredIndex++;
		return keyChar;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
