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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.impl.DefaultDataSet;
import junit.framework.TestCase;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests the LinkChararacters directive.
 */
public class LinkCharactersTest extends TestCase {

    private DeltaContext _context;

    @Before
    public void setUp() {
        DefaultDataSetFactory factory = new DefaultDataSetFactory();
        DefaultDataSet dataSet = (DefaultDataSet)factory.createDataSet("test");

        // Type of character doesn't matter for this test.
        int charCount = 20;
        for (int i=0; i<charCount; i++) {
            dataSet.addCharacter(CharacterType.UnorderedMultiState);
        }

        int itemCount = 10;
        for (int i=0; i<itemCount; i++) {
            dataSet.addItem();
        }

        _context = new DeltaContext(dataSet);

    }
	
	public void testLinkCharactersWithValidInput() throws Exception {
		LinkCharacters directive = new LinkCharacters();

		String data = "1-3 6:10-11 7-9";
		
		directive.parseAndProcess(_context, data);
		
	
		Set<Integer> set1 = new HashSet<Integer>();
		set1.add(1);
		set1.add(2);
		set1.add(3);
		
		Set<Integer> set2 = new HashSet<Integer>();
		set2.add(6);
		set2.add(10);
		set2.add(11);
		
		Set<Integer> set3 = new HashSet<Integer>();
		set3.add(7);
		set3.add(8);
		set3.add(9);
		
		
		assertEquals(set1, _context.getLinkedCharacters(1));
		assertEquals(set1, _context.getLinkedCharacters(2));
		assertEquals(set1, _context.getLinkedCharacters(3));
		assertNull(_context.getLinkedCharacters(4));
		assertNull(_context.getLinkedCharacters(5));
		
		assertEquals(set2, _context.getLinkedCharacters(6));
		assertEquals(set3, _context.getLinkedCharacters(7));
		assertEquals(set3, _context.getLinkedCharacters(8));
		assertEquals(set3, _context.getLinkedCharacters(9));
		assertEquals(set2, _context.getLinkedCharacters(10));
		assertEquals(set2, _context.getLinkedCharacters(11));
		
	}
	
}
