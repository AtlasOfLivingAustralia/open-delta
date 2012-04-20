/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.ObservableDeltaDataSet;
import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.util.Predicate;
import au.org.ala.delta.util.Visitor;

/**
 * Provides the UI model with a backing DeltaDataSet. Each EditorDataModel is associated with a single view component. This class maintains a separate list of DeltaDataSetObservers to allow clean
 * removal of listeners from the backing DeltaDataSet when a view of the model is closed.
 */
public class EditorDataModel extends DataSetWrapper implements EditorViewModel, PreferenceChangeListener {

	/** The number of the currently selected character */
	private Character _selectedCharacter;

	/** The number of the currently selected item */
	private Item _selectedItem;

	/**
	 * The number of the selected state. Only valid when the selected character is a multistate character (otherwise it's -1).
	 */
	private int _selectedState;

	/** the currently selected image */
	private Image _selectedImage;

	/** The currently selected directive file */
	private DirectiveFile _selectedDirectiveFile;

	/** Helper class for notifying interested parties of property changes */
	private PropertyChangeSupport _propertyChangeSupport;

	private List<PreferenceChangeListener> _preferenceChangeListeners;
	
	/** The path at which the last data set export was done */
	private String _exportPath;

	/** Keeps track of whether this data set has been modified */
	private boolean _modified;

