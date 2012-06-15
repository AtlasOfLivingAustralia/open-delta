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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.util.Predicate;
import au.org.ala.delta.util.Visitor;

/**
 * The DeltaViewModel is designed to provide each view with a separate instance of the model to work with. The main reason for this is to allow components of the view to register themselves as
 * observers of the data model without needing to track them carefully and deregister them individually when the view closes. PreferenceChangeListener support has been implemented with the same
 * philosophy.
 */
public class DeltaViewModel extends DataSetWrapper implements EditorViewModel, PreferenceChangeListener {

	/** The model we delegate to */
	private EditorDataModel _editorDataModel;

	/** A list of objects interested in being notified of preference changes */
	private List<PreferenceChangeListener> _preferenceChangeListeners;

	/** The currently selected character */
	private Character _selectedCharacter;

	/** the currently selected image */
	private Image _selectedImage;

	/** The currently selected directive file */
	private DirectiveFile _selectedDirectiveFile;

	/**
	 * The number of the selected state. Only valid when the selected character is a multistate character (otherwise it's -1).
	 */
	private int _selectedState;

	/** The currently selected item */
	private Item _selectedItem;

	public DeltaViewModel(EditorDataModel model) {
		super(model);
		_editorDataModel = model;
		_editorDataModel.addPreferenceChangeListener(this);
		_preferenceChangeListeners = new ArrayList<PreferenceChangeListener>();
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
	public void characterTypeChanged(DeltaDataSetChangeEvent event) {
		Character oldCharacter = event.getCharacter();
		Character newCharacter = (Character) event.getExtraInformation();
		if (_selectedCharacter == oldCharacter) {
			_selectedCharacter = newCharacter;
		}
		super.characterTypeChanged(event);
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
	public String getShortName() {
		return _editorDataModel.getShortName();
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

	public String getImagePath() {
		return _editorDataModel.getImagePath();
	}

	public String getDataSetPath() {
		return _editorDataModel.getDataSetPath();
	}

	@Override
	public int getDirectiveFileCount() {
		return _editorDataModel.getDirectiveFileCount();
	}

	@Override
	public DirectiveFile getDirectiveFile(int directiveFileNumber) {
		return _editorDataModel.getDirectiveFile(directiveFileNumber);
	}

	@Override
	public void deleteDirectiveFile(DirectiveFile file) {
		_editorDataModel.deleteDirectiveFile(file);
	}

	@Override
	public DirectiveFile addDirectiveFile(int fileNumber, String fileName, DirectiveType type) {
		return _editorDataModel.addDirectiveFile(fileNumber, fileName, type);
	}

	@Override
	public DirectiveFile getSelectedDirectiveFile() {
		return _selectedDirectiveFile;
	}

	@Override
	public void setSelectedDirectiveFile(DirectiveFile file) {
		_selectedDirectiveFile = file;
	}

	@Override
	public DirectiveFile getDirectiveFile(String fileName) {
		return _editorDataModel.getDirectiveFile(fileName);
	}

	@Override
	public void visitCharacters(Visitor<Character> visitor) {
		_editorDataModel.visitCharacters(visitor);
	}

	@Override
	public Collection<Character> selectCharacters(Predicate<Character> predicate) {
		return _editorDataModel.selectCharacters(predicate);
	}

	@Override
	public Character firstCharacter(Predicate<Character> predicate, int startIndex, SearchDirection direction) {
		return _editorDataModel.firstCharacter(predicate, startIndex, direction);
	}

	@Override
	public Item firstItem(Predicate<Item> predicate, int startIndex, SearchDirection direction) {
		return _editorDataModel.firstItem(predicate, startIndex, direction);
	}

	@Override
	public String getExportPath() {
		return _editorDataModel.getExportPath();
	}

	@Override
	public void setExportPath(String path) {
		_editorDataModel.setExportPath(path);
	}

	@Override
	public String displayTextFromAttributeValue(Attribute attribute, String attributeText) {
		return _editorDataModel.displayTextFromAttributeValue(attribute, attributeText);
	}

	@Override
	public String attributeValueFromDisplayText(Attribute attribute, String attributeDisplayText) {
		return _editorDataModel.attributeValueFromDisplayText(attribute, attributeDisplayText);
	}

    @Override
    public void setImageSettings(ImageSettings imageSettings) {
        _editorDataModel.setImageSettings(imageSettings);
    }
}
