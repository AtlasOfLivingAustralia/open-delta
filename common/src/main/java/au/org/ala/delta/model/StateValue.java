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

public class StateValue {
	
	private Character _character;
	private Item _item;	
	private String _rawValue;
	private String _comment;
	
	public StateValue(Character character, Item item, String value) {
		_character = character;
		_item = item;
		_rawValue = value;
	}
	
	public Character getCharacter() {
		return _character;
	}
	
	public Item getItem() {
		return _item;
	}
	
	public String getComment() {
		return _comment;
	}
	
	public String getValue() {
		return _rawValue;		
	}
	
	public void setComment(String comment) {
		_comment = comment;
	}
	
	@Override
	public String toString() {
		return String.format("%s", _rawValue);
	}

}