	public EditorDataModel(AbstractObservableDataSet dataSet) {
		super(dataSet);
		_propertyChangeSupport = new PropertyChangeSupport(this);
		_preferenceChangeListeners = new ArrayList<PreferenceChangeListener>();
		_selectedState = -1;
		_selectedCharacter = null;
		_selectedItem = null;
		EditorPreferences.addPreferencesChangeListener(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void setSelectedItem(Item selectedItem) {
		_selectedItem = selectedItem;
	}

	@Override
	public void setSelectedCharacter(Character selectedCharacter) {
		if (_selectedCharacter == null || selectedCharacter == null || !_selectedCharacter.equals(selectedCharacter)) {
			_selectedState = -1;
		}

		_selectedCharacter = selectedCharacter;
	}

	@Override
	public void setSelectedState(int state) {
		if (!(_selectedCharacter instanceof MultiStateCharacter)) {
			_selectedState = -1;
		}
		_selectedState = state;
	}

	@Override
	public int getSelectedState() {
		return _selectedState;
	}

	@Override
	public Item getSelectedItem() {
		return _selectedItem;
	}

	@Override
	public Character getSelectedCharacter() {
		return _selectedCharacter;
	}

	@Override
	public Image getSelectedImage() {
		return _selectedImage;
	}

	@Override
	public void setSelectedImage(Image image) {
		_selectedImage = image;
	}

	@Override
	public DirectiveFile getSelectedDirectiveFile() {
		return _selectedDirectiveFile;
	}

	@Override
	public void setSelectedDirectiveFile(DirectiveFile file) {
		_selectedDirectiveFile = file;
	}

	public ObservableDeltaDataSet getDeltaDataSet() {
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

	@Override
	public String getShortName() {
		String name = _wrappedDataSet.getName();
		if (name == null) {
			name = "";
		}
		name = new File(name).getName();
		return name;
	}

	@Override
	public String getImagePath() {
		ImageSettings settings = getImageSettings();
		String imagePath = settings.getFirstResourcePathLocation();
		File file = new File(imagePath);
		if (!file.isAbsolute()) {
			imagePath = getDataSetPath() + imagePath;
		}

		return imagePath;
	}

	@Override
	public ImageSettings getImageSettings() {
		ImageSettings settings = _wrappedDataSet.getImageSettings();
		if (settings == null) {
			settings = new ImageSettings();
		}
		settings.setDataSetPath(getDataSetPath());
		return settings;
	}

	@Override
	public String getDataSetPath() {
		File name = new File(getName());
		String dataSetFolder = "";
		if (name.isAbsolute()) {
			dataSetFolder = name.getParent() + File.separator;
		}
		return dataSetFolder;
	}

	@Override
	public void setName(String name) {

		_wrappedDataSet.setName(name);
		_propertyChangeSupport.firePropertyChange("name", null, name);
	}

	@Override
	public void close() {
		EditorPreferences.removePreferenceChangeListener(this);
		_wrappedDataSet.removeDeltaDataSetObserver(this);
		_wrappedDataSet.close();
	}

	public DirectiveFile addDirectiveFile(int fileNumber, String fileName, DirectiveType type) {
		setModified(true);
		return slotFileDataSet().addDirectiveFile(fileNumber, fileName, type);
	}

	public int getDirectiveFileCount() {
		return slotFileDataSet().getDirectiveFileCount();
	}

	public DirectiveFile getDirectiveFile(int fileNumber) {
		return slotFileDataSet().getDirectiveFile(fileNumber);
	}

	@Override
	public DirectiveFile getDirectiveFile(String fileName) {
		return slotFileDataSet().getDirectiveFile(fileName);
	}

	public void deleteDirectiveFile(DirectiveFile file) {
		setModified(true);
		slotFileDataSet().deleteDirectiveFile(file);
	}

	private SlotFileDataSet slotFileDataSet() {
		return (SlotFileDataSet) _wrappedDataSet;
	}

	public boolean isModified() {
		return _wrappedDataSet.isModified();
	}

	public void setModified(boolean modified) {

		if (modified != _modified) {
			_propertyChangeSupport.firePropertyChange("modified", _modified, modified);
		}
		_modified = modified;
	}

	public void addPreferenceChangeListener(PreferenceChangeListener listener) {
		_preferenceChangeListeners.add(listener);
	}

	public void removePreferenceChangeListener(PreferenceChangeListener listener) {
		_preferenceChangeListeners.remove(listener);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		for (PreferenceChangeListener listener : _preferenceChangeListeners) {
			listener.preferenceChange(evt);
		}
	}

	@Override
	public void itemAdded(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.itemAdded(event);
	}

	@Override
	public void itemDeleted(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.itemDeleted(event);
	}

	@Override
	public void itemMoved(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.itemMoved(event);
	}

	@Override
	public void itemEdited(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.itemEdited(event);
	}

	@Override
	public void characterAdded(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.characterAdded(event);
	}

	@Override
	public void characterDeleted(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.characterDeleted(event);
	}

	@Override
	public void characterMoved(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.characterMoved(event);
	}

	@Override
	public void characterEdited(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.characterEdited(event);
	}

	@Override
	public void characterTypeChanged(DeltaDataSetChangeEvent event) {
		Character oldCharacter = event.getCharacter();
		Character newCharacter = (Character) event.getExtraInformation();
		if (_selectedCharacter == oldCharacter) {
			_selectedCharacter = newCharacter;
		}
		super.characterTypeChanged(event);
	}

	@Override
	public void imageEdited(DeltaDataSetChangeEvent event) {
		setModified(true);
		super.imageEdited(event);
	}

	@Override
	public void visitCharacters(Visitor<Character> visitor) {
		if (visitor != null) {
			for (int i = 1; i <= getNumberOfCharacters(); ++i) {
				Character character = getCharacter(i);
				if (!visitor.visit(character)) {
					break;
				}
			}
		}
	}

	@Override
	public Collection<Character> selectCharacters(Predicate<Character> predicate) {
		ArrayList<Character> results = new ArrayList<Character>();
		for (int i = 1; i <= getNumberOfCharacters(); ++i) {
			Character ch = getCharacter(i);
			if (predicate.test(ch)) {
				results.add(ch);
			}
		}
		return results;
	}

	@Override
	public Character firstCharacter(Predicate<Character> predicate, int startIndex, SearchDirection direction) {
		if (direction == SearchDirection.Forward) {
			for (int i = startIndex; i <= getNumberOfCharacters(); ++i) {
				Character ch = getCharacter(i);
				if (predicate.test(ch)) {
					return ch;
				}
			}
		} else {
			for (int i = startIndex; i >= 1; --i) {
				Character ch = getCharacter(i);
				if (predicate.test(ch)) {
					return ch;
				}
			}
		}

		return null;
	}
	
	@Override
	public Item firstItem(Predicate<Item> predicate, int startIndex, SearchDirection direction) {
		if (direction == SearchDirection.Forward) {
			for (int i = startIndex; i <= getMaximumNumberOfItems(); ++i) {
				Item item = getItem(i);
				if (predicate.test(item)) {
					return item;
				}
			}
		} else {
			for (int i = startIndex; i >= 1; --i) {
				Item item = getItem(i);
				if (predicate.test(item)) {
					return item;
				}
			}
		}

		return null;
	}
	

	@Override
	public String getExportPath() {
		
		return _exportPath;
	}

	@Override
	public void setExportPath(String path) {
		_exportPath = path;
	}

	@Override
	public String displayTextFromAttributeValue(Attribute attribute, String attributeText) {
		Character character = attribute.getCharacter();
		if (character.getCharacterType().isText()) {
			if (StringUtils.isNotBlank(attributeText) && attributeText.length() >= 2) {
				// Remove the surrouding <>.
				attributeText = attributeText.substring(1, attributeText.length()-1);
			}
		}
		return attributeText;
	}

	@Override
	public String attributeValueFromDisplayText(Attribute attribute, String attributeDisplayText) {
		 
		Character character = attribute.getCharacter();
		if (character.getCharacterType().isText()) {
			
			if (!Attribute.UNKNOWN.equals(attributeDisplayText) && !Attribute.INAPPICABLE.equals(attributeDisplayText)) {
				attributeDisplayText = "<" + attributeDisplayText + ">";
			}
		}
		return attributeDisplayText;
	}
	
	@Override
    public String getAttributeAsString(int itemNumber, int characterNumber) {
		
		Attribute attribute = getAttribute(itemNumber, characterNumber);
		
		return displayTextFromAttributeValue(attribute, _wrappedDataSet.getAttributeAsString(itemNumber, characterNumber));
		
	}
	
	
}
