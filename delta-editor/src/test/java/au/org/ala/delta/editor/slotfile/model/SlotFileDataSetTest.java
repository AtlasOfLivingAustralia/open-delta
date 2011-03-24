package au.org.ala.delta.editor.slotfile.model;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.TextCharacter;

/**
 * Tests the SlotFileDataSet class.
 */
public class SlotFileDataSetTest extends TestCase {

	private SlotFileRepository _repo = new SlotFileRepository();
	
	private SlotFileDataSet _dataSet = (SlotFileDataSet)_repo.newDataSet();
	
	/**
	 * Tests that a new Data set can be created and a text character added successfully.
	 */
	@Test
	public void testCreateNewTextCharacter() throws Exception {
		
		String description = "I am a new text character";
		String notes = "This is a really great character";
		boolean exclusive = true;
		boolean mandatory = true;
		
		TextCharacter textChar = (TextCharacter)_dataSet.addCharacter(CharacterType.Text);
		textChar.setDescription(description);
		textChar.setExclusive(exclusive);
		textChar.setMandatory(mandatory);
		textChar.setNotes(notes);
		
		File temp = File.createTempFile("test", ".dlt");
		_repo.saveAsName(_dataSet, temp.getAbsolutePath(), null);
		_dataSet.close();
	
		_dataSet = (SlotFileDataSet)_repo.findByName(temp.getAbsolutePath(), null);
		
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
	
	/**
	 * Tests that a new Data set can be created and a new item added successfully.
	 */
	@Test
	public void testCreateNewItem() {
	
		String description = "I am a new Item";
		Item item = _dataSet.addItem();
		item.setDescription(description);
		
		item = _dataSet.getItem(item.getItemNumber());
		
		assertEquals(description, item.getDescription());
	}
	
	/**
	 * Tests that a new Data set can be created and a new item added successfully.
	 */
	@Test
	public void testCreateNewItemWithTextAttribute() throws Exception {
	
		TextCharacter textChar = (TextCharacter)_dataSet.addCharacter(CharacterType.Text);
		textChar.setDescription("Text char");
		
		String description = "I am a new Item";
		Item item = _dataSet.addItem();
		item.setDescription(description);
		item.setVariant(true);
		
		String attributeText = "I am a new item attribute";
		item.addAttribute(textChar,"<"+attributeText+">");
		
		File temp = File.createTempFile("test", ".dlt");
		_repo.saveAsName(_dataSet, temp.getAbsolutePath(), null);
		_dataSet.close();
	
		_dataSet = (SlotFileDataSet)_repo.findByName(temp.getAbsolutePath(), null);
		
		item = _dataSet.getItem(item.getItemNumber());
		assertEquals(description, item.getDescription());
		assertEquals(attributeText, item.getAttribute(textChar).getValue());
		assertTrue(item.isVariant());
		
		item = _dataSet.addItem();
		attributeText = "\\i{}Ornithospermum\\i0{} Dumoulin, \\i{}Tema\\i0{} Adans.";
		item.addAttribute(textChar,"<"+attributeText+">");
		assertEquals(attributeText, item.getAttribute(textChar).getValue());
		assertFalse(item.isVariant());
	}
	
	
	
	
}
