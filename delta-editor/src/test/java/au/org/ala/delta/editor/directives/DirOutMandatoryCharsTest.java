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
import au.org.ala.delta.editor.slotfile.directive.DirOutMandatoryChars;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;

/**
 * Tests the DirOutMandatoryChars class.
 */
public class DirOutMandatoryCharsTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.MANDATORY_CHARACTERS];
	}
	
	/**
	 * Tests the export of the MANDATORY CHARACTERS directive using our 
	 * sample dataset.
	 */
	public void testDirOutMandatoryChars() throws Exception {
		
		CharacterType[] characterTypes = new CharacterType[] {
				CharacterType.Text, 
				CharacterType.Text,
				CharacterType.UnorderedMultiState,
				CharacterType.UnorderedMultiState,
				CharacterType.RealNumeric,
				CharacterType.IntegerNumeric,
				CharacterType.OrderedMultiState,
				CharacterType.UnorderedMultiState
		};
		
		boolean[] mandatory = new boolean[] {true, false, true, true, true, false, false, true};
		
		for (int i=0; i<characterTypes.length; i++) {
			Character character = _dataSet.addCharacter(characterTypes[i]);
			character.setMandatory(mandatory[i]);
			
		}
		DirOutMandatoryChars dirOut = new DirOutMandatoryChars();
		
		dirOut.process(_state);
		
		assertEquals("*MANDATORY CHARACTERS 1 3-5 8\n", output());
		
	}
}
