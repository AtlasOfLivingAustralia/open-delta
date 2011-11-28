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
package au.org.ala.delta.translation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DataSetWrapper;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
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
	
	public int filteredCharacterNumber(int originalCharacterNumber) {
		for (FilteredCharacter character : _filteredCharacters) {
			if (originalCharacterNumber == character.getCharacter().getCharacterId()) {
				return character.getCharacterNumber();
			}
		}
		throw new IllegalArgumentException("No such character: "+originalCharacterNumber);
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

	public int getNumberOfFilteredCharacters() {
		return _filteredCharacters.size();
	}

	public int getNumberOfFilteredItems() {
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
	
	public Iterator<IdentificationKeyCharacter> unfilteredIdentificationKeyCharacterIterator() {
		return new IdentificationKeyCharacterIterator(_context, new AllPassFilter());
	}
	
	public Iterator<Character> unfilteredCharacters() {
		return new UnfilteredCharacterIterator();
	}
	
	public Iterator<Item> unfilteredItems() {
		return new UnfilteredItemIterator();
	}

	public int getNumberOfIntegerCharacters() {
		int count = 0;
		Iterator<IdentificationKeyCharacter> chars = identificationKeyCharacterIterator();
		while (chars.hasNext()) {
			if (chars.next().getCharacterType() == CharacterType.IntegerNumeric) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @return the maximum number of states in any character (including filtered ones), excluding 
	 * any characters modified by the KEY STATES directive.
	 */
	public int getMaximumNumberOfStates() {
		Iterator<FilteredCharacter> chars = filteredCharacters();
		int maxStates = 0;
		while (chars.hasNext()) {
			Character character = chars.next().getCharacter();
			if (character.getCharacterType().isMultistate()) {
				MultiStateCharacter mulitStateChar = (MultiStateCharacter)character;
				maxStates = Math.max(maxStates, mulitStateChar.getNumberOfStates());
			}
		}
		return maxStates;
	}
}
