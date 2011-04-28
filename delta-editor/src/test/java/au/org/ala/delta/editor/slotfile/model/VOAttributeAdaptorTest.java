package au.org.ala.delta.editor.slotfile.model;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Tests the VOAttributeAdaptor class.
 */
public class VOAttributeAdaptorTest extends TestCase {

	private SlotFileRepository _repo = new SlotFileRepository();
	private SlotFileDataSet _dataSet = (SlotFileDataSet)_repo.newDataSet();
	private VOAttributeAdaptor _attributeAdaptor;
	private MultiStateCharacter _character;
	
	@Before
	public void setUp() {
		Item item = _dataSet.addItem();
		_character = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.OrderedMultiState);
		_character.setNumberOfStates(3);
		
		VOItemAdaptor voItemAdaptor = (VOItemAdaptor)item.getItemData();
		VOCharacterAdaptor voCharAdaptor = (VOCharacterAdaptor)_character.getImpl(); 
		_attributeAdaptor = new VOAttributeAdaptor(voItemAdaptor.getItemDesc(), voCharAdaptor.getCharBaseDesc());
		
	}
	
	@After
	public void tearDown() {
		_dataSet.close();
	}
	
	@Test
	public void testIsStatePresent() {
		
		_attributeAdaptor.setValue("1/2");
		assertTrue(_attributeAdaptor.isStatePresent(1));
		assertTrue(_attributeAdaptor.isStatePresent(2));
		assertFalse(_attributeAdaptor.isStatePresent(3));
		
		_attributeAdaptor.setValue("3");
		assertFalse(_attributeAdaptor.isStatePresent(1));
		assertFalse(_attributeAdaptor.isStatePresent(2));
		assertTrue(_attributeAdaptor.isStatePresent(3));
	}
	
	@Test
	public void testSetStatePresent() {
		_attributeAdaptor.setValue("2");
		
		_attributeAdaptor.setStatePresent(3, true);
		assertEquals("2/3", _attributeAdaptor.getValue());
		
		_attributeAdaptor.setValue("1/2");
		_attributeAdaptor.setStatePresent(3, true);
		assertEquals("1/2/3", _attributeAdaptor.getValue());
		
		_attributeAdaptor.setValue("2");
		_attributeAdaptor.setStatePresent(1, true);
		assertEquals("1/2", _attributeAdaptor.getValue());
		
		_attributeAdaptor.setValue("1/3");
		_attributeAdaptor.setStatePresent(2, true);
		assertEquals("1/2/3", _attributeAdaptor.getValue());
		
	}
	
	@Test
	public void testSetStatePresentEmptyAttribute() {
		
		_attributeAdaptor.setStatePresent(1, true);
		assertEquals("1", _attributeAdaptor.getValue());
	}
	
	@Test
	public void testSetStatePresentExclusiveCharacter() {
		
		_character.setExclusive(true);
		_attributeAdaptor.setValue("2");
		
		_attributeAdaptor.setStatePresent(3, true);
		assertEquals("3", _attributeAdaptor.getValue());
		
		_attributeAdaptor.setValue("3");
		_attributeAdaptor.setStatePresent(1, true);
		assertEquals("1", _attributeAdaptor.getValue());
	}
	

}
