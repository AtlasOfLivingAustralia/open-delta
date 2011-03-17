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
package au.org.ala.delta.editor.slotfile.model;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.model.impl.ItemData;


public class VOItemAdaptor implements ItemData {

	private VOItemDesc _voItemDesc;
	
	
	
	public VOItemAdaptor(VOItemDesc voItem, int i) {
		_voItemDesc = voItem;
	}
	
	public void setDescription(String itemName) {		
		throw new NotImplementedException();
	}


	public String getDescription() {
		return _voItemDesc.getAnsiName();	
	}


	@Override
	public List<au.org.ala.delta.model.Attribute> getAttributes() {
		
		throw new NotImplementedException();
	}

	@Override
	public au.org.ala.delta.model.Attribute getAttribute(Character character) {
		if (character == null) {
			return null;
		}
		AttributeData impl = new VOAttributeAdaptor(_voItemDesc, ((VOCharacterAdaptor)character.getImpl()).getCharBaseDesc());
		return new au.org.ala.delta.model.Attribute(character, impl);
	}

	/**
	 * This method does nothing - the VOItemAdaptor reads from the slot file and creates Attributes
	 * when requested by getAttribute. 
	 */
	@Override
	public void addAttribute(Character character, String value) {
		// do nothing - attributes are created on the fly when getAttribute is called.
	}
}
