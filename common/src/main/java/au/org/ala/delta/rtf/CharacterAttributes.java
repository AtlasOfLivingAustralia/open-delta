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
package au.org.ala.delta.rtf;

import java.util.HashMap;
import java.util.Map;

public class CharacterAttributes {
		
	private Map<CharacterAttributeType, Integer> _attrMap = new HashMap<CharacterAttributeType, Integer>();
	
	public CharacterAttributes() {
		for (CharacterAttributeType attrType : CharacterAttributeType.values()) {
			_attrMap.put(attrType, 0);
		}
	}
	
	public CharacterAttributes(CharacterAttributes other) {
		for (CharacterAttributeType attrType : CharacterAttributeType.values()) {
			_attrMap.put(attrType, other.get(attrType));
			
		}
	}
	
	public int get(CharacterAttributeType attrType) {
		return _attrMap.get(attrType);		
	}
	
	public void set(CharacterAttributeType attrType, int value) {
		_attrMap.put(attrType, value);
	}

}
