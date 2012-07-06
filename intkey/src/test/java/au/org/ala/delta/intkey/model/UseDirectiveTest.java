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
package au.org.ala.delta.intkey.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.junit.Test;

import au.org.ala.delta.directives.DirectiveSearchResult;
import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParseException;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParser;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.util.Pair;

/**
 * Unit tests for the USE directive
 * 
 * @author ChrisF
 * 
 */
public class UseDirectiveTest extends IntkeyDatasetTestCase {

    /**
     * Test setting a value for a multi state character
     * 
     * @throws Exception
     */
    @Test
    public void testSetMultiState() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        MultiStateCharacter charSubfamily = (MultiStateCharacter) context.getDataset().getCharacter(78);

        // Set single state
        new UseDirective().parseAndProcess(context, "78,3");

        List<Integer> presentStatesAsList;

        MultiStateAttribute val1 = (MultiStateAttribute) context.getSpecimen().getAttributeForCharacter(charSubfamily);
        presentStatesAsList = new ArrayList<Integer>(val1.getPresentStates());
        Collections.sort(presentStatesAsList);
        assertEquals(Arrays.asList(3), presentStatesAsList);

        // Set multiple states with "/" (or) character
        new UseDirective().parseAndProcess(context, "/M 78,1/3/5");

        MultiStateAttribute val2 = (MultiStateAttribute) context.getSpecimen().getAttributeForCharacter(charSubfamily);
        presentStatesAsList = new ArrayList<Integer>(val2.getPresentStates());
        Collections.sort(presentStatesAsList);
        assertEquals(Arrays.asList(1, 3, 5), presentStatesAsList);

        // Set multiple states with "-" (range) character
        new UseDirective().parseAndProcess(context, "/M 78,2-4");

        MultiStateAttribute val3 = (MultiStateAttribute) context.getSpecimen().getAttributeForCharacter(charSubfamily);
        presentStatesAsList = new ArrayList<Integer>(val3.getPresentStates());
        Collections.sort(presentStatesAsList);
        assertEquals(Arrays.asList(2, 3, 4), presentStatesAsList);

        // Set multiple states with both "/" and "-" characters
        new UseDirective().parseAndProcess(context, "/M 78,1-2/3/4-5");

