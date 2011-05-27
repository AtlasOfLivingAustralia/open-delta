package au.org.ala.delta.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.observer.CharacterObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;
import au.org.ala.delta.model.observer.ItemObserver;

/**
 * An implementation of the DeltaDataSet interface designed to support notification of changes to the
 * data set.  
 * Subclasses should implement the "doXXX" methods where they exist instead of overriding the
 * equivalent interface method.
 */
public abstract class AbstractObservableDataSet implements ObservableDeltaDataSet, ItemObserver, CharacterObserver {

	/** Maintains a list of objects interested in being notified of changes to this data set */
	protected List<DeltaDataSetObserver> _observerList = new ArrayList<DeltaDataSetObserver>();

	
	/**
	 * Adds this AbstractObservableDataSet as an observer of the character.  The actual work
	 * of getting the Character needs to be implemented in doGetCharacter.
	 */
	@Override
	public Character getCharacter(int number) {
		
		Character character = doGetCharacter(number);
		if (character != null) {
			character.addCharacterObserver(this);
		}
		return character;
	}
	protected abstract Character doGetCharacter(int number);
	
	/**
	 * Adds this AbstractObservableDataSet as an observer of the item.  The actual work of getting
	 * the Item needs to be done in doGetItem.
	 */
	@Override
	public Item getItem(int number) {
		Item item = doGetItem(number);
		item.addItemObserver(this);
		return item;
	}
	protected abstract Item doGetItem(int number);
	
	
	/**
	 * Convenience method to add the new character using the next available character number.
	 * @param type the type of character to create and add.
	 */
	@Override
	public Character addCharacter(CharacterType type) {
		return addCharacter(getNumberOfCharacters()+1, type);
	}
	
