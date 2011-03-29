package au.org.ala.delta.editor.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;


/**
 * Provides the UI model with a backing DeltaDataSet.  Each EditorDataModel is associated with a 
 * single view component.  This class maintains a separate list of DeltaDataSetObservers 
 * to allow clean removal of listeners from the backing DeltaDataSet when a view of the model is 
 * closed. 
 */
public class EditorDataModel implements DeltaDataSet, DeltaDataSetObserver {

	/** The currently selected data set */
	private AbstractObservableDataSet _currentDataSet;
	
	/** The number of the currently selected character */
	private Character _selectedCharacter;
	
	/** The number of the currently selected item */
	private Item _selectedItem;
	
	/** Helper class for notifying interested parties of property changes */
	private PropertyChangeSupport _propertyChangeSupport;
	
	/** Maintains a list of objects interested in being notified of changes to this model */
	private List<DeltaDataSetObserver> _observerList = new ArrayList<DeltaDataSetObserver>();
	
	public EditorDataModel(AbstractObservableDataSet dataSet) {
		setCurrentDataSet(dataSet);
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
	
	public void setCurrentDataSet(AbstractObservableDataSet dataSet) {
		if (_currentDataSet != null) {
			_currentDataSet.removeDeltaDataSetObserver(this);
		}
		_currentDataSet = dataSet;
		_currentDataSet.addDeltaDataSetObserver(this);
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
	
	public AbstractObservableDataSet getCurrentDataSet() {
		return _currentDataSet;
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
		return item;
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		return _currentDataSet.getAttributeAsString(itemNumber, characterNumber);
	}

	@Override
	public Character getCharacter(int number) {
		Character character = _currentDataSet.getCharacter(number);
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
		_currentDataSet.removeDeltaDataSetObserver(this);
		_currentDataSet.close();
	}
	
	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		Character character = _currentDataSet.addCharacter(characterNumber, type);
		return character;
	}
	
	@Override
	public Character addCharacter(CharacterType type) {
		Character character = _currentDataSet.addCharacter(type);
		return character;
	}

	@Override
	public Item addItem(int itemNumber) {
		Item item = _currentDataSet.addItem(itemNumber);
		return item;
	}

	@Override
	public Item addItem() {
		Item item = _currentDataSet.addItem();
		return item;
	}
	
	@Override
	public boolean isModified() {
		return _currentDataSet.isModified();
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
}
