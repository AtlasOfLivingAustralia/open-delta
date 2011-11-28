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
 * An empty implementation of the ItemListTypeSetter interface.
 */
public class ItemListTypeSetterAdapter implements ItemListTypeSetter {

	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {}

	@Override
	public void afterItem(Item item) {}

	@Override
	public void beforeAttribute(Attribute attribute) {}

	@Override
	public void afterAttribute(Attribute attribute) {}

	@Override
	public void afterLastItem() {}

	@Override
	public void beforeItemHeading() {}

	@Override
	public void afterItemHeading() {}

	@Override
	public void beforeItemName() {}

	@Override
	public void afterItemName() {}

	@Override
	public void newParagraph() {}

	@Override
	public String typeSetItemDescription(String description) {
		return description;
	}

	@Override
	public void beforeNewParagraphCharacter() {}

	@Override
	public String rangeSeparator() {
		return "";
	}

	@Override
	public void beforeCharacterDescription(Character character, Item item) {}

	@Override
	public void afterCharacterDescription(Character character, Item item) {}

	@Override
	public void beforeEmphasizedCharacter() {}

	@Override
	public void afterEmphasizedCharacter() {}

}
