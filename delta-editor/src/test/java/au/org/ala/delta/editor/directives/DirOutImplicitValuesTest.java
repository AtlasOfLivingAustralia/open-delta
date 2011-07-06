package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DirOutImplicitValues;
import au.org.ala.delta.editor.slotfile.directive.DirOutNumberStates;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Tests the DirOutImplicitValues class.
 */
public class DirOutImplicitValuesTest extends DirOutTest {

	protected Directive getDirective() {
		return ConforDirType.ConforDirArray[ConforDirType.IMPLICIT_VALUES];
	}
	
	/**
	 * Tests the export of the IMPLICIT VALUES directive using our 
	 * sample dataset.
	 */
	public void testDirOutImplicitValues() throws Exception {
		
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
		
		int[] numbersOfStates = new int[] {4, 3, 1, 1};
		int[] implicit = new int[] {-1, 2, 1, -1};
		
		int j = 0;
		for (int i=0; i<characterTypes.length; i++) {
			Character character = _dataSet.addCharacter(characterTypes[i]);
			if (character.getCharacterType().isMultistate()) {
				((MultiStateCharacter)character).setNumberOfStates(numbersOfStates[j]);
				if (implicit[j] > 0) {
					
					((MultiStateCharacter)character).setUncodedImplicitState(implicit[j]);
					
				}
				j++;
			}
			
		}
		DirOutImplicitValues dirOut = new DirOutImplicitValues();
		
		dirOut.process(_state);
		
		assertEquals("*IMPLICIT VALUES 4,2 7,1\n", output());
		
	}
}
