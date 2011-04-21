package au.org.ala.delta.model.impl;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.Item;

/**
 * Tests the DefaultDataSet class.
 */
public class DefaultDataSetTest extends TestCase {

	
	private DefaultDataSet _dataSet;
	
	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = (DefaultDataSet)factory.createDataSet("Test");
		
		for (int i=0; i<10; i++) {
			Item item = _dataSet.addItem();
			
			item.setDescription("Item "+(i+1));
		}
	}
	
	/**
	 * Tests the deleteItem method.
	 */
	@Test
	public void testDeleteItemInTheMiddle() {
		
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
	public void testDeleteFirstItem() {
		int itemNumberToDelete = 1;
		Item toDelete = _dataSet.getItem(itemNumberToDelete);
		
		_dataSet.deleteItem(toDelete);
		
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+(i+1), item.getDescription());
		}
	}
	
	
	public void testDeleteLastItem() {
		int itemNumberToDelete = _dataSet.getMaximumNumberOfItems();
		Item toDelete = _dataSet.getItem(itemNumberToDelete);
		
		_dataSet.deleteItem(toDelete);
		
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			
			assertEquals(i, item.getItemNumber());
			assertEquals("Item "+i, item.getDescription());
		}
	}
}
