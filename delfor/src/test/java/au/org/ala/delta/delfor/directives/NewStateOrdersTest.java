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
package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.format.FormattingAction;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.CharacterType;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tests the NewStateOrders class.
 */
public class NewStateOrdersTest extends TestCase {

	private DelforContext _context;
	private SlotFileDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		_dataSet = (SlotFileDataSet) dataSetRepository.newDataSet();

        int numChars = 10;
        for (int i=0; i<numChars; i++) {
            _dataSet.addCharacter(CharacterType.UnorderedMultiState);
        }
		
		_context = new DelforContext(_dataSet);
	}
	
	@Test
	public void testNewStateOrders() throws Exception {
		NewStateOrders newStateOrders = new NewStateOrders();
		
		String test = "5,2:1 10,4:1-3:5";
		newStateOrders.parseAndProcess(_context, test);
		
		List<FormattingAction> actions = _context.getFormattingActions();
		assertEquals(2, actions.size());
		
		
	}
	
}
