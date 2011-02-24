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
package au.org.ala.delta.slotfile.model;

import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.model.impl.ItemData;
import au.org.ala.delta.slotfile.VOAdaptor;
import au.org.ala.delta.slotfile.VOItemDesc;


public class VOItemAdaptor implements ItemData, VOAdaptor<VOItemDesc> {

	private VOItemDesc _voItemDesc;
	
	
	
	public VOItemAdaptor(VOItemDesc voItem, int i) {
		_voItemDesc = voItem;
	}
	

	public VOItemDesc getVirtualObject() {
		return _voItemDesc;
	}


	public int getItemId() {
		return _voItemDesc.getUniId();
	}


	public void setDescription(String itemName) {		
		
	}


	public String getDescription() {
		return _voItemDesc.getAnsiName();	
	}


	/* (non-Javadoc)
	 * @see au.org.ala.delta.model.impl.ItemImpl#getAttributes()
	 */
	@Override
	public List<au.org.ala.delta.model.Attribute> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see au.org.ala.delta.model.impl.ItemImpl#getAttribute(au.org.ala.delta.model.Character)
	 */
	@Override
	public au.org.ala.delta.model.Attribute getAttribute(Character character) {
		
		AttributeData impl = new VOAttributeAdaptor(_voItemDesc, ((VOCharacterAdaptor)character.getImpl()).getCharBaseDesc());
		return new au.org.ala.delta.model.Attribute(impl);
	}

	
	
	
	
	
}
