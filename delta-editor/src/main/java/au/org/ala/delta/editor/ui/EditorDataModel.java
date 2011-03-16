package au.org.ala.delta.editor.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;


/**
 * Maintains the current overall state of the DELTA Editor.
 */
public class EditorDataModel implements DeltaDataSet {

	/** The currently selected data set */
	private DeltaDataSet _currentDataSet;
	
	/** The number of the currently selected character */
	private Character _selectedCharacter;
	
	/** The number of the currently selected item */
	private Item _selectedItem;
	
	/** Helper class for notifying interested parties of property changes */
	private PropertyChangeSupport _propertyChangeSupport;
	
	
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
		return _currentDataSet.getName();
	}
	
	public void setName(String name) {
		
		_currentDataSet.setName(name);
		
		_propertyChangeSupport.firePropertyChange("name", null, name);
	}

	@Override
	public Item getItem(int number) {
		
		return _currentDataSet.getItem(number);
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		return _currentDataSet.getAttributeAsString(itemNumber, characterNumber);
	}

	@Override
	public Character getCharacter(int number) {
		return _currentDataSet.getCharacter(number);
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

	private class PropertyChangeDetector implements InvocationHandler {

		/**
		 * Attempts to detect when properties have changed using the fact that the method starts with "set".
		 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			Object result = method.invoke(proxy, args);
			String methodName = method.getName();
			if (methodName.startsWith("set")) {
				// Bit of a lazy property change - we aren't respecting the "old value" and are assuming
				// that there is a single argument to the method containing the new value. 
				String propertyName = methodName.substring(3);
				_propertyChangeSupport.firePropertyChange(propertyName, null, args[0]);
			}
			
			return result;
		}
		
	}
	
	
	
}
