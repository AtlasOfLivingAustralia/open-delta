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
package au.org.ala.delta.slotfile.model;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VODeltaMasterDesc;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

/**
 * Tests the SlotFileDataSetFactory.
 */
public class SlotFileDataSetFactoryTest extends TestCase {

	/**
	 * Tests the creation of a new SlotFileDataSet.
	 */
	@Test
	public void testSlotFileDataSetFactory() {
		
		SlotFileDataSetFactory factory = new SlotFileDataSetFactory();
		
		SlotFileDataSet dataSet = (SlotFileDataSet)factory.createDataSet("unnamed");
		
		DeltaVOP vop = dataSet.getVOP();
		
		assertNotNull(vop.getDeltaMaster());
		
		VODeltaMasterDesc master = vop.getDeltaMaster();
		
		assertEquals(0, master.getNChars());
		assertEquals(0, master.getNItems());
	}

}
