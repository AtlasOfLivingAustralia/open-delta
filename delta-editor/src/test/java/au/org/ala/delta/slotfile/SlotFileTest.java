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
package au.org.ala.delta.slotfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.slotfile.SlotFile.SlotHeader;

/**
 * Tests the SlotFile class.
 */
public class SlotFileTest {

	/** Holds the instance of the class we are testing */
	private SlotFile slotFile;
	
	
	@Before public void setUp() throws Exception {
		slotFile = copyAndOpen("/SAMPLE.DLT");
	}
	
	@After public void tearDown() throws Exception {
		if (slotFile != null) {
			slotFile.close();
		}
	}
	
	/**
	 * Makes a copy of the supplied DELTA file and returns a new SlotFile created from the copied file.
	 * @param fileName the ClassLoader relative name of the DELTA file.
	 * @return a new SlotFile.
	 * @throws IOException if the file cannot be found.
	 */
	private SlotFile copyAndOpen(String fileName) throws IOException {
		
		URL deltaFileUrl = getClass().getResource(fileName);		
		File tempDeltaFile = File.createTempFile("test", ".dlt");
		
		FileUtils.copyURLToFile(deltaFileUrl, tempDeltaFile);
		SlotFile slotFile = new SlotFile(tempDeltaFile.getAbsolutePath(), BinFileMode.FM_EXISTING);
		
		return slotFile;
	}
	
	/**
	 * Utility method that reads all of the slot headers in the supplied
	 * SlotFile and returns them.
	 * @param slotFile the SlotFile to read.
	 * @return a TreeMap keyed by file position containing the SlotHeader at 
	 * each position.
	 * @throws IOException if there is an error reading the file.
	 */
	private TreeMap<Integer, SlotHeader> getSlotInfo(SlotFile slotFile) throws IOException {
		
		TreeMap<Integer, SlotHeader> headers = new TreeMap<Integer, SlotHeader>();
		
		int position = slotFile.getUserDataPtr();
		int length = slotFile.seekToEnd();
		
		while (position < length) {
			slotFile.seek(position);
			SlotHeader slotHeader = slotFile.readSlotHeader();
			headers.put(position, slotHeader);
			position += slotHeader.SlotSize + SlotHeader.SIZE;
		}
		
		return headers;
	}
	
	/**
	 * Test method for {@link au.org.ala.delta.slotfile.SlotFile#allocSlot(int, int, int, int)}.
	 */
	@Test
	public void testAllocSlot() throws Exception {
		
		TreeMap<Integer, SlotHeader> preexistingSlots = getSlotInfo(slotFile);
		
		final int SLOT_SIZE = 2000;
		final int SLOT_TYPE_ID = 100;
		final int SLOT_DATA_SIZE = 5000;
		final int SLOW_GROW_SIZE = 2500;
		
		int filePos = slotFile.allocSlot(SLOT_SIZE, SLOT_TYPE_ID, SLOT_DATA_SIZE, SLOW_GROW_SIZE);
		slotFile.seek(filePos);
		
		// read back the new slot.
		SlotHeader newSlot = slotFile.readSlotHeader();
		assertEquals(SLOT_SIZE, newSlot.SlotSize);
		assertEquals(SLOT_TYPE_ID, newSlot.SlotId);
		assertEquals(SLOT_DATA_SIZE, newSlot.DataSize);
		assertEquals(SLOW_GROW_SIZE, newSlot.GrowSize);
		
		// Now make sure we didn't corrupt anything.
		TreeMap<Integer, SlotHeader> postInsertSlots = getSlotInfo(slotFile);
		
		assertEquals(preexistingSlots.keySet().size()+1, postInsertSlots.keySet().size());
		assertEquals(postInsertSlots.lastKey().intValue(), filePos);
		
		for (int position : preexistingSlots.keySet()) {
			SlotHeader existing = preexistingSlots.get(position);
			SlotHeader afterwards = postInsertSlots.get(position);
			
			assertEquals(existing.SlotSize, afterwards.SlotSize);
			assertEquals(existing.SlotId, afterwards.SlotId);
			assertEquals(existing.DataSize, afterwards.DataSize);
			assertEquals(existing.GrowSize, afterwards.GrowSize);
			
		}
		
		slotFile.close();
	}
	
	
	/**
	 * Tests the growSlotData method.
	 * TODO add a test for reusing a slot.
	 */
	@Test public void testGrowSlotData() throws Exception {
		
		TreeMap<Integer, SlotHeader> preexistingSlots = getSlotInfo(slotFile);
		
		int firstPos = preexistingSlots.firstKey();
		int GROW_BY = 1000;
		
		slotFile.growSlotData(firstPos, GROW_BY, false);
	
		TreeMap<Integer, SlotHeader> postGrowSlots = getSlotInfo(slotFile);
		
		// What we expect to have happened is the first slot has been added
		// to the free list & a new, bigger slot appended to the end.
		Map<Integer,Integer> freeSlots = slotFile.getFreeSlotMap();
		assertEquals(1, freeSlots.size());
		assertNotNull(freeSlots.get(firstPos));
		
		assertEquals(preexistingSlots.keySet().size()+1, postGrowSlots.keySet().size());
		
		SlotHeader firstHeader = preexistingSlots.get(firstPos);
		SlotHeader movedFirstHeader = postGrowSlots.get(postGrowSlots.lastKey());
		
		assertEquals(firstHeader.DataSize+GROW_BY, movedFirstHeader.SlotSize);
		assertEquals(firstHeader.SlotId, movedFirstHeader.SlotId);
		assertEquals(firstHeader.DataSize+GROW_BY, movedFirstHeader.DataSize);
		assertEquals(firstHeader.GrowSize, movedFirstHeader.GrowSize);
	}
	
	/**
	 * Tests the copySlotData method.
	 * TODO add a test for: large data size (above buffer size) and make sure the data is actually 
	 * the same
	 */
	@Test public void testCopySlotData() throws Exception {
		
		TreeMap<Integer, SlotHeader> preexistingSlots = getSlotInfo(slotFile);
		
		// We are going to copy the first slot to the end of the file
		// (both source and destination files are the same instance).
		int firstPos = preexistingSlots.firstKey();
		SlotHeader firstHeader = preexistingSlots.get(firstPos);
		
		slotFile.allocSlot(firstHeader.SlotSize, firstHeader.SlotId, firstHeader.DataSize, firstHeader.GrowSize);
		
		slotFile.copySlotData(firstPos, slotFile);
	
		TreeMap<Integer, SlotHeader> postCopySlots = getSlotInfo(slotFile);
		
		assertEquals(preexistingSlots.keySet().size()+1, postCopySlots.keySet().size());
		
		SlotHeader copiedFirstHeader = postCopySlots.get(postCopySlots.lastKey());
		
		assertEquals(firstHeader.SlotSize, copiedFirstHeader.SlotSize);
		assertEquals(firstHeader.SlotId, copiedFirstHeader.SlotId);
		assertEquals(firstHeader.DataSize, copiedFirstHeader.DataSize);
		assertEquals(firstHeader.GrowSize, copiedFirstHeader.GrowSize);
	}

}
