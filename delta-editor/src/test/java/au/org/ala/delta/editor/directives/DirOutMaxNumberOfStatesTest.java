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
package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DirOutMaxNumberStates;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Tests the DirOutMaxNumberOfStates class.
 */
public class DirOutMaxNumberOfStatesTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.MAXIMUM_NUMBER_OF_STATES];
	}
	
	/**
	 * Tests the export of the MAXIMUM NUMBER OF STATES directive using our 
	 * sample dataset.
	 */
	public void testDirOutMaximumNumberOfStates() throws Exception {
		
		CharacterType[] characterTypes = new CharacterType[] {
				CharacterType.Text, 
				CharacterType.Text,
				CharacterType.UnorderedMultiState,
				CharacterType.UnorderedMultiState,
				CharacterType.RealNumeric,
				CharacterType.IntegerNumeric,
				CharacterType.OrderedMultiState
		};
		
		int[] numbersOfStates = new int[] {1, 3, 1};
		
		int j = 0;
		for (int i=0; i<characterTypes.length; i++) {
			Character character = _dataSet.addCharacter(characterTypes[i]);
			if (character.getCharacterType().isMultistate()) {
				((MultiStateCharacter)character).setNumberOfStates(numbersOfStates[j++]);
			}
			
		}
		DirOutMaxNumberStates dirOut = new DirOutMaxNumberStates();
		
		dirOut.process(_state);
		
		assertEquals("*MAXIMUM NUMBER OF STATES 3\n", output());
		
	}
}
