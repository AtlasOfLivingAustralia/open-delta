package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.FloatRange;
import org.junit.Test;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParseException;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.IntegerValue;
import au.org.ala.delta.intkey.model.specimen.MultiStateValue;
import au.org.ala.delta.intkey.model.specimen.RealValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.model.specimen.TextValue;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

/**
 * Unit tests for the USE directive
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

        MultiStateValue val1 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(3), val1.getStateValues());

        // Set multiple states with "/" (or) character
        new UseDirective().parseAndProcess(context, "/M 78,1/3/5");

        MultiStateValue val2 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(1, 3, 5), val2.getStateValues());

        // Set multiple states with "-" (range) character
        new UseDirective().parseAndProcess(context, "/M 78,2-4");

        MultiStateValue val3 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(2, 3, 4), val3.getStateValues());

        // Set multiple states with both "/" and "-" characters
        new UseDirective().parseAndProcess(context, "/M 78,1-2/3/4-5");

        MultiStateValue val4 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), val4.getStateValues());

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

        IntegerCharacter charStamens = (IntegerCharacter) context.getDataset().getCharacter(60);

        // set single value
        new UseDirective().parseAndProcess(context, "60,3");
        IntegerValue val1 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(3), val1.getValues());

        // set a range of values
        new UseDirective().parseAndProcess(context, "/M 60,2-5");
        IntegerValue val2 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(2, 3, 4, 5), val2.getValues());

        // set more than one value using the "/" (or) separator
        new UseDirective().parseAndProcess(context, "/M 60,2/5");
        IntegerValue val3 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(2, 5), val3.getValues());

        // use a combination of single values, and ranges
        new UseDirective().parseAndProcess(context, "/M 60,1-3/4/5-6");
        IntegerValue val4 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), val4.getValues());

        // set a value below the character minimum - the value set for the
        // character should be
        // one below the minimum - this value represents any values below the
        // character minimum
        new UseDirective().parseAndProcess(context, "/M 60,-100");
        IntegerValue val5 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(charStamens.getMinimumValue() - 1), val5.getValues());

        // set a value above the character maximum - the value set for the
        // character should be
        // one above the maximum - this value represents any values below the
        // character maximum
        new UseDirective().parseAndProcess(context, "/M 60,100");
        IntegerValue val6 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(charStamens.getMaximumValue() + 1), val6.getValues());

        // set a large range that span both below and above the characters
        // minimum and maximum
        new UseDirective().parseAndProcess(context, "/M 60,-100-100");
        IntegerValue val7 = (IntegerValue) context.getSpecimen().getValueForCharacter(charStamens);
        assertEquals(Arrays.asList(charStamens.getMinimumValue() - 1, 0, 1, 2, 3, 4, 5, 6, charStamens.getMaximumValue() + 1), val7.getValues());

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

        RealValue val1 = (RealValue) context.getSpecimen().getValueForCharacter(charCulmsMaxHeight);
        assertEquals(new FloatRange(10, 10), val1.getRange());

        // Set range
        new UseDirective().parseAndProcess(context, "/M 3,15-20");

        RealValue val2 = (RealValue) context.getSpecimen().getValueForCharacter(charCulmsMaxHeight);
        assertEquals(new FloatRange(15, 20), val2.getRange());

        // Set range using "/" character - for a real character, this should be
        // treated
        // the same as the "-" character.
        new UseDirective().parseAndProcess(context, "/M 3,50/100");

        RealValue val3 = (RealValue) context.getSpecimen().getValueForCharacter(charCulmsMaxHeight);
        assertEquals(new FloatRange(50, 100), val3.getRange());

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
        TextValue val1 = (TextValue) context.getSpecimen().getValueForCharacter(charIncluding);
        assertEquals(Arrays.asList("foo"), val1.getValues());

        // Set text value containing spaces
        new UseDirective().parseAndProcess(context, "/M 1,\"foo and bar\"");
        TextValue val2 = (TextValue) context.getSpecimen().getValueForCharacter(charIncluding);
        assertEquals(Arrays.asList("foo and bar"), val2.getValues());

        // Set multiple text values
        new UseDirective().parseAndProcess(context, "/M 1,foo/bar");
        TextValue val3 = (TextValue) context.getSpecimen().getValueForCharacter(charIncluding);
        assertEquals(Arrays.asList("foo", "bar"), val3.getValues());

        // Multiple text values containing spaces
        new UseDirective().parseAndProcess(context, "/M 1,\"foo and bar/one/two and three\"");
        TextValue val4 = (TextValue) context.getSpecimen().getValueForCharacter(charIncluding);
        assertEquals(Arrays.asList("foo and bar", "one", "two and three"), val4.getValues());

        // Mismatched quotes
        new UseDirective().parseAndProcess(context, "/M 1,\"one and two\"three");
        TextValue val5 = (TextValue) context.getSpecimen().getValueForCharacter(charIncluding);
        assertEquals(Arrays.asList("\"one and two\"three"), val5.getValues());
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
        context.setProcessingInputFile(true);

        new UseDirective().parseAndProcess(context, "3,2");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3)), specimen.getUsedCharacters());

        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferences.size());

        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(5)));

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

        taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferences.size());

        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(5)));

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

        MultiStateValue valLongevity = (MultiStateValue) specimen.getValueForCharacter(charLongevity);
        RealValue valCulmsMaxHeight = (RealValue) specimen.getValueForCharacter(charCulmsMaxHeight);
        MultiStateValue valCulmsWoodyHerbacious = (MultiStateValue) specimen.getValueForCharacter(charCulmsWoodyHerbacious);
        MultiStateValue valCulmsBranchedAbove = (MultiStateValue) specimen.getValueForCharacter(charCulmsBranchedAbove);
        MultiStateValue valInfloresence = (MultiStateValue) specimen.getValueForCharacter(charInfloresence);

        assertEquals(Arrays.asList(1), valLongevity.getStateValues());
        assertEquals(new FloatRange(1.0, 1.0), valCulmsMaxHeight.getRange());
        assertEquals(Arrays.asList(1), valCulmsWoodyHerbacious.getStateValues());
        assertEquals(Arrays.asList(1), valCulmsBranchedAbove.getStateValues());
        assertEquals(Arrays.asList(1), valInfloresence.getStateValues());

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

        MultiStateValue charSeedPresenceValue = (MultiStateValue) specimen.getValueForCharacter(charSeedPresence);
        assertEquals(1, charSeedPresenceValue.getStateValues().size());
        assertEquals(1, (int) charSeedPresenceValue.getStateValues().get(0));

        MultiStateValue charSeedInShellValue = (MultiStateValue) specimen.getValueForCharacter(charSeedInShell);
        assertEquals(1, charSeedInShellValue.getStateValues().size());
        assertEquals(1, (int) charSeedInShellValue.getStateValues().get(0));

        RealValue charAvgThicknessValue = (RealValue) specimen.getValueForCharacter(charAvgThickness);
        assertEquals(new FloatRange(1.0, 1.0), charAvgThicknessValue.getRange());
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

        MultiStateValue charSeedPresenceValue = (MultiStateValue) specimen.getValueForCharacter(charSeedPresence);
        assertEquals(1, charSeedPresenceValue.getStateValues().size());
        assertEquals(2, (int) charSeedPresenceValue.getStateValues().get(0));

        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
    }

    /**
     * Test that when a character is made inapplicable my multiple controlling
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

        new UseDirective().parseAndProcess(context, "/M 2,1");

        availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(charSeedInShell));
        assertTrue(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
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

        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences.size());

        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(5)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(6)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(7)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(8)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(9)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(10)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(11)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(12)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(13)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(14)));
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
        context.setProcessingInputFile(true);

        IntkeyDataset ds = context.getDataset();

        new UseDirective().parseAndProcess(context, "3-4,1");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(2), ds.getCharacter(3), ds.getCharacter(4)), specimen.getUsedCharacters());

        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferences.size());

        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(5)));
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
        context.setProcessingInputFile(true);

        IntkeyDataset ds = context.getDataset();

        new UseDirective().parseAndProcess(context, "2-4,2");

        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(2)), specimen.getUsedCharacters());

        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(5, taxonDifferences.size());

        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(5)));
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

    // USE CC
    // Non auto cc

}
