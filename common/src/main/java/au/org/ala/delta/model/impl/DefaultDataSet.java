package au.org.ala.delta.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.ImageSettings;


/**
 * Default in-memory implementation of a DeltaDataset.
 * Note that this class is not thread safe.
 *
 */
public class DefaultDataSet extends AbstractObservableDataSet {

	/** The name of this data set */
	private String _name;
	
	private Map<Integer, Item> _items;
	
	private Map<Integer, Character> _characters;
	
	private Set<CharacterDependency> _characterDependencies;
	
	private ImageSettings _imageSettings;
	
	private boolean _modified;
	
	public DefaultDataSet(DeltaDataSetFactory factory) {
		super(factory);
		_items = new HashMap<Integer, Item>();
		_characters = new HashMap<Integer, Character>();
		_characterDependencies = new HashSet<CharacterDependency>();
		_modified = false;
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
	public Item doGetItem(int number) {
		return _items.get(number);
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		return getItem(itemNumber).getAttribute(getCharacter(characterNumber)).getValueAsString();
	}

	@Override
	public Character doGetCharacter(int number) {
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
	protected Character doAddCharacter(int characterNumber, CharacterType type) {
		
		Character character = _factory.createCharacter(type, characterNumber);
		_characters.put(characterNumber, character);
		
		return character;
	}
	
	@Override
	public void deleteCharacter(Character character) {
		
		int numCharacters = _characters.size();
		_characters.remove(character);
		
		renumberCharacters(character.getCharacterId()+1, numCharacters, -1);
		
		fireCharacterDeleted(character);
	}
	
	@Override
	public void moveCharacter(Character character, int newCharacterNumber) {
		int oldCharacterNumber = character.getCharacterId();
		
		_characters.remove(oldCharacterNumber);
		if (newCharacterNumber < oldCharacterNumber) {
			renumberCharacters(newCharacterNumber, oldCharacterNumber-1, 1);
		}
		else if (newCharacterNumber > oldCharacterNumber ){
			renumberCharacters(oldCharacterNumber+1, newCharacterNumber, -1);
		}
		character.setCharacterNumber(newCharacterNumber);
		_characters.put(newCharacterNumber,character);
		
	}
	
	
	@Override
	protected Item doAddItem(int itemNumber) {
		
		Item item = _factory.createItem(itemNumber);
		_items.put(itemNumber, item);

		return item;	
	}
	

	@Override
	protected Item doAddVariantItem(int parentItemNumber, int itemNumber) {
		Item parent = getItem(parentItemNumber);
		
		Item item = _factory.createVariantItem(parent, itemNumber);
		_items.put(itemNumber, item);

		return item;	
	}

	@Override
	public boolean isModified() {
		return _modified;
	}
	
	public void setModified(boolean modified) {
		_modified = modified;
	}
	
	@Override
	public void deleteItem(Item item) {
		
		int numItems = _items.size();
		_items.remove(item);
		
		renumberItems(item.getItemNumber()+1, numItems, -1);
		
		fireItemDeleted(item);
	}

	@Override
	public void moveItem(Item item, int newItemNumber) {
		int oldItemNumber = item.getItemNumber();
		
		_items.remove(oldItemNumber);
		if (newItemNumber < oldItemNumber) {
			renumberItems(newItemNumber, oldItemNumber-1, 1);
		}
		else if (newItemNumber > oldItemNumber ){
			renumberItems(oldItemNumber+1, newItemNumber, -1);
		}
		item.setItemNumber(newItemNumber);
		_items.put(newItemNumber,item);
		
	}
	
	private void renumberItems(int from, int to, int change) {
		Map<Integer, Item> tempItems = new HashMap<Integer, Item>();
		for (int i=from; i<=to; i++) {
			
			Item tmp = doGetItem(i);
			tmp.setItemNumber(i+change);
			_items.remove(i);
			tempItems.put(i+change, tmp);		
		}
		_items.putAll(tempItems);
	}
	
	private void renumberCharacters(int from, int to, int change) {
		Map<Integer, Character> tempItems = new HashMap<Integer, Character>();
		for (int i=from; i<=to; i++) {
			
			Character tmp = doGetCharacter(i);
			tmp.setCharacterNumber(i+change);
			_items.remove(i);
			tempItems.put(i+change, tmp);		
		}
		_characters.putAll(tempItems);
	}

	@Override
	protected void doDeleteState(MultiStateCharacter character, int stateNumber) {
		throw new UnsupportedOperationException();
	}
	
	@Override
    public Attribute addAttribute(int itemNumber, int characterNumber) {
        Item item = getItem(itemNumber);
        Character character = getCharacter(characterNumber);
        Attribute attribute = _factory.createAttribute(character, item);
        item.addAttribute(character, attribute);
        return attribute;
    }
	
	@Override
	public Character changeCharacterType(Character character,
			CharacterType newType) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean canChangeCharacterType(Character character,
			CharacterType newType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CharacterDependency addCharacterDependency(
			MultiStateCharacter owningCharacter, Set<Integer> states,
			Set<Integer> dependentCharacters) {
		CharacterDependency characterDependency = _factory.createCharacterDependency(owningCharacter, states, dependentCharacters);
		owningCharacter.addDependentCharacters(characterDependency);
		
		for (int characterNumber : dependentCharacters) {
			Character dependent = getCharacter(characterNumber);
			dependent.addControllingCharacter(characterDependency);
		}
		
		return characterDependency;
	}

	@Override
	public void deleteCharacterDependency(
			CharacterDependency characterDependency) {
		getCharacter(characterDependency.getControllingCharacterId()).removeControllingCharacter(characterDependency);
	}
	
	/**
	 * Returns information about how character and taxon images are displayed.
	 */
	public ImageSettings getImageSettings() {
		return _imageSettings;
	}
	
	/**
	 * Specifies information about how character and taxon images are displayed.
	 */
	public void setImageSettings(ImageSettings imageSettings) {
		_imageSettings = imageSettings;
	}
}
