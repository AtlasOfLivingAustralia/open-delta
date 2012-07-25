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

import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.ObservableDeltaDataSet;
import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.util.Predicate;
import au.org.ala.delta.util.Visitor;

import java.util.Collection;
import java.util.prefs.PreferenceChangeListener;

public interface EditorViewModel extends ObservableDeltaDataSet {

	public void setSelectedItem(Item selectedItem);

	public void setSelectedCharacter(Character selectedCharacter);

	public Item getSelectedItem();

	public Character getSelectedCharacter();

	public void deleteItem(Item item);

	public String getName();

	public String getShortName();

	public void setName(String name);

	public void close();
	
	public void addPreferenceChangeListener(PreferenceChangeListener listener); 
	
	public void removePreferenceChangeListener(PreferenceChangeListener listener);

	public abstract String getImagePath();
	
	public ImageSettings getImageSettings();

	public void setSelectedState(int stateNo);
	
	public int getSelectedState();

	void setSelectedImage(Image image);

	Image getSelectedImage();

	String getDataSetPath();
	
	public int getDirectiveFileCount();
	
	public DirectiveFile getDirectiveFile(int directiveFileNumber);
	
	public DirectiveFile getDirectiveFile(String fileName);
	
	public void deleteDirectiveFile(DirectiveFile file);
	
	public DirectiveFile getSelectedDirectiveFile();
	
	public void setSelectedDirectiveFile(DirectiveFile file);
	
	public DirectiveFile addDirectiveFile(int fileNumber, String fileName, DirectiveType type);
	
	public void visitCharacters(Visitor<Character> visitor);
	
	public Collection<Character> selectCharacters(Predicate<Character> predicate);

	public Character firstCharacter(Predicate<Character> predicate, int startIndex, SearchDirection direction);
	
	public Item firstItem(Predicate<Item> predicate, int startIndex, SearchDirection direction);
	
	public String getExportPath();

	public void setExportPath(String path);
	
	public String displayTextFromAttributeValue(Attribute attribute, String attributeText);
	
	public String attributeValueFromDisplayText(Attribute attribute, String attributeDisplayText);

    public void setModified(boolean modified);
}
