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
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.impl.DefaultDataSet;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * Tests the ExcludeCharacters class.
 */
public class ExcludeCharactersTest extends TestCase {

    private DeltaContext _context;
    private ExcludeCharacters _excludeCharacters;

    @Before
    public void setUp() {
        DefaultDataSetFactory factory = new DefaultDataSetFactory();
        DefaultDataSet dataSet = (DefaultDataSet)factory.createDataSet("test");

        // Type of character doesn't matter for this test.
        int charCount = 10;
        for (int i=0; i<charCount; i++) {
            dataSet.addCharacter(CharacterType.UnorderedMultiState);
        }

        int itemCount = 5;
        for (int i=0; i<itemCount; i++) {
            dataSet.addItem();
        }

        _context = new DeltaContext(dataSet);

        _excludeCharacters = new ExcludeCharacters();
    }


    /**
     * Tests the ExcludeCharacters directive can correctly populate the DeltaContext when the input is correct.
     */
    @Test
    public void testExcludeCharacters() throws Exception {

        String data = "1 2 3 10";

        _excludeCharacters.parseAndProcess(_context, data);

        Set<Integer> excluded = _context.getExcludedCharacters();

        int[] expected = {1,2,3,10};
        assertEquals(expected.length, excluded.size());
        for (int i : expected) {
            assertTrue(excluded.contains(i));
        }
    }

    /**
     * Tests the ExcludeCharacters directive correctly responds (with a validation error) when an out of range
     * character is supplied.
     */
    @Test
    public void testExcludeCharactersWithOutOfRangeCharacter() throws Exception {
        String data = "11";
        try {
            _excludeCharacters.parseAndProcess(_context, data);
            fail("Validation exception should have been thrown");
        }
        catch (DirectiveException e) {
            assertEquals(DirectiveError.Error.CHARACTER_NUMBER_TOO_HIGH.getErrorNumber(), e.getError().getErrorNumber());
            assertEquals(1, e.getError().getPosition());
        }
    }

}
