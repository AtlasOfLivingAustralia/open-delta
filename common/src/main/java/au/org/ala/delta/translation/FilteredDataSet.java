package au.org.ala.delta.translation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DataSetWrapper;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.ObservableDeltaDataSet;
import au.org.ala.delta.util.IdentificationKeyCharacterIterator;

/**
 * Provides a view of the dataset filtered on the directives 
 * EXCLUDE CHARACTERS / EXCLUDE ITEMS
 */
public class FilteredDataSet extends DataSetWrapper {

	private DataSetFilter _filter;
	private DeltaContext _context;
	private List<FilteredItem> _filteredItems;
	private List<FilteredCharacter> _filteredCharacters;
	
	public class UnfilteredItemIterator implements Iterator<Item> {

		private int itemNumber = 1;
		
		@Override
		public boolean hasNext() {
			return itemNumber <= getMaximumNumberOfItems();
		}

		@Override
		public Item next() {
			Item item = getItem(itemNumber);
			itemNumber++;
			return item;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public class UnfilteredCharacterIterator implements Iterator<Character> {

		private int characterNumber = 1;
		
		@Override
		public boolean hasNext() {
			return characterNumber <= getNumberOfCharacters();
		}

		@Override
		public Character next() {
			Character character = getCharacter(characterNumber);
			characterNumber++;
			return character;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public FilteredDataSet(DeltaContext context, DataSetFilter filter) {
		super((ObservableDeltaDataSet)context.getDataSet());
		_context = context;
		_filter = filter;
		filterCharacters();
		filterItems();
	}
	
	
	private void filterCharacters() {
		_filteredCharacters = new ArrayList<FilteredCharacter>();
		
		int numChars = _wrappedDataSet.getNumberOfCharacters();
		for (int i=1; i<=numChars; i++) {
			
			Character character = _wrappedDataSet.getCharacter(i);
			
			if (_filter.filter(character)) {
				int filteredCharNum = _filteredCharacters.size()+1;
				_filteredCharacters.add(new FilteredCharacter(filteredCharNum, character));
			}	
		}
	}
	
	private void filterItems() {
		_filteredItems = new ArrayList<FilteredItem>();
		
		int numItems = _wrappedDataSet.getMaximumNumberOfItems();
		for (int i=1; i<=numItems; i++) {
			
			Item item = _wrappedDataSet.getItem(i);
			
			if (_filter.filter(item)) {
				int filteredItemNum = _filteredItems.size()+1;
				_filteredItems.add(new FilteredItem(filteredItemNum, item));
			}	
		}
	}

	@Override
	public int getNumberOfCharacters() {
		return _filteredCharacters.size();
	}

	@Override
	public int getMaximumNumberOfItems() {
		return _filteredItems.size();
	}
	
	public Iterator<FilteredCharacter> filteredCharacters() {
		return _filteredCharacters.iterator();
	}
	
	public Iterator<FilteredItem> filteredItems() {
		return _filteredItems.iterator();
	}
	
	public Iterator<IdentificationKeyCharacter> identificationKeyCharacterIterator() {
		return new IdentificationKeyCharacterIterator(_context, _filter);
	}
	
	public Iterator<IdentificationKeyCharacter> unfiltereddentificationKeyCharacterIterator() {
		return new IdentificationKeyCharacterIterator(_context, new AllPassFilter());
	}
	
	public Iterator<Character> unfilteredCharacters() {
		return new UnfilteredCharacterIterator();
	}
	
	public Iterator<Item> unfilteredItems() {
		return new UnfilteredItemIterator();
	}
}
