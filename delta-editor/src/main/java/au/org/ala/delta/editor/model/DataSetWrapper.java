package au.org.ala.delta.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.ObservableDeltaDataSet;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;

/**
 * A wrapper for an observable DeltaDataSet.  It allows DeltaDataSetObservers to
 * be isolated from the actual data set so that references to closed views don't 
 * hang around in the model and prevent garbage collection.
 */
public class DataSetWrapper implements ObservableDeltaDataSet, DeltaDataSetObserver {

	/** The data set we are wrapping */
	protected ObservableDeltaDataSet _wrappedDataSet;
	/** Maintains a list of objects interested in being notified of changes to this model */
	private List<DeltaDataSetObserver> _observerList = new ArrayList<DeltaDataSetObserver>();

	public DataSetWrapper(ObservableDeltaDataSet dataSet) {
		_wrappedDataSet = dataSet;
		_wrappedDataSet.addDeltaDataSetObserver(this);
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
	
	/**
	 * Adds an observer interested in receiving notification of changes to this model.
	 * Duplicate observers are ignored.
	 * @param observer the observer to add.
	 */
	public void addDeltaDataSetObserver(DeltaDataSetObserver observer) {
		if (_observerList.contains(observer)) {
			return;
		}
		_observerList.add(observer);
	}

	/**
	 * Prevents an observer from receiving further notifications of changes to this model.
	 * @param observer the observer to remove.
	 */
	public void removeDeltaDataSetObserver(DeltaDataSetObserver observer) {
		_observerList.remove(observer);
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
	public void itemAdded(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemAdded(event);
		}
	}

	@Override
	public void itemDeleted(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemDeleted(event);
		}
	}

	@Override
	public void itemMoved(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemMoved(event);
		}
	}

	@Override
	public void itemEdited(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemEdited(event);
		}
	}

	@Override
	public void itemSelected(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemSelected(event);
		}
	}

	@Override
	public void characterAdded(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterAdded(event);
		}
	}

	@Override
	public void characterDeleted(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterDeleted(event);
		}
	}

	@Override
	public void characterMoved(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterMoved(event);
		}
	}

	@Override
	public void characterEdited(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterEdited(event);
		}
	}

	@Override
	public void characterSelected(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterSelected(event);
		}	
	}

    @Override
    public Attribute addAttribute(int itemNumber, int characterNumber) {
        return _wrappedDataSet.addAttribute(itemNumber, characterNumber);
    }
}