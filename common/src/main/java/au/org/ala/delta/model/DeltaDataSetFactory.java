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

import java.util.Set;


/**
 * An abstract factory for creating DELTA model objects.
 */
public interface DeltaDataSetFactory {

	/**
	 * Creates a new DeltaDataSet with the supplied name.
	 */
	public MutableDeltaDataSet createDataSet(String name);
	
	/**
	 * Creates an item with the supplied number.
	 * @param number the number of the item.
	 * @return a new Item.
	 */
	public Item createItem(int number);
	
	/**
	 * Creates a new variant Item with the supplied number and parent Item.
	 * @param parent the parent Item for the new Item.
	 * @param itemNumber the number for the new Item.
	 * @return a new variant Item.
	 */
	public Item createVariantItem(Item parent, int itemNumber);
	
	/**
	 * Creates a new Character of the specified type.
	 * @param type the type of Character to create.
	 * @param number the character number.
	 * @return a new Character of the specified type.
	 */
	public Character createCharacter(CharacterType type, int number);
	
	/**
	 * Creates a new Attribute for the supplied Character and Item
	 * @param character the Character that the Attribute will reference
	 * @param item the Item that the Character will reference
	 * @return a new Attribute
	 */
	public Attribute  createAttribute(Character character, Item item);

	
	public CharacterDependency createCharacterDependency(
			MultiStateCharacter owningCharacter, Set<Integer> states, Set<Integer> dependentCharacters);
	
}