	/**
	 * Handles notification of the new Character.  The actual work of adding the Character needs
	 * to be implemented in doAddCharacter.
	 */
	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		Character character = doAddCharacter(characterNumber, type);
		character.addCharacterObserver(this);
		fireCharacterAdded(character);
		return character;
	}
	protected abstract Character doAddCharacter(int characterNumber, CharacterType type);
	
	/**
	 * Convenience method to add the new Item using the next available item number.
	 */
	@Override
	public Item addItem() {
		return addItem(getMaximumNumberOfItems()+1);
	}
	
	/**
	 * Handles notification of the new Item.  The actual work of adding the Item needs
	 * to be implemented in doAddItem.
	 */
	@Override
	public Item addItem(int itemNumber) {
		Item item = doAddItem(itemNumber);
		item.addItemObserver(this);
		fireItemAdded(item);
		return item;
	}
	protected abstract Item doAddItem(int itemNumber);
	
	public Item addVariantItem(int parentItemNumber, int itemNumber) {
		Item item = doAddVariantItem(parentItemNumber, itemNumber);
		item.addItemObserver(this);
		fireItemAdded(item);
		return item;
	}
	protected abstract Item doAddVariantItem(int parentItemNumber, int itemNumber);
	
	@Override
	public Attribute getAttribute(int itemNumber, int characterNumber) {
		return getItem(itemNumber).getAttribute(getCharacter(characterNumber));
	}
	
	@Override
	public void deleteState(MultiStateCharacter character, int stateNumber) {
		doDeleteState(character, stateNumber);
	
		characterChanged(character);
	}
	
	protected abstract void doDeleteState(MultiStateCharacter character, int stateNumber);
	
	@Override
	public List<Item> getUncodedItems(Character character) {
		List<Item> uncodedItems = new ArrayList<Item>();
		if (character.getCharacterType().isMultistate() &&
			((MultiStateCharacter)character).getUncodedImplicitState() > 0) {
			return uncodedItems;
		}
		for (int i=1; i<=getMaximumNumberOfItems(); i++) {
			Item item = getItem(i);
			if (!item.hasAttribute(character) && 
				!character.checkApplicability(item).isInapplicable()) {
				uncodedItems.add(item);
			}
		}
		return uncodedItems;
	}
	
	@Override
	public List<Item> getItemsWithMultipleStatesCoded(MultiStateCharacter character) {
		List<Item> items = new ArrayList<Item>();
		for (int i=1; i<=getMaximumNumberOfItems(); i++) {
			Item item = getItem(i);
			MultiStateAttribute attribute = (MultiStateAttribute)item.getAttribute(character);
			if (attribute == null) {
				continue;
			}
			Set<Integer> states = attribute.getPresentStates();
			if (states.size() > 1 || attribute.isVariable()) {
				items.add(item);
			}
		}
		return items;
	}
	
	
	
	@Override
	public List<CharacterDependency> getAllCharacterDependencies() {
		List<CharacterDependency> characterDependencies = new ArrayList<CharacterDependency>();
		for (int i=1; i<getNumberOfCharacters(); i++) {
			Character character = doGetCharacter(i);
			if (character.getCharacterType().isMultistate()) {
				characterDependencies.addAll(((MultiStateCharacter)character).getControllingCharacters());
			}
		}
		return characterDependencies;
	}
	/**
	 * Adds an observer interested in receiving notification of changes to this data set.
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
	 * Prevents an observer from receiving further notifications of changes to this data set.
	 * @param observer the observer to remove.
	 */
	public void removeDeltaDataSetObserver(DeltaDataSetObserver observer) {
		_observerList.remove(observer);
	}
	
	protected void fireDeltaDataSetEvent(Item item, Character character, DataSetEventDispatcher dispatcher) {
		dispatcher.fireDataSetEvent(item, character);
	}
	protected void fireDeltaDataSetEvent(Item item, Character character, Object extraInformation, DataSetEventDispatcher dispatcher) {
		dispatcher.fireDataSetEvent(item, character, extraInformation);
	}
	
	protected void fireItemAdded(Item item) {
		fireDeltaDataSetEvent(item, null, new ItemAddedDispatcher());
	}
	
	protected void fireItemDeleted(Item item) {
		fireDeltaDataSetEvent(item, null, new ItemDeletedDispatcher());
	}
	
	protected void fireCharacterAdded(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterAddedDispatcher());
	}
	
	protected void fireCharacterDeleted(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterDeletedDispatcher());
	}
	
	protected void fireCharacterMoved(Character character, int oldCharacterNumber) {
		fireDeltaDataSetEvent(null, character, oldCharacterNumber, new CharacterMovedDispatcher());
	}
	
	protected void fireItemMoved(Item item, int oldItemNumber) {
		fireDeltaDataSetEvent(item, null, oldItemNumber, new ItemMovedDispatcher());
	}

	@Override
	public void characterChanged(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterEditedDispatcher());
	}

	@Override
	public void itemChanged(Item item, Attribute attribute) {
		fireDeltaDataSetEvent(item, null, new ItemEditedDispatcher());
	}
	

	protected abstract class DataSetEventDispatcher {

		public void fireDataSetEvent(Item item, Character character, Object extraInformation) {

			DeltaDataSetChangeEvent dataSetChangeEvent = new DeltaDataSetChangeEvent(AbstractObservableDataSet.this,
					character, item, extraInformation);
			fireDataSetEvent(dataSetChangeEvent);	
		}
		
		public void fireDataSetEvent(Item item, Character character) {
			DeltaDataSetChangeEvent dataSetChangeEvent = new DeltaDataSetChangeEvent(AbstractObservableDataSet.this,
					character, item);
			fireDataSetEvent(dataSetChangeEvent);
		}
		
		protected void fireDataSetEvent(DeltaDataSetChangeEvent event) {
			// process in reverse order to support removal during processing.
			for (int i = _observerList.size() - 1; i >= 0; i--) {
				doFireEvent((DeltaDataSetObserver) _observerList.get(i), event);
			}
		}

		public abstract void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event);
	}

	protected class ItemAddedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemAdded(event);
		}
	}

	protected class ItemEditedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemEdited(event);
		}
	}
	protected class ItemMovedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemMoved(event);
		}
	}
	
	protected class ItemDeletedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.itemDeleted(event);
		}
	}

	protected class CharacterEditedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterEdited(event);
		}
	}

	protected class CharacterAddedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterAdded(event);
		}
	}
	
	protected class CharacterDeletedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterDeleted(event);
		}
	}
	
	protected class CharacterMovedDispatcher extends DataSetEventDispatcher {
		@Override
		public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			observer.characterMoved(event);
		}
	}
}
