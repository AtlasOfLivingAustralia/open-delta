package au.org.ala.delta.gui;

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
	
	
	public EditorDataModel(DeltaDataSet dataSet) {
		_currentDataSet = dataSet;
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
	
	
	
	
	
}
