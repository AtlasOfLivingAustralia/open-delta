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

import au.org.ala.delta.model.impl.AttributeData;

/**
 * An attribute is the value of a single character associated with an item.
 */
public class Attribute {

	private AttributeData _impl;
	
	private Character _character;
	
	public Attribute(Character character, AttributeData impl) {
		_character = character;
		_impl = impl;
	}
	
	public Character getCharacter() {
		return _character;
	}
	
	public boolean isPresent(int stateNumber) {
		
		if (!(_character instanceof MultiStateCharacter)) {
			return false;
		}
		return _impl.isStatePresent(stateNumber);
	}
	
	public boolean isSimple() {
		return _impl.isSimple();
	}
	
	public String getValue() {
		return _impl.getValue();
	}
	
	public void setValue(String value) {
		_impl.setValue(value);
	}
	
}
