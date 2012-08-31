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
	
	@Test
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
	
	@Test
	public void testMoveItemForwards() {
		testMoveItemForwards(2, 8);
	}
	
	@Test
	public void testMoveFirstToLast() {
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
	public void testMoveItemBackwards() {
		testMoveItemBackwards(7, 2);
	}
	
	@Test
	public void testMoveLastToFirst() {
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
}
