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

public class Item {

	private ItemData _impl;

	private int _itemId;
	
	public Item(ItemData impl, int itemNum) {
		_impl = impl;
		_itemId = itemNum;
	}
	
	public Item(int itemId) {
		_itemId = itemId;
	}
	
	public int getItemId() {
		return _itemId;
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
}
