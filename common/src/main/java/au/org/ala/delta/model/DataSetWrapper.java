package au.org.ala.delta.model;

import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;

/**
 * A wrapper for an observable DeltaDataSet to simplify the creation of
 * decorator classes.
 */
public class DataSetWrapper implements ObservableDeltaDataSet {

	/** The data set we are wrapping */
	protected ObservableDeltaDataSet _wrappedDataSet;
	
	public DataSetWrapper(ObservableDeltaDataSet dataSet) {
		_wrappedDataSet = dataSet;
	}

	@Override
	public String getName() {
		return _wrappedDataSet.getName();
	}

	@Override
	public void setName(String name) {
		_wrappedDataSet.setName(name);
	}

	@Override
	public void close() {
		_wrappedDataSet.close();
	}
	
	
	@Override
	public void addDeltaDataSetObserver(DeltaDataSetObserver observer) {
		_wrappedDataSet.addDeltaDataSetObserver(observer);
	}

	@Override
	public void removeDeltaDataSetObserver(DeltaDataSetObserver observer) {
		_wrappedDataSet.removeDeltaDataSetObserver(observer);
	}

	@Override
	public Item getItem(int number) {
		Item item = _wrappedDataSet.getItem(number);
		return item;
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		return _wrappedDataSet.getAttributeAsString(itemNumber, characterNumber);
	}

	@Override
	public Character getCharacter(int number) {
		Character character = _wrappedDataSet.getCharacter(number);
		return character;
	}

	@Override
	public int getNumberOfCharacters() {
		return _wrappedDataSet.getNumberOfCharacters();
	}

	@Override
	public int getMaximumNumberOfItems() {
		return _wrappedDataSet.getMaximumNumberOfItems();
	}

	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		Character character = _wrappedDataSet.addCharacter(characterNumber, type);
		return character;
	}

	@Override
	public Character addCharacter(CharacterType type) {
		Character character = _wrappedDataSet.addCharacter(type);
		return character;
	}

	@Override
	public Item addItem(int itemNumber) {
		Item item = _wrappedDataSet.addItem(itemNumber);
		return item;
	}

	@Override
	public Item addItem() {
		Item item = _wrappedDataSet.addItem();
		return item;
	}

	@Override
	public Item addVariantItem(int parentItemNumber, int itemNumber) {
		return _wrappedDataSet.addVariantItem(parentItemNumber, itemNumber);
	}

	@Override
	public boolean isModified() {
		return _wrappedDataSet.isModified();
	}

	public void deleteItem(int itemNumber) {
		deleteItem(_wrappedDataSet.getItem(itemNumber));
	}

	@Override
	public void deleteItem(Item item) {
		_wrappedDataSet.deleteItem(item);
	}
	
	@Override
	public void deleteCharacter(Character character) {
		_wrappedDataSet.deleteCharacter(character);
	}

	@Override
	public Attribute getAttribute(int itemNumber, int characterNumber) {
		return _wrappedDataSet.getAttribute(itemNumber, characterNumber);
	}

	@Override
	public void moveItem(Item item, int newItemNumber) {
		_wrappedDataSet.moveItem(item, newItemNumber);
	}
	
	@Override
	public void deleteState(MultiStateCharacter character, int stateNumber) {
		_wrappedDataSet.deleteState(character, stateNumber);
	}

	@Override
	public void moveCharacter(Character character, int newCharacterNumber) {
		_wrappedDataSet.moveCharacter(character, newCharacterNumber);
	}
	
	@Override
	public List<Item> getUncodedItems(Character character) {
		return _wrappedDataSet.getUncodedItems(character);
	}
	
	@Override
	public List<Item> getItemsWithMultipleStatesCoded(
			MultiStateCharacter character) {
		return _wrappedDataSet.getItemsWithMultipleStatesCoded(character);
	}
	
	@Override
	public List<CharacterDependency> getAllCharacterDependencies() {
		return _wrappedDataSet.getAllCharacterDependencies();
	}

	@Override
	public Character changeCharacterType(Character character,
			CharacterType newType) {
		return _wrappedDataSet.changeCharacterType(character, newType);
	}
	
	@Override
	public boolean canChangeCharacterType(Character character, CharacterType newType) {
		return _wrappedDataSet.canChangeCharacterType(character, newType);
	}

	
	@Override
	public CharacterDependency addCharacterDependency(
			MultiStateCharacter owningCharacter, Set<Integer> states,
			Set<Integer> dependentCharacters) {
		return _wrappedDataSet.addCharacterDependency(owningCharacter, states, dependentCharacters);
	}

	@Override
	public void deleteCharacterDependency(
			CharacterDependency characterDependency) {
		_wrappedDataSet.deleteCharacterDependency(characterDependency);
	}
	
	@Override
	public Item itemForDescription(String description) {
		return _wrappedDataSet.itemForDescription(description);
	}

    @Override
    public Attribute addAttribute(int itemNumber, int characterNumber) {
        return _wrappedDataSet.addAttribute(itemNumber, characterNumber);
    }

	@Override
	public ImageSettings getImageSettings() {
		return _wrappedDataSet.getImageSettings();
	}

	@Override
	public void setImageSettings(ImageSettings imageSettings) {
		_wrappedDataSet.setImageSettings(imageSettings);	
	}

	@Override
	public ControllingInfo checkApplicability(Character character, Item item) {
		return _wrappedDataSet.checkApplicability(character, item);
	}

	@Override
	public boolean isUncoded(Item item, Character character) {
		return _wrappedDataSet.isUncoded(item, character);
	}
   
    
}