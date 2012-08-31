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
package au.org.ala.delta.model.observer;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;

/**
 * Contains information about a change to a DeltaDataSet.
 */
public class DeltaDataSetChangeEvent {

	public Character _character;
	
	public Item _item;
	
	public Image _image;
	
	public MutableDeltaDataSet _dataSet;
	
	/** 
	 * Allows additional information to be supplied about the change - for example when an Item
	 * is moved knowing the old and new item numbers is helpful.
	 */
	public Object _extra;
	
	public DeltaDataSetChangeEvent(MutableDeltaDataSet source) {
		this(source, null, null);
	}
	
	public DeltaDataSetChangeEvent(MutableDeltaDataSet source, Character character, Item item) {
		this(source, character, item, -1);
	}
	
	public DeltaDataSetChangeEvent(MutableDeltaDataSet source, Character character, Item item, Object extra) {
		_dataSet = source;
		_character = character;
		_item = item;
		_extra = extra;
	}
	
	public DeltaDataSetChangeEvent(MutableDeltaDataSet source, Image image) {
		_dataSet = source;
		_image = image;
	}
	
	public Character getCharacter() {
		return _character;
	}
	
	public Item getItem() {
		return _item;
	}
	
	public Image getImage() {
		return _image;
	}
	
	public Object getExtraInformation() {
		return _extra;
	}
}
