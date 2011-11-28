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

import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.impl.ControllingInfo;

public interface DeltaDataSet {
    
    /**
     * Closes this DeltaDataSet, allowing it to release any resources it may have aquired.
     */
    public void close();
    
    public String getName();

    public Item getItem(int number);

    public List<Item> getItemsAsList();

    public String getAttributeAsString(int itemNumber, int characterNumber);

    public Character getCharacter(int number);

    public List<Character> getCharactersAsList();

    public int getNumberOfCharacters();

    public int getMaximumNumberOfItems();

    public Attribute getAttribute(int itemNumber, int characterNumber);
    
    public List<Attribute> getAllAttributesForCharacter(int characterNumber);

    /**
     * Returns Items that do not have an attribute coded for the supplied
     * Character and have not been made inapplicable. If the character has an
     * implicit value no Items will be returned as the implicit value is
     * sufficient to treat as coded.
     * 
     * @param character
     *            the character to check the coding for.
     * @return a List of items that do not have a coded attribute for the
     *         supplied Character. If all Items are coded an empty List will be
     *         returned.
     */
    public List<Item> getUncodedItems(Character character);

    /**
     * Returns Items that have more than one state of the supplied Character
     * present. Items with a variable attribute will also be returned.
     * 
     * @param character
     *            the Character to check the coding for.
     * @return a List of Items with more than one state coded for the supplied
     *         Character. If no Items meet the criteria, an empty List will be
     *         returned.
     */
    public List<Item> getItemsWithMultipleStatesCoded(MultiStateCharacter character);

    public List<CharacterDependency> getAllCharacterDependencies();

    /**
     * Returns information about how character and taxon images are displayed.
     */
    public ImageSettings getImageSettings();

    /**
     * Returns the item with the supplied description. Descriptions are matched
     * after any RTF formatting has been removed.
     * 
     * @param description
     *            the description of the Item to return.
     * @return the first item found with the supplied description, null if no
     *         such Item can be found.
     */
    public Item itemForDescription(String description);

    /**
     * Checks if the supplied character has been made inapplicable by the
     * attributes coded for the supplied item.
     * 
     * @param character
     *            the character to check.
     * @param item
     *            the item to check.
     * @return information about whether the character has been made
     *         inapplicable in the context of the supplied Item.
     */
    public ControllingInfo checkApplicability(Character character, Item item);

    /**
     * Checks if the value of a character has been coded for a particular item.
     * The rules are:
     * <ul>
     * <li>Characters that have an implicit value are never considered to be
     * uncoded.</li>
     * <li>Characters that have been made inapplicable for the supplied item are
     * not considered to be uncoded.</li>
     * <li>Otherwise if the attribute for the character is absent, explicitly
     * coded as unknown (U) or just a comment (except for text characters) the
     * attribute is considered to be uncoded.
     * </ul>
     * 
     * @param item
     *            the item to check.
     * @param character
     *            the character to check.
     * @return true if there is no attribute coded for the supplied character
     *         and item.
     */
    public boolean isUncoded(Item item, Character character);
}
