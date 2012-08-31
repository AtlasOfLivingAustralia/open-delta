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
package au.org.ala.delta.translation.nexus;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.AbstractDataSetFilter;

public class NexusDataSetFilter extends AbstractDataSetFilter {

	
	public NexusDataSetFilter(DeltaContext context) {
		_context = context;
	}
	
	@Override
	public boolean filter(Item item) {
		return !_context.isItemExcluded(item.getItemNumber());
	}

	
	
	@Override
	public boolean filter(Item item, Character character) {
		
		return filter(character);
	}

	/**
	 * Text Characters and Numeric Characters without a Key State defined
	 * are excluded from Nexus transformations.
	 */
	@Override
	public boolean filter(Character character) {
		if (!_context.isCharacterExcluded(character.getCharacterId())) {
			CharacterType type = character.getCharacterType();
			if (type.isMultistate()) {
				return true;
			}
			else if (type.isNumeric()) {
				IdentificationKeyCharacter idChar = _context.getIdentificationKeyCharacter(character.getCharacterId());
				if (idChar != null && idChar.getNumberOfStates() > 0) {
					return true;
				}
			}
		}
		return false;
	}

}
