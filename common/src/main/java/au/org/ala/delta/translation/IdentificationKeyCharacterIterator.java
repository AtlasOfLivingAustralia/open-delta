package au.org.ala.delta.translation;

import java.util.Iterator;
import java.util.NoSuchElementException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;

/**
 * Iterates through the Characters in a data set, possibly excluding some, 
 * and presenting them to the client as IdentificationKeyCharacters.
 */
public class IdentificationKeyCharacterIterator implements Iterator<IdentificationKeyCharacter>{

	private int _index;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	private AbstractDataSetFilter _filter;
	
	public IdentificationKeyCharacterIterator(DeltaContext context, AbstractDataSetFilter filter) {
		_context = context;
		_dataSet = context.getDataSet();
		_index = 1;
		_filter = filter;
	}
	
	@Override
	public boolean hasNext() {
		return _index <= _dataSet.getNumberOfCharacters();
	}

	@Override
	public IdentificationKeyCharacter next() {
		if (_index > _dataSet.getNumberOfCharacters()) {
			throw new NoSuchElementException();
		}
		
		Character character = _dataSet.getCharacter(_index);
		while (!_filter.filter(character)) {
			_index++;
			character = _dataSet.getCharacter(_index);
		}
		
		IdentificationKeyCharacter keyChar = _context.getIdentificationKeyCharacter(_index);
		if (keyChar == null) {
			keyChar = new IdentificationKeyCharacter(character);
		}
		_index++;
		
		return keyChar;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
