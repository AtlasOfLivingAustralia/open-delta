package au.org.ala.delta.translation;

import java.util.Iterator;
import java.util.NoSuchElementException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;

public class IdentificationKeyCharacterIterator implements Iterator<IdentificationKeyCharacter>{

	private int _index;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	public IdentificationKeyCharacterIterator(DeltaContext context) {
		_context = context;
		_dataSet = context.getDataSet();
		_index = 1;
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
		
		return _context.getIdentificationKeyCharacter(_index);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
