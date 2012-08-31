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
import au.org.ala.delta.editor.slotfile.directive.DirOutCharTypes;
import au.org.ala.delta.model.CharacterType;

/**
 * Tests the DirOutCharTypes class.
 */
public class DirOutCharTypesTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.CHARACTER_TYPES];
	}
	
	/**
	 * Tests the export of the character types directive using our 
	 * sample dataset.
	 */
	public void testDirOutCharTypes() throws Exception {
		
		CharacterType[] characterTypes = new CharacterType[] {
				CharacterType.Text, 
				CharacterType.Text,
				CharacterType.UnorderedMultiState,
				CharacterType.UnorderedMultiState,
				CharacterType.RealNumeric,
				CharacterType.IntegerNumeric,
				CharacterType.OrderedMultiState
		};
		
		for (CharacterType type : characterTypes) {
			_dataSet.addCharacter(type);
		}
		DirOutCharTypes charTypesOut = new DirOutCharTypes();
		
		charTypesOut.process(_state);
		
		assertEquals("*CHARACTER TYPES 1-2,TE 5,RN 6,IN 7,OM\n", output());
		
	}
}