        MultiStateAttribute val4 = (MultiStateAttribute) context.getSpecimen().getAttributeForCharacter(charSubfamily);
        presentStatesAsList = new ArrayList<Integer>(val4.getPresentStates());
        Collections.sort(presentStatesAsList);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), presentStatesAsList);

        Intkey intkey = new Intkey();

        // Attempt to set an invalid state number
        boolean exception1Thrown = false;
        try {
            new UseDirective().parseAndProcess(context, "/M 78,10");
        } catch (IntkeyDirectiveParseException ex) {
            exception1Thrown = true;
        }
        assertTrue("Expected exception when setting state value with incorrect format", exception1Thrown);

        // Attempt to set states using incorrect format
        boolean exception2Thrown = false;
        try {
            new UseDirective().parseAndProcess(context, "/M 78,blah");
        } catch (IntkeyDirectiveParseException ex) {
            exception2Thrown = true;
        }

        assertTrue("Expected exception when setting state value with incorrect format", exception2Thrown);
    }

    /**
     * Test setting a value for an integer character
     * 
     * @throws Exception
     */
    @Test
    public void testSetInteger() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        List<Integer> presentValuesAsList;

        IntegerCharacter charStamens = (IntegerCharacter) context.getDataset().getCharacter(60);

        // set single value
        new UseDirective().parseAndProcess(context, "60,3");
        IntegerAttribute val1 = (IntegerAttribute) context.getSpecimen().getAttributeForCharacter(charStamens);
        presentValuesAsList = new ArrayList<Integer>(val1.getPresentValues());
        Collections.sort(presentValuesAsList);
        assertEquals(Arrays.asList(3), presentValuesAsList);

        // set a range of values
        new UseDirective().parseAndProcess(context, "/M 60,2-5");
        IntegerAttribute val2 = (IntegerAttribute) context.getSpecimen().getAttributeForCharacter(charStamens);
        presentValuesAsList = new ArrayList<Integer>(val2.getPresentValues());
        Collections.sort(presentValuesAsList);
        assertEquals(Arrays.asList(2, 3, 4, 5), presentValuesAsList);

        // set more than one value using the "/" (or) separator
        new UseDirective().parseAndProcess(context, "/M 60,2/5");
        IntegerAttribute val3 = (IntegerAttribute) context.getSpecimen().getAttributeForCharacter(charStamens);
        presentValuesAsList = new ArrayList<Integer>(val3.getPresentValues());
        Collections.sort(presentValuesAsList);
        assertEquals(Arrays.asList(2, 5), presentValuesAsList);

        // use a combination of single values, and ranges
        new UseDirective().parseAndProcess(context, "/M 60,1-3/4/5-6");
        IntegerAttribute val4 = (IntegerAttribute) context.getSpecimen().getAttributeForCharacter(charStamens);
        presentValuesAsList = new ArrayList<Integer>(val4.getPresentValues());
        Collections.sort(presentValuesAsList);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), presentValuesAsList);

        // Attempt to set integer character using incorrect format
        boolean exceptionThrown = false;
        try {
            new UseDirective().parseAndProcess(context, "/M 6,foo");
        } catch (IntkeyDirectiveParseException ex) {
            exceptionThrown = true;
        }
        assertTrue("Expected exception when setting integer value with incorrect format", exceptionThrown);

    }

    /**
     * Test setting a value for a real character
     * 
     * @throws Exception
     */
    @Test
    public void testSetReal() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        RealCharacter charCulmsMaxHeight = (RealCharacter) context.getDataset().getCharacter(3);

        // Set single value
        new UseDirective().parseAndProcess(context, "3,10");

        RealAttribute val1 = (RealAttribute) context.getSpecimen().getAttributeForCharacter(charCulmsMaxHeight);
        assertEquals(new FloatRange(10, 10), val1.getPresentRange());

        // Set range
        new UseDirective().parseAndProcess(context, "/M 3,15-20");

        RealAttribute val2 = (RealAttribute) context.getSpecimen().getAttributeForCharacter(charCulmsMaxHeight);
        assertEquals(new FloatRange(15, 20), val2.getPresentRange());

        // Set range using "/" character - for a real character, this should be
        // treated
        // the same as the "-" character.
        new UseDirective().parseAndProcess(context, "/M 3,50/100");

        RealAttribute val3 = (RealAttribute) context.getSpecimen().getAttributeForCharacter(charCulmsMaxHeight);
        assertEquals(new FloatRange(50, 100), val3.getPresentRange());

        // Attempt to set real character using incorrect format
        boolean exceptionThrown = false;
        try {
            new UseDirective().parseAndProcess(context, "/M 3,50-foo");
        } catch (IntkeyDirectiveParseException ex) {
            exceptionThrown = true;
        }
        assertTrue("Expected exception when setting real value with incorrect format", exceptionThrown);

    }

    /**
     * Test setting a value for a text character
     * 
     * @throws Exception
     */
    @Test
    public void testSetText() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        TextCharacter charIncluding = (TextCharacter) context.getDataset().getCharacter(1);

        // Set simple text value
        new UseDirective().parseAndProcess(context, "1,foo");
        TextAttribute val1 = (TextAttribute) context.getSpecimen().getAttributeForCharacter(charIncluding);
        assertEquals("foo", val1.getText());

        // Set text value containing spaces
        new UseDirective().parseAndProcess(context, "/M 1,\"foo and bar\"");
        TextAttribute val2 = (TextAttribute) context.getSpecimen().getAttributeForCharacter(charIncluding);
        assertEquals("foo and bar", val2.getText());

        // Set multiple text values
        new UseDirective().parseAndProcess(context, "/M 1,foo/bar");
        TextAttribute val3 = (TextAttribute) context.getSpecimen().getAttributeForCharacter(charIncluding);
        assertEquals("foo/bar", val3.getText());

        // Multiple text values containing spaces
        new UseDirective().parseAndProcess(context, "/M 1,\"foo and bar/one/two and three\"");
        TextAttribute val4 = (TextAttribute) context.getSpecimen().getAttributeForCharacter(charIncluding);
        assertEquals("foo and bar/one/two and three", val4.getText());

        // Mismatched quotes
        new UseDirective().parseAndProcess(context, "/M 1,\"one and two\"three");
        TextAttribute val5 = (TextAttribute) context.getSpecimen().getAttributeForCharacter(charIncluding);
        assertEquals("\"one and two\"three", val5.getText());
    }

    /**
     * Test setting the value of a character twice. The character is both a
     * controlling and dependent character.
     * 
     * @throws Exception
     */
    @Test
    public void testSetTwice() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        // Set processing input file flag to true so that Intkey will
        // automatically
        // set the values of controlling characters as opposed to prompting for
        // them using
        // modal dialogs.
        context.setProcessingDirectivesFile(true);

        new UseDirective().parseAndProcess(context, "3,2");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3)), specimen.getUsedCharacters());

        Map<Item, Set<au.org.ala.delta.model.Character>> taxonDifferingCharacters = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferingCharacters.size());

        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(1)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(2)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(3)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(4)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(5)).size());

        List<au.org.ala.delta.model.Character> availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(ds.getCharacter(1)));
        assertFalse(availableCharacters.contains(ds.getCharacter(2)));
        assertFalse(availableCharacters.contains(ds.getCharacter(3)));
        assertFalse(availableCharacters.contains(ds.getCharacter(4)));
        assertTrue(availableCharacters.contains(ds.getCharacter(5)));
        assertTrue(availableCharacters.contains(ds.getCharacter(6)));
        assertTrue(availableCharacters.contains(ds.getCharacter(7)));
        assertTrue(availableCharacters.contains(ds.getCharacter(8)));

        new UseDirective().parseAndProcess(context, "3,2");

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3)), specimen.getUsedCharacters());

        taxonDifferingCharacters = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferingCharacters.size());

        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(1)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(2)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(3)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(4)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(5)).size());

        availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(ds.getCharacter(1)));
        assertFalse(availableCharacters.contains(ds.getCharacter(2)));
        assertFalse(availableCharacters.contains(ds.getCharacter(3)));
        assertFalse(availableCharacters.contains(ds.getCharacter(4)));
        assertTrue(availableCharacters.contains(ds.getCharacter(5)));
        assertTrue(availableCharacters.contains(ds.getCharacter(6)));
        assertTrue(availableCharacters.contains(ds.getCharacter(7)));
        assertTrue(availableCharacters.contains(ds.getCharacter(8)));
    }

    /**
     * Test setting a value for a non existent character, i.e. supply an invalid
     * character number.
     * 
     * @throws Exception
     */
    @Test
    public void testSetNonExistentCharacter() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        boolean exceptionThrown = false;
        try {
            new UseDirective().parseAndProcess(context, "666,1");
        } catch (IntkeyDirectiveParseException ex) {
            exceptionThrown = true;
        }

        assertTrue("Expecting exception thrown for non-existent character number", exceptionThrown);
    }

    /**
     * Test the use of the USE directive using a character keyword in place of
     * character numbers
     * 
     * @throws Exception
     */
    @Test
    public void testKeyword() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charLongevity = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        RealCharacter charCulmsMaxHeight = (RealCharacter) ds.getCharacter(3);
        UnorderedMultiStateCharacter charCulmsWoodyHerbacious = (UnorderedMultiStateCharacter) ds.getCharacter(4);
        UnorderedMultiStateCharacter charCulmsBranchedAbove = (UnorderedMultiStateCharacter) ds.getCharacter(5);
        UnorderedMultiStateCharacter charInfloresence = (UnorderedMultiStateCharacter) ds.getCharacter(13);

        new UseDirective().parseAndProcess(context, "habit,1");
        Specimen specimen = context.getSpecimen();

        MultiStateAttribute valLongevity = (MultiStateAttribute) specimen.getAttributeForCharacter(charLongevity);
        RealAttribute valCulmsMaxHeight = (RealAttribute) specimen.getAttributeForCharacter(charCulmsMaxHeight);
        MultiStateAttribute valCulmsWoodyHerbacious = (MultiStateAttribute) specimen.getAttributeForCharacter(charCulmsWoodyHerbacious);
        MultiStateAttribute valCulmsBranchedAbove = (MultiStateAttribute) specimen.getAttributeForCharacter(charCulmsBranchedAbove);
        MultiStateAttribute valInfloresence = (MultiStateAttribute) specimen.getAttributeForCharacter(charInfloresence);

        assertEquals(Arrays.asList(1), new ArrayList<Integer>(valLongevity.getPresentStates()));
        assertEquals(new FloatRange(1.0, 1.0), valCulmsMaxHeight.getPresentRange());
        assertEquals(Arrays.asList(1), new ArrayList<Integer>(valCulmsWoodyHerbacious.getPresentStates()));
        assertEquals(Arrays.asList(1), new ArrayList<Integer>(valCulmsBranchedAbove.getPresentStates()));
        assertEquals(Arrays.asList(1), new ArrayList<Integer>(valInfloresence.getPresentStates()));

    }

    /**
     * Test that when a character is used, values are set for its controlling
     * characters (when the NONAUTOMATIC CONTROLLING CHARACTERS and USE
     * CONTROLLING CHARACTERS FIRST directives are not used)
     * 
     * @throws Exception
     */
    @Test
    public void testControllingCharactersSet() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        new UseDirective().parseAndProcess(context, "4,1");

        Specimen specimen = context.getSpecimen();

        MultiStateAttribute charSeedPresenceValue = (MultiStateAttribute) specimen.getAttributeForCharacter(charSeedPresence);
        assertEquals(1, charSeedPresenceValue.getPresentStates().size());
        assertEquals(1, charSeedPresenceValue.getPresentStates().toArray()[0]);

        MultiStateAttribute charSeedInShellValue = (MultiStateAttribute) specimen.getAttributeForCharacter(charSeedInShell);
        assertEquals(1, charSeedInShellValue.getPresentStates().size());
        assertEquals(1, charSeedInShellValue.getPresentStates().toArray()[0]);

        RealAttribute charAvgThicknessValue = (RealAttribute) specimen.getAttributeForCharacter(charAvgThickness);
        assertEquals(new FloatRange(1.0, 1.0), charAvgThicknessValue.getPresentRange());
    }

    /**
     * Test that when the value of a character is changed, the values set for
     * any dependent characters are removed from the specimen.
     * 
     * @throws Exception
     */
    @Test
    public void testDependentCharactersRemoved() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        new UseDirective().parseAndProcess(context, "4,1");

        new UseDirective().parseAndProcess(context, "/M 2,2");

        Specimen specimen = context.getSpecimen();

        au.org.ala.delta.model.MultiStateAttribute charSeedPresenceValue = (MultiStateAttribute) specimen.getAttributeForCharacter(charSeedPresence);
        assertEquals(1, charSeedPresenceValue.getPresentStates().size());
        assertEquals(2, charSeedPresenceValue.getPresentStates().toArray()[0]);

        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
    }

    /**
     * Test that when a character is made inapplicable by multiple controlling
     * characters, it says marked as inapplicable until the values of all
     * controlling characters are deleted or changed such that the character is
     * no longer inapplicable
     * 
     * @throws Exception
     */
    @Test
    public void testAvailabilityMultipleControllingCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charThree = ds.getCharacter(3);

        assertTrue(context.getAvailableCharacters().contains(charThree));

        new UseDirective().parseAndProcess(context, "1,2");

        assertFalse(context.getAvailableCharacters().contains(charThree));

        new UseDirective().parseAndProcess(context, "2,2");

        assertFalse(context.getAvailableCharacters().contains(charThree));

        new UseDirective().parseAndProcess(context, "/M 2,1");

        assertFalse(context.getAvailableCharacters().contains(charThree));

        new UseDirective().parseAndProcess(context, "/M 1,1");

        assertTrue(context.getAvailableCharacters().contains(charThree));

        // TODO do restart and follow same process but delete values for
        // characters 1 and 2
        // instead of changing their values
    }

    /**
     * Test that when a controlling character is set, dependent characters are
     * available/not available as appropriate, and that previously set values
     * for characters that are now inapplicable are removed from the specimen
     * 
     * @throws Exception
     */
    @Test
    public void testAvailabilityControlHierarchy() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        new UseDirective().parseAndProcess(context, "4,1");

        Specimen specimen = context.getSpecimen();

        new UseDirective().parseAndProcess(context, "/M 2,2");

        List<au.org.ala.delta.model.Character> availableCharacters = context.getAvailableCharacters();
        assertFalse(availableCharacters.contains(charSeedInShell));
        assertFalse(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertTrue(specimen.isCharacterInapplicable(charSeedInShell));
        assertTrue(specimen.isCharacterInapplicable(charAvgThickness));

        new UseDirective().parseAndProcess(context, "/M 2,1");

        availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(charSeedInShell));
        assertTrue(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertFalse(specimen.isCharacterInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterInapplicable(charAvgThickness));
    }

    /**
     * Test that a taxon that has an attribute that has both values specified
     * and the inapplicability flag set to true in the data file is handled
     * correctly by the USE command.
     * 
     * @throws Exception
     */
    @Test
    public void testAttributeWithValuesAndInapplicabilityFlag() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        // Check that the taxon "Oryza" - number 10 - is eliminated when
        // character 38 is given a value
        // of 5. Oryza is listed in the data file as both having a value for
        // character 38 - 0 - and
        // having the inapplicability flag set to true for character 38.

        new UseDirective().parseAndProcess(context, "38,5");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(32), ds.getCharacter(38)), specimen.getUsedCharacters());

        Map<Item, Set<Character>> taxonDifferingCharacters = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferingCharacters.size());

        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(1)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(2)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(3)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(4)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(5)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(6)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(7)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(8)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(9)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(10)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(11)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(12)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(13)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(14)).size());
    }

    /**
     * Set values for both a controlling character and its dependent, when there
     * is another character that controls both of them
     * 
     * @throws Exception
     */
    @Test
    public void testSetCCAndDependentShareControllingCharacter() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_shared_cc/intkey.ink");

        // Set processing input file flag to true so that Intkey will
        // automatically
        // set the values of controlling characters as opposed to prompting for
        // them using
        // modal dialogs.
        context.setProcessingDirectivesFile(true);

        IntkeyDataset ds = context.getDataset();

        new UseDirective().parseAndProcess(context, "3-4,1");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3), ds.getCharacter(4)), specimen.getUsedCharacters());

        Map<Item, Set<Character>> taxonDifferingCharacters = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferingCharacters.size());

        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(1)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(2)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(3)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(4)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(5)).size());
    }

    /**
     * Attempt to set values for both a controlling character and its dependent
     * when both of these character are inapplicable due to the value of their
     * shared controlling character.
     * 
     * @throws Exception
     */
    @Test
    public void testValuesForInapplicableCharactersWithSharedCC() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_shared_cc/intkey.ink");

        // Set processing input file flag to true so that Intkey will
        // automatically
        // set the values of controlling characters as opposed to prompting for
        // them using
        // modal dialogs.
        context.setProcessingDirectivesFile(true);

        IntkeyDataset ds = context.getDataset();

        new UseDirective().parseAndProcess(context, "2-4,2");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(2)), specimen.getUsedCharacters());

        Map<Item, Set<Character>> taxonDifferingCharacters = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferingCharacters.size());

        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(1)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(2)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(3)).size());
        assertEquals(1, taxonDifferingCharacters.get(ds.getItem(4)).size());
        assertEquals(0, taxonDifferingCharacters.get(ds.getItem(5)).size());
    }

    /**
     * Test that the specimen already has a value set for a controlling
     * character, no attempt is made to change that value when subsequently
     * setting the value of one of the dependent characters
     * 
     * @throws Exception
     */
    public void testControllingCharacterAlreadySet() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_multi_available/intkey.ink");
        
        ReportCharacterValuePromptDirectivePopulator populator = new ReportCharacterValuePromptDirectivePopulator();
        
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), populator);
        SwingWorker<?, ?> worker = context.newDataSetFile(new File(initFileUrl.toURI()));

        // The dataset is loaded on a separate thread so we need to wait until
        // it is loaded.
        worker.get();
        
        new UseDirective().parseAndProcess(context, "2,1");
        new UseDirective().parseAndProcess(context, "5,1");
        
        assertFalse(populator.promptedForCharacterValue());
    }
    
    private class ReportCharacterValuePromptDirectivePopulator implements DirectivePopulator {
        
        private boolean _promptedForCharacterValue = false;
        
        public boolean promptedForCharacterValue() {
            return _promptedForCharacterValue;
        }
        
        @Override
        public Boolean promptForYesNoOption(String message) {
            return null;
        }
        
        @Override
        public List<String> promptForTextValue(TextCharacter ch) {
            _promptedForCharacterValue = true;
            return null;
        }
        
        @Override
        public List<Item> promptForTaxaByList(String directiveName, boolean selectFromIncludedTaxaOnly, boolean autoSelectSingleValue, boolean singleSelect, boolean includeSpecimenAsOption, MutableBoolean specimenSelected, List<String> returnSelectedKeywords) {
            return null;
        }
        
        @Override
        public List<Item> promptForTaxaByKeyword(String directiveName, boolean permitSelectionFromIncludedTaxaOnly, boolean noneKeywordAvailable, boolean includeSpecimenAsOption, MutableBoolean specimenSelected, List<String> returnSelectedKeywords) {
            return null;
        }
        
        @Override
        public String promptForString(String message, String initialSelectionValue, String directiveName) {
            return null;
        }
        
        @Override
        public FloatRange promptForRealValue(RealCharacter ch) {
            _promptedForCharacterValue = true;
            return null;
        }
        
        @Override
        public Boolean promptForOnOffValue(String directiveName, boolean initialValue) {
            return null;
        }
        
        @Override
        public Set<Integer> promptForMultiStateValue(MultiStateCharacter ch) {
            _promptedForCharacterValue = true;
            return null;
        }
        
        @Override
        public List<Object> promptForMatchSettings() {
            return null;
        }
        
        @Override
        public Set<Integer> promptForIntegerValue(IntegerCharacter ch) {
            _promptedForCharacterValue = true;
            return null;
        }
        
        @Override
        public Pair<ImageDisplayMode, DisplayImagesReportType> promptForImageDisplaySettings() {
            return null;
        }
        
        @Override
        public File promptForFile(List<String> fileExtensions, String description, boolean createFileIfNonExistant) throws IOException {
            return null;
        }
        
        @Override
        public List<Character> promptForCharactersByList(String directiveName, boolean selectFromAvailableCharactersOnly, List<String> returnSelectedKeywords) {
            return null;
        }
        
        @Override
        public List<Character> promptForCharactersByKeyword(String directiveName, boolean permitSelectionFromIncludedCharactersOnly, boolean noneKeywordAvailable, List<String> returnSelectedKeywords) {
            return null;
        }
        
        @Override
        public List<Object> promptForButtonDefinition() {
            return null;
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testNoValidationPromptDialogsWhenProcessingInputFile() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/testNoValidationPromptDialogsWhenProcessingInputFile.ink");
        IntkeyDataset ds = context.getDataset();

        Specimen specimen = context.getSpecimen();

        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(8)), specimen.getUsedCharacters());

        // if test reaches this point, no modal dialogs must have been shown
        // while loading the data set file, so the test is
        // successful
    }

    // The following 4 tests test specific aspects of directive parsing.
    @Test
    public void testAdvancedUse1() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("1,foo");
        Character ch = context.getDataset().getCharacter(1);
        assertEquals(((TextAttribute) context.getSpecimen().getAttributeForCharacter(ch)).getText(), "foo");

    }

    @Test
    public void testAdvancedUse2() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("USE 1,foo");
        Character ch = context.getDataset().getCharacter(1);
        assertEquals(((TextAttribute) context.getSpecimen().getAttributeForCharacter(ch)).getText(), "foo");
    }

    @Test
    public void testAdvancedUse3() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("INCLUDE CHARACTERS 1-10");
        assertEquals(10, context.getIncludedCharacters().size());
    }

    @Test
    public void testAdvancedUse4() throws Exception {
        IntkeyDirectiveParser parser = IntkeyDirectiveParser.createInstance();
        DirectiveSearchResult result = parser.getDirectiveRegistry().findDirective("I");
        assertEquals(DirectiveSearchResult.ResultType.MoreSpecificityRequired, result.getResultType());
    }
    
    /**
     * Test that if a controlling character is set again to its current value, the values of its dependents
     * characters are not removed from the specimen.
     * @throws Exception
     */
    @Test
    public void testReSetControllingCharacter() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("Use 32,1");
        context.parseAndExecuteDirective("Use 38,5");
        context.parseAndExecuteDirective("Use 32,1");
        assertTrue(context.getSpecimen().hasValueFor(ds.getCharacter(38)));
    }
    
    @Test
    public void testMaybeInapplicable() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("Use 32,1/2");
        assertTrue(context.getSpecimen().isCharacterMaybeInapplicable(ds.getCharacter(38)));
    }
    
    /**
     * Test setting the value of a controlling character such that it's dependant character is maybe inapplicable.
     * The value set for this dependant character should not be removed as a result.
     */
    @Test
    public void testMaybeInapplicableDependantCharacterSet() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("Use 32,1");
        context.parseAndExecuteDirective("Use 38,5");
        context.parseAndExecuteDirective("Use 32,1/2");
        assertTrue(context.getSpecimen().isCharacterMaybeInapplicable(ds.getCharacter(38)));
        assertTrue(context.getSpecimen().hasValueFor(ds.getCharacter(38)));
    }    
    
    @Test
    public void testMaybeInapplicableHierarchy() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        Specimen specimen = context.getSpecimen();

        new UseDirective().parseAndProcess(context, "/M 2,1/2");

        List<au.org.ala.delta.model.Character> availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(charSeedInShell));
        assertTrue(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertFalse(specimen.isCharacterInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterInapplicable(charAvgThickness));
        assertTrue(specimen.isCharacterMaybeInapplicable(charSeedInShell));
        assertTrue(specimen.isCharacterMaybeInapplicable(charAvgThickness));
    }
    
    
    /**
     * Test that when a character is re-used with the same value, it is returned as the last
     * element of the list of used characters.
     */
    @Test
    public void testReUsedCharacterSameValueOrdering() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        context.parseAndExecuteDirective("Use 1,1");
        context.parseAndExecuteDirective("Use 2,1");
        context.parseAndExecuteDirective("Use 1,1");
        Specimen specimen = context.getSpecimen();
        assertEquals(2, specimen.getUsedCharacters().size());
        assertEquals(ds.getCharacter(2), specimen.getUsedCharacters().get(0));
        assertEquals(ds.getCharacter(1), specimen.getUsedCharacters().get(1));
    }
    
    /**
     * Set first setting a character to inapplicable, then to maybe inapplicable
     */
    @Test
    public void testSetInapplicableThenMaybeInapplicable() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        
        Character charThree = ds.getCharacter(3);
        
        context.parseAndExecuteDirective("Use 1,1/2");
        context.parseAndExecuteDirective("Use 2,2");
        Specimen specimen = context.getSpecimen();
        assertTrue(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertFalse(context.getAvailableCharacters().contains(charThree));
        
        context.parseAndExecuteDirective("Use /M 2,1/2");
        context.calculateBestOrSeparateCharacters();
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertTrue(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));
        //All taxa should be available again
        assertEquals(7, context.getAvailableTaxa().size());
    }
    
    /**
     * Set first setting a character to maybe inapplicable, then applicable
     */
    @Test
    public void testSetMaybeInapplicableThenInapplicable() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();
        Specimen specimen = context.getSpecimen();
        
        Character charThree = ds.getCharacter(3);
        Item taxon2 = ds.getItem(2);
        Item taxon6 = ds.getItem(6);
        Item taxon7 = ds.getItem(7);
        
        context.parseAndExecuteDirective("Use 1,1/2");
        context.parseAndExecuteDirective("Use 2,1/2");
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertTrue(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));
        //All taxa should be available
        assertEquals(7, context.getAvailableTaxa().size());
        
        context.parseAndExecuteDirective("Use /M 2,2");
        context.calculateBestOrSeparateCharacters();
        assertTrue(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertFalse(context.getAvailableCharacters().contains(charThree));
        //3 taxa should be available
        assertEquals(3, context.getAvailableTaxa().size());
        assertTrue(context.getAvailableTaxa().contains(taxon2));
        assertTrue(context.getAvailableTaxa().contains(taxon6));
        assertTrue(context.getAvailableTaxa().contains(taxon7));
    }

    // USE CC
    // Non auto cc

}
