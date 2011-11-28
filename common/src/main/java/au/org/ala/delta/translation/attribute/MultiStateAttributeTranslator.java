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
package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;

/**
 * The MultiStateAttributeTranslator is responsible for translating MultiStateCharacter attributes into 
 * natural language.
 */
public class MultiStateAttributeTranslator extends AttributeTranslator {

	/** The character associated with the attribute to translate */
	private MultiStateCharacter _character;
	
	/** Knows how to format character states */
	private CharacterFormatter _formatter;
	
	public MultiStateAttributeTranslator(
			MultiStateCharacter character, 
			CharacterFormatter characterFormatter, 
			AttributeFormatter formatter,
			boolean omitOr) {
		super(formatter, omitOr);
		_character = character;
		_formatter = characterFormatter;
	}

	@Override
	public String translateValue(String value) {
		
		String state = "";
		try{ 
		int stateNum = Integer.parseInt(value);
		
		state = _formatter.formatState(_character, stateNum);
		}
		catch (NumberFormatException e) {
			System.err.println("Error translating character: "+_character.getCharacterId());
			e.printStackTrace();
			throw e;
		}
		return state;
	}

	@Override
	public String rangeSeparator() {
		return " to ";
	}
	

}
