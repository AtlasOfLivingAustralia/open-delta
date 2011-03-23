package au.org.ala.delta.editor.slotfile.model;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
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
	
	/**
	 * Tests that a new Data set can be created and an integer character added successfully.
	 */
	@Test
	public void testCreateNewIntegerCharacter() {
		
		String description = "I am a new integer character";
		String notes = "This is a really great character";
		String units = "mm";
		boolean exclusive = true;
		boolean mandatory = true;
		
		IntegerCharacter intChar = (IntegerCharacter)_dataSet.addCharacter(CharacterType.IntegerNumeric);
		intChar.setDescription(description);
		intChar.setExclusive(exclusive);
		intChar.setMandatory(mandatory);
		intChar.setNotes(notes);
		intChar.setUnits(units);
		
		int number = intChar.getCharacterId();
		intChar = (IntegerCharacter)_dataSet.getCharacter(number);
		assertEquals(description, intChar.getDescription());
		assertEquals(mandatory, intChar.isMandatory());
		assertEquals(exclusive, intChar.isExclusive());
		assertEquals(notes, intChar.getNotes());
		assertEquals(units, intChar.getUnits());
		
	}
	
	
	/**
	 * Tests that a new Data set can be created and an multistate character added successfully.
	 */
	@Test
	public void testCreateNewMultistateCharacter() {
		
		String description = "I am a new multistate character";
		String notes = "This is a really great character";
		String[] stateText = new String[] {"state 1", "state 2", "state 3"};
		boolean exclusive = true;
		boolean mandatory = true;
		
		MultiStateCharacter multiStateChar = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.UnorderedMultiState);
		multiStateChar.setDescription(description);
		multiStateChar.setExclusive(exclusive);
		multiStateChar.setMandatory(mandatory);
		multiStateChar.setNotes(notes);
		multiStateChar.setNumberOfStates(stateText.length);
		for (int i=1; i<=stateText.length; i++) {
			multiStateChar.setState(i, stateText[i-1]);
		}
		int number = multiStateChar.getCharacterId();
		multiStateChar = (MultiStateCharacter)_dataSet.getCharacter(number);
		assertEquals(description, multiStateChar.getDescription());
		assertEquals(mandatory, multiStateChar.isMandatory());
		assertEquals(exclusive, multiStateChar.isExclusive());
		assertEquals(notes, multiStateChar.getNotes());
		assertEquals(stateText.length, multiStateChar.getNumberOfStates());
		for (int i=1; i<=stateText.length; i++) {
			assertEquals(i+". "+stateText[i-1], multiStateChar.getState(i));
		}
	}
	
}
