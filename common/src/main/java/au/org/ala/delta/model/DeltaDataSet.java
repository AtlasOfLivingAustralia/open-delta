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
package au.org.ala.delta.model;

/**
 * Represents a single DELTA data set, consisting of a List of characters and a List of Items.
 */
public interface DeltaDataSet {
	
	public String getName();
	
	public void setName(String name);
	
	public Item getItem(int number);
	
	public String getAttributeAsString(int itemNumber, int characterNumber);

	public Character getCharacter(int number);

	public int getNumberOfCharacters();

	public int getMaximumNumberOfItems();
	
	/**
	 * Closes this DeltaDataSet, allowing it to release any resources it may have aquired.
	 */
	public void close();
	
	
	// Creation methods
	/**
	 * Creates a new Character and adds it to this DeltaDataSet.
	 * @param type the type of Character to create.
	 * @return the new Character.
	 */
	public Character addCharacter(CharacterType type);
	
	/**
	 * Creates a new Character and adds it to this DeltaDataSet.
	 * @param characterNumber the number that will identify the new character.
	 * @param type the type of Character to create.
	 * @return the new Character.
	 */
	public Character addCharacter(int characterNumber, CharacterType type);
	
	/**
	 * Creates a new Item and adds it to this DeltaDataSet.
	 * @return the new Item.
	 */
	public Item addItem();
	
	/**
	 * Creates a new Item and adds it to this DeltaDataSet.
	 * @param itemNumber the number that will identify the new item
	 * @return the new Item.
	 */
	public Item addItem(int itemNumber);
	
}
