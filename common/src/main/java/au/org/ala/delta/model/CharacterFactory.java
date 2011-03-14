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

public class CharacterFactory {

	public static Character newCharacter(CharacterType type, int number) {
		switch (type) {
			case IntegerNumeric:
				return new IntegerCharacter(number);
			case OrderedMultiState:
				return new OrderedMultiStateCharacter(number);
			case RealNumeric:
				return new RealCharacter(number);
			case Text:
				return new TextCharacter(number);
			case UnorderedMultiState:
				return new UnorderedMultiStateCharacter(number);
			default:
				throw new RuntimeException("Unhandled character type: " + type);
		}
	}

}
