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

import au.org.ala.delta.editor.slotfile.CharType;
import au.org.ala.delta.model.CharacterType;

public class CharacterTypeConverter {
	
	/**
	 * Converts a slotfile CharType int into a model class CharacterType enum.
	 * @param charType the slotfile character type.
	 * @return the appropriate matching CharacterType for the supplied char type.
	 */
	public static CharacterType fromCharType(int charType) {
		switch (charType) {
		case CharType.TEXT:
			return CharacterType.Text;
		case CharType.INTEGER:
			return CharacterType.IntegerNumeric;
		case CharType.REAL:
			return CharacterType.RealNumeric;
		case CharType.ORDERED:
			return CharacterType.OrderedMultiState;
		case CharType.UNORDERED:
			return CharacterType.UnorderedMultiState;
		case CharType.UNKNOWN:
			return CharacterType.Unknown;
		default:
			throw new RuntimeException("Unrecognised character type: " + charType);
		}
		
		
	}
	
	
	/**
	 * Converts a model CharacterType into a slotfile int CharType.
	 * @param characterType the model character type.
	 * @return the appropriate matching int for the supplied character type.
	 */
	public static int toCharType(CharacterType characterType) {
		switch (characterType) {
		case Text:
			return CharType.TEXT;
		case IntegerNumeric:
			return CharType.INTEGER;
		case RealNumeric:
			return CharType.REAL;
		case OrderedMultiState:
			return CharType.ORDERED;
		case UnorderedMultiState:
			return CharType.UNORDERED;
		case Unknown:
			return CharType.UNKNOWN;
		default:
			throw new RuntimeException("Unrecognised character type: " + characterType);
		}
	}
}
