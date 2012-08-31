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
package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		boolean mandatory = true;
		
		TextCharacter textChar = (TextCharacter)_dataSet.addCharacter(CharacterType.Text);
		textChar.setDescription(description);
		textChar.setMandatory(mandatory);
		textChar.setNotes(notes);

        String fileName = tempFileName();
		_repo.saveAsName(_dataSet, fileName, true, null);
		_dataSet.close();
	
		_dataSet = (SlotFileDataSet)_repo.findByName(fileName, null);
		
		int number = textChar.getCharacterId();
		textChar = (TextCharacter)_dataSet.getCharacter(number);
		assertEquals(description, textChar.getDescription());
		assertEquals(mandatory, textChar.isMandatory());
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
		boolean mandatory = true;
		
		IntegerCharacter intChar = (IntegerCharacter)_dataSet.addCharacter(CharacterType.IntegerNumeric);
		intChar.setDescription(description);
		intChar.setMandatory(mandatory);
		intChar.setNotes(notes);
		intChar.setUnits(units);
		
		int number = intChar.getCharacterId();
		intChar = (IntegerCharacter)_dataSet.getCharacter(number);
		assertEquals(description, intChar.getDescription());
		assertEquals(mandatory, intChar.isMandatory());
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
		
		String attributeText = "<I am a new item attribute>";
		
		TextAttribute textAttr = (TextAttribute) _dataSet.addAttribute(item.getItemNumber(), textChar.getCharacterId());
		textAttr.setText(attributeText);
		
		String fileName = tempFileName();
        _repo.saveAsName(_dataSet, fileName, true, null);
		_dataSet.close();
	
		_dataSet = (SlotFileDataSet)_repo.findByName(fileName, null);
		
		item = _dataSet.getItem(item.getItemNumber());
		assertEquals(description, item.getDescription());
		assertEquals(attributeText, item.getAttribute(textChar).getValueAsString());
		assertTrue(item.isVariant());
		
		item = _dataSet.addItem();
		attributeText = "<\\i{}Ornithospermum\\i0{} Dumoulin, \\i{}Tema\\i0{} Adans.>";
		textAttr = (TextAttribute) _dataSet.addAttribute(item.getItemNumber(), textChar.getCharacterId());
        textAttr.setText(attributeText);
		assertEquals(attributeText, item.getAttribute(textChar).getValueAsString());
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
		MutableDeltaDataSet dataSet = _repo.findByName(f.getAbsolutePath(), null);
		
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
		_repo.saveAsName(dataSet, temp.getAbsolutePath(), true, null);
		dataSet.close();
	
		dataSet = (SlotFileDataSet)_repo.findByName(temp.getAbsolutePath(), null);
		checkDescriptions(dataSet, itemNames);
		
	}

	private void checkDescriptions(MutableDeltaDataSet dataSet, List<String> itemNames) {
		for (int i=1; i<dataSet.getMaximumNumberOfItems(); i++) {
			Item item = dataSet.getItem(i);
			assertEquals(i, item.getItemNumber());
			assertEquals(itemNames.get(i-1), item.getDescription());
		}
	}
	
	@Test
	public void testMoveItemForwards() throws Exception {
		addItems();
		testMoveItemForwards(2, 8);
	}
	
	@Test
	public void testMoveFirstToLast() throws Exception {
		addItems();
		testMoveItemForwards(1, 10);
	}
	
	private void testMoveItemForwards(int from, int to) {
		int itemNumberToMove = from;
		Item toMove = _dataSet.getItem(itemNumberToMove);
		
		_dataSet.moveItem(toMove, to);
		
		for (int i=1; i<itemNumberToMove; i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
		for (int i=itemNumberToMove; i<to; i++) {
			Item item = _dataSet.getItem(i);
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+(i+1), item.getDescription());
		}
		assertEquals("Item "+from, _dataSet.getItem(to).getDescription());
		assertEquals(to, _dataSet.getItem(to).getItemNumber());
		
		for (int i=to+1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
	}
	
	@Test
	public void testMoveItemBackwards() throws Exception {
		addItems();
		testMoveItemBackwards(7, 2);
	}
	
	@Test
	public void testMoveLastToFirst() throws Exception {
		addItems();
		testMoveItemBackwards(10, 1);
	}
	

	private void testMoveItemBackwards(int from, int to) {
		int itemNumberToMove = from;
		Item toMove = _dataSet.getItem(itemNumberToMove);
		
		_dataSet.moveItem(toMove, to);
		
		for (int i=1; i<to; i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
		
		assertEquals("Item "+from, _dataSet.getItem(to).getDescription());
		assertEquals(to, _dataSet.getItem(to).getItemNumber());
		
		
		for (int i=to+1; i<=from; i++) {
			Item item = _dataSet.getItem(i);
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+(i-1), item.getDescription());
		}
		
		for (int i=from+1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
	}
	
	@Test
	public void testDeleteState() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
		MutableDeltaDataSet dataSet = _repo.findByName(f.getAbsolutePath(), null);
		
		MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(79);
		dataSet.deleteState(character, 3);
		
		assertEquals(5, character.getNumberOfStates());
		
	}
	
	/**
	 * Tests that a state can be deleted when it is part of a controlling character.
	 */
	@Test
	public void testDeleteStateThatControls() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
		MutableDeltaDataSet dataSet = _repo.findByName(f.getAbsolutePath(), null);
		
		MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(10);
		String description = character.getDescription();
		dataSet.deleteState(character, 2);
		
		assertEquals(1, character.getNumberOfStates());
		assertEquals(description, character.getDescription());
		
		deepRead(dataSet);
		
		
	}
	
	@Test
	public void testGetUncodedItems() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
		MutableDeltaDataSet dataSet = _repo.findByName(f.getAbsolutePath(), null);
		
		au.org.ala.delta.model.Character character = dataSet.getCharacter(2);
		List<Item> uncodedItems = dataSet.getUncodedItems(character);
		
		assertEquals(0, uncodedItems.size());
		
		character = dataSet.getCharacter(5);
		uncodedItems = dataSet.getUncodedItems(character);
		
		assertEquals(6, uncodedItems.size());
		
		assertEquals(2, uncodedItems.get(0).getItemNumber());
		assertEquals(4, uncodedItems.get(1).getItemNumber());
		assertEquals(5, uncodedItems.get(2).getItemNumber());
		assertEquals(8, uncodedItems.get(3).getItemNumber());
		assertEquals(10, uncodedItems.get(4).getItemNumber());
		assertEquals(14, uncodedItems.get(5).getItemNumber());
	}
	
	@Test
	public void testGetItemsWithMultipleStatesCoded() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
		MutableDeltaDataSet dataSet = _repo.findByName(f.getAbsolutePath(), null);
		
		MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(10);
		List<Item> items = dataSet.getItemsWithMultipleStatesCoded(character);
		
		assertEquals(1, items.size());
		assertEquals(1, items.get(0).getItemNumber());
		
		character = (MultiStateCharacter)dataSet.getCharacter(8);
		items = dataSet.getItemsWithMultipleStatesCoded(character);
		
		assertEquals(4, items.size());
		assertEquals(9, items.get(0).getItemNumber());
		assertEquals(10, items.get(1).getItemNumber());
		assertEquals(12, items.get(2).getItemNumber());
		assertEquals(13, items.get(3).getItemNumber());
		
		
		MultiStateAttribute attr = (MultiStateAttribute) dataSet.addAttribute(dataSet.getItem(11).getItemNumber(), character.getCharacterId());
		attr.setValueFromString("V");
		items = dataSet.getItemsWithMultipleStatesCoded(character);
		
		assertEquals(5, items.size());
		assertEquals(11, items.get(2).getItemNumber());
		
		character = (MultiStateCharacter)dataSet.getCharacter(16);
		items = dataSet.getItemsWithMultipleStatesCoded(character);
		
		assertEquals(0, items.size());
		
		
		
	}

    private String tempFileName() throws IOException {
        File temp = File.createTempFile("test", ".dlt");
        String name = temp.getAbsolutePath();
        temp.delete();

        return name;
    }
	
}
