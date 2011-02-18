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
package au.org.ala.delta.slotfile;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;


public class VOItemAdaptor extends Item implements VOAdaptor<VOItemDesc> {

	private VOItemDesc _voItemDesc;
	
	
	
	public VOItemAdaptor(VOItemDesc voItem, int i) {
		super(i);
		_voItemDesc = voItem;
	}
	
	@Override
	public VOItemDesc getVirtualObject() {
		return _voItemDesc;
	}

	@Override
	public int getItemId() {
		return super.getItemId();
	}

	@Override
	public void setDescription(String itemName) {		
		super.setDescription(itemName);		
	}

	@Override
	public String getDescription() {
		return _voItemDesc.getAnsiName();
	}

	@Override
	public void setAttribute(Character character, String value) {
		
		Attribute attribute = new Attribute(value, ((VOAdaptor<VOCharBaseDesc>)character).getVirtualObject());
		_voItemDesc.writeAttribute(attribute);
	}
	
}
