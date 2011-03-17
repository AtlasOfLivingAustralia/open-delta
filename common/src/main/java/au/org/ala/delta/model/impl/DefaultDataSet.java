package au.org.ala.delta.model.impl;

import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.Item;


/**
 * Default in-memory implementation of a DeltaDataset.
 * Note that this class is not thread safe.
 *
 */
public class DefaultDataSet implements DeltaDataSet {

	/** The name of this data set */
	private String _name;
	
	private Map<Integer, Item> _items;
	
	private Map<Integer, Character> _characters;
	
	private DeltaDataSetFactory _factory;
	
	public DefaultDataSet(DeltaDataSetFactory factory) {
		_factory = factory;
		_items = new HashMap<Integer, Item>();
		_characters = new HashMap<Integer, Character>();
	}
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) {
		_name = name;
	}

	@Override
	public Item getItem(int number) {
		return _items.get(number);
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		return getItem(itemNumber).getAttribute(getCharacter(characterNumber)).getValue();
	}

	@Override
	public Character getCharacter(int number) {
		return 	_characters.get(number);
	}

	@Override
	public int getNumberOfCharacters() {
		return _characters.size();
	}

	@Override
	public int getMaximumNumberOfItems() {
		return _items.size();
	}

	@Override
	public void close() {
		// Do nothing.
	}

	@Override
	public Character addCharacter(CharacterType type) {
		
		return addCharacter(getNumberOfCharacters(), type);
	}
	
	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		
		Character character = _factory.createCharacter(type, characterNumber);
		_characters.put(characterNumber, character);
		
		return character;
	}

	@Override
	public Item addItem() {
		
		return addItem(getMaximumNumberOfItems());
	}
	
	@Override
	public Item addItem(int itemNumber) {
		
		Item item = _factory.createItem(itemNumber);
		_items.put(itemNumber, item);
		
		return item;	
	}
}
