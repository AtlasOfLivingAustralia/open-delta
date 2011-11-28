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
import au.org.ala.delta.editor.slotfile.directive.DirOutCharNotes;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;

/**
 * Tests the DirOutCharNotes class.
 */
public class DirOutCharNotesTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.CHARACTER_NOTES];
	}
	
	/**
	 * Tests the export of the CHARACTER NOTES directive using our 
	 * sample dataset.
	 */
	public void testDirOutCharNotes() throws Exception {
		
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
		
		String[] notes = new String[] {
				"Notes 1", "", "Notes 3",
				"", "", "", 
				"Notes 7", "Notes 8"};
		
		for (int i=0; i<characterTypes.length; i++) {
			Character character = _dataSet.addCharacter(characterTypes[i]);
			character.setNotes(notes[i]);
			
		}
		DirOutCharNotes dirOut = new DirOutCharNotes();
		
		dirOut.process(_state);
		
		assertEquals("*CHARACTER NOTES\n#1. Notes 1\n#3. Notes 3\n"+
				"#7. Notes 7\n#8. Notes 8\n", output());
		
	}
}
