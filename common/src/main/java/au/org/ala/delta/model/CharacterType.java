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

import java.util.HashMap;

public enum CharacterType {	
	Text,
	IntegerNumeric,
	RealNumeric,
	OrderedMultiState,
	UnorderedMultiState;
	
	public boolean isMultistate() {
		return (this.equals(OrderedMultiState) || (this.equals(UnorderedMultiState)));
	}
	
	public boolean isNumeric() {
		return (this.equals(IntegerNumeric) || (this.equals(RealNumeric)));
	}
	
	public static CharacterType parse(String str) {
		String s = str.substring(0,2).toUpperCase();
		if (TYPE_MAP.containsKey(s)) {
			return TYPE_MAP.get(s);
		}
		throw new RuntimeException("Unrecognized character type literal: " + str);
	}
	
	private static HashMap<String, CharacterType> TYPE_MAP = new HashMap<String, CharacterType>();
	
	static {
		TYPE_MAP.put("TE", Text);
		TYPE_MAP.put("IN", IntegerNumeric);
		TYPE_MAP.put("RN", RealNumeric);
		TYPE_MAP.put("OM", OrderedMultiState);
		TYPE_MAP.put("UM", UnorderedMultiState);
	}
}
