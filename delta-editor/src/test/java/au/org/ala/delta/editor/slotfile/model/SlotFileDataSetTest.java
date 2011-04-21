package au.org.ala.delta.editor.slotfile.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.TextCharacter;

/**
 * Tests the SlotFileDataSet class.
 */
public class SlotFileDataSetTest  extends DeltaTestCase {

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
			assertEquals(stateText[i-1], multiStateChar.getState(i));
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
	
	@Test
	private void addItems() throws Exception {
		for (int i=0; i<10; i++) {
			Item item = _dataSet.addItem();
			
			item.setDescription("Item "+(i+1));
		}
	}
	
	/**
	 * Tests the deleteItem method.
	 */
	@Test
	public void testDeleteItemInTheMiddle() throws Exception {
		
		addItems();
		
		int itemNumberToDelete = 5;
		Item toDelete = _dataSet.getItem(itemNumberToDelete);
		
		_dataSet.deleteItem(toDelete);
		
		for (int i=1; i<itemNumberToDelete; i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
		
		for (int i=itemNumberToDelete; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+(i+1), item.getDescription());
		}
	}
	
	
	@Test
	public void testDeleteFirstItem() throws Exception {
		
		addItems();
		
		int itemNumberToDelete = 1;
		Item toDelete = _dataSet.getItem(itemNumberToDelete);
		
		_dataSet.deleteItem(toDelete);
		
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+(i+1), item.getDescription());
		}
	}
	
	@Test
	public void testDeleteLastItem() throws Exception {
		
		addItems();
		
		int itemNumberToDelete = _dataSet.getMaximumNumberOfItems();
		Item toDelete = _dataSet.getItem(itemNumberToDelete);
		
		_dataSet.deleteItem(toDelete);
		
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
	}
	
	@Test
	public void testDeleteItemsWithSample() throws Exception {
		
		// Item 13 has no images.
		deleteItemWithSample(13);
		
		// Item 5 has an image.
		deleteItemWithSample(5);
	}
	
	private void deleteItemWithSample(int itemNumberToDelete) throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
		DeltaDataSet dataSet = _repo.findByName(f.getAbsolutePath(), null);
		
		List<String> itemNames = new ArrayList<String>();
		for (int i=1; i<=dataSet.getMaximumNumberOfItems(); i++) {
			itemNames.add(dataSet.getItem(i).getDescription());
		}
	
		itemNames.remove(itemNumberToDelete-1);
		
		Item toDelete = dataSet.getItem(itemNumberToDelete);
		
		dataSet.deleteItem(toDelete);
		
		checkDescriptions(dataSet, itemNames);
		
		// Save and load to ensure it saves correctly.
		File temp = newTempFile();
		_repo.saveAsName(dataSet, temp.getAbsolutePath(), null);
		dataSet.close();
	
		dataSet = (SlotFileDataSet)_repo.findByName(temp.getAbsolutePath(), null);
		checkDescriptions(dataSet, itemNames);
		
	}

	private void checkDescriptions(DeltaDataSet dataSet, List<String> itemNames) {
		for (int i=1; i<dataSet.getMaximumNumberOfItems(); i++) {
			Item item = dataSet.getItem(i);
			assertEquals(i, item.getItemNumber());
			assertEquals(itemNames.get(i-1), item.getDescription());
		}
	}
	
	
}
