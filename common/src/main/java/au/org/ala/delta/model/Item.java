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

import au.org.ala.delta.model.impl.ItemData;

/**
 * Represents an Item in the DELTA system.
 * An item usually corresponds to a Taxon, but a 1-1 relationship is not required.
 */
public class Item {

	private ItemData _impl;

	private int _itemNumber;
	
	public Item(ItemData impl, int itemNum) {
		_impl = impl;
		_itemNumber = itemNum;
	}
	
	public Item(int itemId) {
		_itemNumber = itemId;
	}
	
	public int getItemNumber() {
		return _itemNumber;
	}

	public void setDescription(String description) {
		_impl.setDescription(description);
	}
	
	public String getDescription() {
		return _impl.getDescription();
	}
	
	public List<Attribute> getAttributes() {
		return _impl.getAttributes();
	}
	
	public Attribute getAttribute(Character character) {
		return _impl.getAttribute(character);
	}
	
	public void addAttribute(Character character, String value) {
		_impl.addAttribute(character, value);
	}
	
	public boolean hasAttribute(Character character) {
		return getAttribute(character) != null;
	}
	
	public boolean isVariant() {
		return _impl.isVariant();
	}
	
	public void setVariant(boolean variant) {
		_impl.setVariant(variant);
	}
}
