package au.org.ala.delta.editor.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.observer.CharacterObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;
import au.org.ala.delta.model.observer.ItemObserver;


/**
 * Maintains the current overall state of the DELTA Editor.
 */
public class EditorDataModel implements DeltaDataSet, ItemObserver, CharacterObserver {

	/** The currently selected data set */
	private DeltaDataSet _currentDataSet;
	
	/** The number of the currently selected character */
	private Character _selectedCharacter;
	
	/** The number of the currently selected item */
	private Item _selectedItem;
	
	/** Helper class for notifying interested parties of property changes */
	private PropertyChangeSupport _propertyChangeSupport;
	
	private List<DeltaDataSetObserver> _observerList = new ArrayList<DeltaDataSetObserver>();
	
	public EditorDataModel(DeltaDataSet dataSet) {
		_currentDataSet = dataSet;
		_propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(listener);
		
		if (_propertyChangeSupport.getPropertyChangeListeners().length == 0) {
			close();
		}
	}
	
	public void setCurrentDataSet(DeltaDataSet dataSet) {
		_currentDataSet = dataSet;
		_selectedCharacter = null;
		_selectedItem = null;
	}
	
	public void setSelectedItem(Item selectedItem) {
		_selectedItem = selectedItem;
	}
	
	public void setSelectedCharacter(Character selectedCharacter) {
		_selectedCharacter = selectedCharacter;
	}
	
	public Item getSelectedItem() {
		return _selectedItem;
	}
	
	public Character getSelectedCharacter() {
		return _selectedCharacter;
	}
	
	public DeltaDataSet getCurrentDataSet() {
		return _currentDataSet;
	}
	
	
	@Override
	public String getName() {
		String name = _currentDataSet.getName();
		if (name == null) {
			name = "";
		}
		return name;
	}
	
	public void setName(String name) {
		
		_currentDataSet.setName(name);
		
		_propertyChangeSupport.firePropertyChange("name", null, name);
	}

	@Override
	public Item getItem(int number) {
		Item item = _currentDataSet.getItem(number);
		item.addItemObserver(this);
		return item;
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		return _currentDataSet.getAttributeAsString(itemNumber, characterNumber);
	}

	@Override
	public Character getCharacter(int number) {
		Character character = _currentDataSet.getCharacter(number);
		character.addCharacterObserver(this);
		return character;
	}

	@Override
	public int getNumberOfCharacters() {
		return _currentDataSet.getNumberOfCharacters();
	}

	@Override
	public int getMaximumNumberOfItems() {
		return _currentDataSet.getMaximumNumberOfItems();
	}
	
	@Override
	public void close() {
		_currentDataSet.close();
	}
	
	@Override
	public Character addCharacter(CharacterType type) {
		Character character = _currentDataSet.addCharacter(type);
		character.addCharacterObserver(this);
		fireDeltaDataSetEvent(null, character, new CharacterAddedDispatcher());
		return character;
	}
	
	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		Character character = _currentDataSet.addCharacter(characterNumber, type);
		character.addCharacterObserver(this);
		fireDeltaDataSetEvent(null, character, new CharacterAddedDispatcher());
		return character;
	}

	@Override
	public Item addItem(int itemNumber) {
		Item item = _currentDataSet.addItem(itemNumber);
		item.addItemObserver(this);
		fireDeltaDataSetEvent(item, null, new ItemAddedDispatcher());
		return item;
	}

	@Override
	public Item addItem() {
		Item item = _currentDataSet.addItem();
		item.addItemObserver(this);
		fireDeltaDataSetEvent(item, null, new ItemAddedDispatcher());
		return item;
	}
	
	private void fireDeltaDataSetEvent(Item item, Character character, DataSetEventDispatcher dispatcher) {
		dispatcher.fireDataSetEvent(item, character);
	}
	
	@Override
	public void characterChanged(Character character) {
		fireDeltaDataSetEvent(null, character, new CharacterEditedDispatcher());
		
	}

	@Override
	public void itemChanged(Item item) {
		fireDeltaDataSetEvent(item, null, new ItemEditedDispatcher());
	}

	public void addDeltaDataSetObserver(DeltaDataSetObserver observer) {
	     _observerList.add(observer);
	 }

	 public void removeDeltaDataSetObserver(DeltaDataSetObserver observer) {
	     _observerList.remove(observer);
	 }

	 private abstract class DataSetEventDispatcher {
		
		 public void fireDataSetEvent(Item item, Character character) {
			 
			 DeltaDataSetChangeEvent dataSetChangeEvent = new DeltaDataSetChangeEvent(
					 EditorDataModel.this, character, item);
            
			 // process in reverse order to support removal during processing.
		     for (int i = _observerList.size()-1; i>=0; i--) {
		        	doFireEvent((DeltaDataSetObserver)_observerList.get(i), dataSetChangeEvent); 
		     }
		 }
		 
		 public abstract void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event);
	 }
	 
	 private class ItemAddedDispatcher extends DataSetEventDispatcher {
		 @Override
			public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			 observer.itemAdded(event);
			}
	 }
	 
	 private class ItemEditedDispatcher extends DataSetEventDispatcher {
		 @Override
			public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			 observer.itemEdited(event);
			}
	 }
	 
	 private class CharacterEditedDispatcher extends DataSetEventDispatcher {
		 @Override
			public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			 observer.characterEdited(event);
			}
	 }
	 
	 private class CharacterAddedDispatcher extends DataSetEventDispatcher {
		 @Override
			public void doFireEvent(DeltaDataSetObserver observer, DeltaDataSetChangeEvent event) {
			 observer.characterAdded(event);
			}
	 }

}
