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
import au.org.ala.delta.model.MutableDeltaDataSet;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Character notes directive.
 */
public class CharacterNotesTest extends TestCase {

    /** The instance of the class we are going to test.  */
    private CharacterNotes _characterNotes = new CharacterNotes();

    private MutableDeltaDataSet _dataSet;
    private DeltaContext _context;

    @Before
    public void setUp() throws Exception {
        DefaultDataSetFactory factory = new DefaultDataSetFactory();
        _dataSet = factory.createDataSet("test");
        _context = new DeltaContext(_dataSet);
        _context.setNumberOfCharacters(3);
        _context.setMaximumNumberOfItems(3);

        _dataSet.addCharacter(CharacterType.RealNumeric);
        _dataSet.addCharacter(CharacterType.IntegerNumeric);
        _dataSet.addCharacter(CharacterType.RealNumeric);

        _dataSet.addItem();
        _dataSet.addItem();

    }

    /**
     * Tests the simple case where there are notes supplied for a single character.
     */
    @Test
    public void testSimpleDirective() throws Exception {
        String notes = "This is the notes for character 1.";
        String data = "#1. "+ notes;
        _characterNotes.parseAndProcess(_context, data);

        assertEquals(notes, _dataSet.getCharacter(1).getNotes());
        for (int i=2; i<=_dataSet.getNumberOfCharacters(); i++) {
            assertTrue(StringUtils.isBlank(_dataSet.getCharacter(i).getNotes()));
        }
    }

    /**
     * Tests the simple case where there are notes supplied for a single character.
     */
    @Test
    public void testMoreComplexDirective() throws Exception {

        String notes = "This is the notes for character 1.";
        String notes3 = "This is the notes for character 3";
        String data = "#1. "+ notes + System.getProperty("line.separator") + "#3. "+notes3;
        _characterNotes.parseAndProcess(_context, data);

        assertEquals(notes, _dataSet.getCharacter(1).getNotes());
        assertTrue(StringUtils.isBlank(_dataSet.getCharacter(2).getNotes()));
        assertEquals(notes3, _dataSet.getCharacter(3).getNotes());
    }

    /**
     * Tests the case where there are notes supplied for a range of characters.
     */
    @Test
    public void testDirectiveWithRange() throws Exception {

        String notes = "This is the notes for character 1-3.";

        String data = "#1-3. "+ notes;
        _characterNotes.parseAndProcess(_context, data);

        for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
            assertEquals(notes, _dataSet.getCharacter(i).getNotes());
        }
    }

    /**
     * Tests the case where there are notes supplied for a set of characters.
     */
    @Test
    public void testDirectiveWithSet() throws Exception {

        String notes = "This is the notes for character 1-3.";

        String data = "#1:2:3. "+ notes;
        _characterNotes.parseAndProcess(_context, data);

        for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
            assertEquals(notes, _dataSet.getCharacter(i).getNotes());
        }
    }

    /**
     * Tests the case where there are notes supplied for a set of characters.
     */
    @Test
    public void testDirectiveWithSet2() throws Exception {

        String notes = "This is the notes for character 1-3.";

        String data = "#2:3. "+ notes;
        _characterNotes.parseAndProcess(_context, data);

        assertTrue(StringUtils.isBlank(_dataSet.getCharacter(1).getNotes()));
        for (int i=2; i<=_dataSet.getNumberOfCharacters(); i++) {
            assertEquals(notes, _dataSet.getCharacter(i).getNotes());
        }
    }


    /**
     * Tests the case where there are notes supplied for a set of characters.
     */
    @Test
    public void testDirectiveWithRangeAndSet() throws Exception {

        String notes = "This is the notes for character 1-3.";

        String data = "#1:2-3. "+ notes;
        _characterNotes.parseAndProcess(_context, data);

        for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
            assertEquals(notes, _dataSet.getCharacter(i).getNotes());
        }
    }

    /**
     * Tests the case where there are notes supplied for a set of characters.
     */
    @Test
    public void testDirectiveWithRangeAndSet2() throws Exception {

        String notes = "This is the notes for character 1-3.";

        String data = "#1:3. "+ notes + System.getProperty("line.separator") + "#2. "+notes;
        _characterNotes.parseAndProcess(_context, data);

        for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
            assertEquals(notes, _dataSet.getCharacter(i).getNotes());
        }
    }

    /**
     * Tests the case an invalid character number is provided in a set.
     */
    @Test
    public void testInvalidCharNumber() throws Exception {
        String notes = "This is the notes for character 1-3.";

        String data = "#1:4. "+ notes + System.getProperty("line.separator") + "#2. "+notes;
        try {
            _characterNotes.parseAndProcess(_context, data);
            fail("An exception should have been thrown");
        }
        catch (DirectiveException e) {
            assertEquals(DirectiveError.Error.CHARACTER_NUMBER_TOO_HIGH.getErrorNumber(), e.getErrorNumber());
        }

    }

    /**
     * Tests the case a set is terminated with whitespace.
     */
    @Test
    public void testInvalidSetTermination() throws Exception {
        String notes = "This is the notes for character 1-3.";

        String data = "#1:3 "+ notes + System.getProperty("line.separator") + "#2. "+notes;
        try {
            _characterNotes.parseAndProcess(_context, data);
            fail("An exception should have been thrown");
        }
        catch (DirectiveException e) {
            assertEquals(DirectiveError.Error.ILLEGAL_SYMBOL_WITH_ARGS.getErrorNumber(), e.getErrorNumber());
        }

    }


}
