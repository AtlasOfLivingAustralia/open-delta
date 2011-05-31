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

import java.util.List;
import java.util.Set;


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
	 * @return true if this DeltaDataSet has been modified since it was opened.  An unchanged 
	 * new dataset should return false.
	 */
	public boolean isModified();
	
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
	 * Deletes the supplied Character from the data set.
	 * @param character the character to delete.
	 */
	public void deleteCharacter(Character character);
	
	/**
	 * Changes the position in the data set of a specific Character.  The Character, and all
	 * subsequent Characters will be renumbered.
	 * @param character the Character to move.
	 * @param newCharacterNumber the new character number for the Item.
	 */
	public void moveCharacter(Character character, int newCharacterNumber);
	
	/**
	 * Creates a new Item and adds it to this DeltaDataSet.
	 * @return the new Item.
	 */
	public Item addItem();
	
	/**
	 * Creates a new Item and adds it to this DeltaDataSet.  If the supplied item number is
	 * already used by an Item, this operation will be treated as an insert - the new item
	 * will take the supplied number and subsequent items will be re-numbered.
	 * @param itemNumber the number that will identify the new item
	 * @return the new Item.
	 */
	public Item addItem(int itemNumber);
	
	public Item addVariantItem(int parentItemNumber, int itemNumber);
	
	public Attribute addAttribute(int itemNumber, int characterNumber);
	
	public Attribute getAttribute(int itemNumber, int characterNumber);

	/**
	 * Deletes the supplied Item from the data set.
	 * @param item the item to delete.
	 */
	public void deleteItem(Item item);
	
	/**
	 * Changes the position in the item list of a specific item.  The Item, and all subsequent
	 * Items will be renumbered.
	 * @param item the Item to move.
	 * @param newItemNumber the new item number for the Item.
	 */
	public void moveItem(Item item, int newItemNumber);
	
	/**
	 * Deletes the state numbered stateNumber from the supplied MultiStateCharacter. 
	 * @param character the character to delete the state from.
	 * @param stateNumber the state number to delete.
	 */
	public void deleteState(MultiStateCharacter character, int stateNumber);
	
	/**
	 * Returns Items that do not have an attribute coded for the supplied Character and have
	 * not been made inapplicable.
	 * If the character has an implicit value no Items will be returned as the implicit value
	 * is sufficient to treat as coded.
	 * @param character the character to check the coding for.
	 * @return a List of items that do not have a coded attribute for the supplied Character.  
	 * If all Items are coded an empty List will be returned.
	 */
	public List<Item> getUncodedItems(Character character);

	/**
	 * Returns Items that have more than one state of the supplied Character present.
	 * Items with a variable attribute will also be returned.
	 * @param character the Character to check the coding for.
	 * @return a List of Items with more than one state coded for the supplied Character.
	 * If no Items meet the criteria, an empty List will be returned.
	 */
	public List<Item> getItemsWithMultipleStatesCoded(MultiStateCharacter character);
	
	/**
	 * Changes the type of the supplied Character.  A Character of the new type will be returned
	 * and should be used instead of any references to the previous Character that still may
	 * exist.
	 * @param character the Character to change.
	 * @param newType the new CharacterType for the character.
	 * @return a Character of the new type.
	 */
	public Character changeCharacterType(Character character, CharacterType newType);
	
	/**
     * Character type changes are allowed as follows:
     * If the character has no data coded, it's type can be changed.
     * If the character is a text character, it's type can be changed.
     * If the character is a multistate character, it's type can be changed to another 
     * multistate character only.
     * If the character is a numeric character, it's type can be changed to another numeric
     * character only.
     * @param character the character thats type is to be changed.
     * @param newType the new type for the character
     * @return true if the changeCharacterType operation will succeed.
     */
	public boolean canChangeCharacterType(Character character, CharacterType newType);
	
	public List<CharacterDependency> getAllCharacterDependencies();
	
	
	public CharacterDependency addCharacterDependency(MultiStateCharacter owningCharacter, Set<Integer> states, Set<Integer> dependentCharacters);
}
