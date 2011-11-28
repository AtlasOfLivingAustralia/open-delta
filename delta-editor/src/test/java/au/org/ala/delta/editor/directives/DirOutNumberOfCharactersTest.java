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
import au.org.ala.delta.editor.slotfile.directive.DirOutNumberChars;
import au.org.ala.delta.model.CharacterType;

/**
 * Tests the DirOutNumberOfCharacters class.
 */
public class DirOutNumberOfCharactersTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.NUMBER_OF_CHARACTERS];
	}
	
	/**
	 * Tests the export of the NUMBER OF CHARACTERS directive using our 
	 * sample dataset.
	 */
	public void testDirOutNumberOfCharacters() throws Exception {
		
		int maxNumChars = 7;
		
		for (int i=0; i<maxNumChars; i++) {
			_dataSet.addCharacter(CharacterType.Text);
		}
		DirOutNumberChars dirOut = new DirOutNumberChars();
		
		dirOut.process(_state);
		
		assertEquals("*NUMBER OF CHARACTERS 7\n", output());
		
	}
}
