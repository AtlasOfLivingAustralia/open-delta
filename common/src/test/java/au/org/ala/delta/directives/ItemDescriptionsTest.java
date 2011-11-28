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
package au.org.ala.delta.directives;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.TextCharacter;

public class ItemDescriptionsTest extends TestCase {
	
	
	private MutableDeltaDataSet _dataSet;
	private DeltaContext _context;
	private ItemDescriptions _itemDescriptions;
	
	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		_context.setMaximumNumberOfItems(1);
		_itemDescriptions = new ItemDescriptions();
	}
	
	@Test
	public void testParsing() throws Exception {
		addTextCharacter();
		
		String itemDescription = 
			"# Blah/\n"+
			"   1<Cyperus aggregatus (Willd.) Endl., Catalogus horti academici vindobonensis 1:\n"+
            "93. 1842. \\{Cat. Horti Vindob. \\} \\par{}Mariscus aggregatus Willd., Enumeratio\n"+
            "Plantarum Horti Botanici Berolinensis, 1: 70. 1809.>\n";
		try {
			_itemDescriptions.parseAndProcess(_context, itemDescription);
			//checkError(135);
		}
		catch (DirectiveException e) {
			fail("Should have added error to context");
		}
		
		
	}
	
	
	private TextCharacter addTextCharacter() {
		_context.setNumberOfCharacters(_context.getNumberOfCharacters()+1);
		TextCharacter character = (TextCharacter)_dataSet.addCharacter(CharacterType.Text);
		return character;
	}
	
	private void checkError(int... numbers) {
		List<DirectiveError> errors = _context.getErrors();
		assertEquals(numbers.length, errors.size());
		int i=0;
		for (DirectiveError error : errors) {
			assertEquals(numbers[i++], error.getErrorNumber());
		}
	}

}
