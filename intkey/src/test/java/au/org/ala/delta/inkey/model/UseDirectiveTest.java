package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.math.FloatRange;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.MultiStateValue;
import au.org.ala.delta.intkey.model.specimen.RealValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class UseDirectiveTest extends TestCase {

    /**
     * Test setting a value for a multi state character
     * 
     * @throws Exception
     */
    @Test
    public void testSetMultiState() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        MultiStateCharacter charSubfamily = (MultiStateCharacter) context.getDataset().getCharacter(78);

        // Set single state
        new UseDirective().process(context, "78,3");

        MultiStateValue val1 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(3), val1.getStateValues());

        // Set multiple states with "/" (or) character
        new UseDirective().process(context, "/M 78,1/3/5");

        MultiStateValue val2 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(1, 3, 5), val2.getStateValues());

        // Set multiple states with "-" (range) character
        new UseDirective().process(context, "/M 78,2-4");

        MultiStateValue val3 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(2, 3, 4), val3.getStateValues());

        // Set multiple states with both "/" and "-" characters
        new UseDirective().process(context, "/M 78,1-2/3/4-5");

        MultiStateValue val4 = (MultiStateValue) context.getSpecimen().getValueForCharacter(charSubfamily);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), val4.getStateValues());

        // Attempt to set states using incorrect format
        try {
            new UseDirective().process(context, "/M 78,blah");
        } catch (IllegalArgumentException ex) {
            return;
        }
        
        fail("Expected exception from last invocation of USE directive");
    }

    /**
     * Test setting a value for an integer character
     * 
     * @throws Exception
     */
    @Test
    public void testSetInteger() throws Exception {

    }

    /**
     * Test setting a value for a real character
     * 
     * @throws Exception
     */
    @Test
    public void testSetReal() throws Exception {

    }

    /**
     * Test setting a value for a text character
     * 
     * @throws Exception
     */
    @Test
    public void testSetText() throws Exception {

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
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        new UseDirective().process(context, "4,1");

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
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        new UseDirective().process(context, "4,1");

        new UseDirective().process(context, "/M 2,2");

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
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charThree = ds.getCharacter(3);

        assertTrue(context.getSpecimen().getAvailableCharacters().contains(charThree));

        new UseDirective().process(context, "1,2");

        assertFalse(context.getSpecimen().getAvailableCharacters().contains(charThree));

        new UseDirective().process(context, "2,2");

        assertFalse(context.getSpecimen().getAvailableCharacters().contains(charThree));

        new UseDirective().process(context, "/M 2,1");

        assertFalse(context.getSpecimen().getAvailableCharacters().contains(charThree));

        new UseDirective().process(context, "/M 1,1");

        assertTrue(context.getSpecimen().getAvailableCharacters().contains(charThree));

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
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);

        new UseDirective().process(context, "4,1");

        Specimen specimen = context.getSpecimen();

        new UseDirective().process(context, "/M 2,2");

        assertFalse(specimen.getAvailableCharacters().contains(charSeedInShell));
        assertFalse(specimen.getAvailableCharacters().contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));

        new UseDirective().process(context, "/M 2,1");

        assertTrue(specimen.getAvailableCharacters().contains(charSeedInShell));
        assertTrue(specimen.getAvailableCharacters().contains(charAvgThickness));
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
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();

        // Check that the taxon "Oryza" - number 10 - is eliminated when
        // character 38 is given a value
        // of 5. Oryza is listed in the data file as both having a value for
        // character 38 - 0 - and
        // having the inapplicability flag set to true for character 38.

        new UseDirective().process(context, "38,5");

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

    // Set multistate, real integer etc

    // set twice
    // remove twice
    // weird behavior with change ???
    // open two datasets in succession, ensure that keywords etc from first
    // dataset are cleared out
    // Integer above and below maximum
    // Dependent characters like 210, 213 and 230 in grasses
    // Dependent characters like 153,154,155 in salix

    // and, or and range operators

    // invalid input - ensure behaves correctly

}
