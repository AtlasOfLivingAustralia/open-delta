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
