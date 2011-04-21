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

import au.org.ala.delta.editor.slotfile.AttrChunk;
import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.model.impl.ItemData;
import au.org.ala.delta.util.Utils;

/**
 * Adapts a slot file VOItemDesc to the model Item interface.
 */
public class VOItemAdaptor implements ItemData {

	private VOItemDesc _voItemDesc;
	private DeltaVOP _vop;
	
	
	public VOItemAdaptor(DeltaVOP vop, VOItemDesc voItem, int i) {
		_vop = vop;
		_voItemDesc = voItem;
	}
	
	public void setDescription(String itemName) {		
		
		String oldDescription = getDescription();
		
		Attribute nameAttribute = new Attribute(VOItemDesc.VOUID_NAME);
		nameAttribute.insert(0, new AttrChunk(itemName));
		_voItemDesc.writeAttribute(nameAttribute);
		
		if (!Utils.RTFToANSI(itemName).equals(oldDescription)) {
			_vop.deleteFromNameList(_voItemDesc);
			_vop.insertInNameList(_voItemDesc);
		}
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
		
		AttributeData impl = new VOAttributeAdaptor(_voItemDesc, getVOCharBaseDesc(character));
		return new au.org.ala.delta.model.Attribute(character, impl);
	}

	/**
	 * This method does nothing - the VOItemAdaptor reads from the slot file and creates Attributes
	 * when requested by getAttribute. 
	 */
	@Override
	public void addAttribute(Character character, String value) {
		
		Attribute attribute = new Attribute(value, getVOCharBaseDesc(character));
		_voItemDesc.writeAttribute(attribute);
	}
	
	private VOCharBaseDesc getVOCharBaseDesc(Character character) {
		return ((VOCharacterAdaptor)character.getImpl()).getCharBaseDesc();
	}

	@Override
	public boolean isVariant() {
		return _voItemDesc.isVariant();
	}

	@Override
	public void setVariant(boolean variant) {
		_voItemDesc.setVariant(variant);
	}
	
	public VOItemDesc getItemDesc() {
		return _voItemDesc;
	}

    @Override
    public String getImageData() {
        throw new NotImplementedException();
    }

    @Override
    public void setImageData(String imageData) {
        throw new NotImplementedException();
    }

    @Override
    public String getLinkFileDataWithSubjects() {
        throw new NotImplementedException();
    }

    @Override
    public void setLinkFileDataWithSubjects(String linkFileData) {
        throw new NotImplementedException();
    }

    @Override
    public String getLinkFileDataNoSubjects() {
        throw new NotImplementedException();
    }

    @Override
    public void setLinkFileDataNoSubjects(String linkFileData) {
        throw new NotImplementedException();
    }
}
