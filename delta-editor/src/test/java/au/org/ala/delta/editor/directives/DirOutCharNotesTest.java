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
