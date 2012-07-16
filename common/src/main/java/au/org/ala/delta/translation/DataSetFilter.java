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
package au.org.ala.delta.translation;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public interface DataSetFilter {

	/**
	 * Filters the supplied Item.
	 * @param item the Item to filter.
	 * @return true if the item should be included in the translation.
	 */
	public boolean filter(Item item);

	/**
	 * Filters the supplied Attribute, identified by the supplied Item and Character.
	 * @param item specifies the Attribute's Item
     * @param character specifies the Attribute's Character
	 * @return true if the attribute should be included in the translation.
	 */
	public boolean filter(Item item, Character character);
	
	/**
	 * Filters the supplied Character during a Character translation operation.
	 * @param character the character to filter.
	 * @return true if the character should be translated, false otherwise.
	 */
	public boolean filter(Character character);

}
