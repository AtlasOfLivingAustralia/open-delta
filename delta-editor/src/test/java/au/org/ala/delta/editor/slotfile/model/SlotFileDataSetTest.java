package au.org.ala.delta.editor.slotfile.model;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.TextCharacter;

/**
 * Tests the SlotFileDataSet class.
 */
public class SlotFileDataSetTest extends TestCase {

	private SlotFileDataSet _dataSet = (SlotFileDataSet)new SlotFileDataSetFactory().createDataSet("test");
	
	/**
	 * Tests that a new Data set can be created and a text character added successfully.
	 */
	@Test
	public void testCreateNewTextCharacter() {
		
		String description = "I am a new text character";
		String notes = "This is a really great character";
		boolean exclusive = true;
		boolean mandatory = true;
		
		TextCharacter textChar = (TextCharacter)_dataSet.addCharacter(CharacterType.Text);
		textChar.setDescription(description);
		textChar.setExclusive(exclusive);
		textChar.setMandatory(mandatory);
		textChar.setNotes(notes);
		
		
		int number = textChar.getCharacterId();
		textChar = (TextCharacter)_dataSet.getCharacter(number);
		assertEquals(description, textChar.getDescription());
		assertEquals(mandatory, textChar.isMandatory());
		assertEquals(exclusive, textChar.isExclusive());
		assertEquals(notes, textChar.getNotes());
	}
	
	
}
