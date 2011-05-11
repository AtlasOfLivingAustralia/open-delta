package au.org.ala.delta.editor.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;


/**
 * Provides the UI model with a backing DeltaDataSet.  Each EditorDataModel is associated with a 
 * single view component.  This class maintains a separate list of DeltaDataSetObservers 
 * to allow clean removal of listeners from the backing DeltaDataSet when a view of the model is 
 * closed. 
 */
public class EditorDataModel extends DataSetWrapper {

	/** The number of the currently selected character */
	private Character _selectedCharacter;
	
	/** The number of the currently selected item */
	private Item _selectedItem;
	
	/** Helper class for notifying interested parties of property changes */
	private PropertyChangeSupport _propertyChangeSupport;
	
	public EditorDataModel(AbstractObservableDataSet dataSet) {
		super(dataSet);
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
		return _wrappedDataSet;
	}
	
	@Override
	public void deleteItem(Item item) {
		_wrappedDataSet.deleteItem(item);
		if (_selectedItem != null && _selectedItem.equals(item)) {
			_selectedItem = null;
		}
	}
	

	@Override
	public String getName() {
		String name = _wrappedDataSet.getName();
		if (name == null) {
			name = "";
		}
		return name;
	}
	public String getShortName() {
		String name = _wrappedDataSet.getName();
		if (name == null) {
			name = "";
		}
		name = new File(name).getName();
		return name;
	}
	
	public void setName(String name) {
		
		_wrappedDataSet.setName(name);
		
		_propertyChangeSupport.firePropertyChange("name", null, name);
	}

	@Override
	public void close() {
		_wrappedDataSet.removeDeltaDataSetObserver(this);
		_wrappedDataSet.close();
	}
}
