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
