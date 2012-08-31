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

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Typesets the translated output of a DELTA data set.  Handles formatting marks, paragraphs etc.
 */
public interface ItemListTypeSetter {

	public abstract void beforeFirstItem();

	public abstract void beforeItem(Item item);

	public abstract void afterItem(Item item);

	public abstract void beforeAttribute(Attribute attribute);

	public abstract void afterAttribute(Attribute attribute);

	public abstract void afterLastItem();

	public abstract void beforeItemHeading();

	public abstract void afterItemHeading();

	public abstract void beforeItemName();

	public abstract void afterItemName();

	public abstract void newParagraph();

	public String typeSetItemDescription(String description);

	public abstract void beforeNewParagraphCharacter();
	
	public String rangeSeparator();

	public abstract void beforeCharacterDescription(Character character, Item item);

	public abstract void afterCharacterDescription(Character character, Item item);
	
	public abstract void beforeEmphasizedCharacter();
	
	public abstract void afterEmphasizedCharacter();
	
}
