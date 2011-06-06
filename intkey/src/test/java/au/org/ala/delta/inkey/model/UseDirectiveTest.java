package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.lang.math.FloatRange;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.MultiStateValue;
import au.org.ala.delta.intkey.model.specimen.RealValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class UseDirectiveTest extends TestCase {

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

        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "4,1");
        context.executeDirective(invoc);

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

        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "4,1");
        context.executeDirective(invoc);

        IntkeyDirectiveInvocation invoc2 = new UseDirective().doProcess(context, "/M 2,2");
        context.executeDirective(invoc2);

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

        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "1,2");
        context.executeDirective(invoc);

        assertFalse(context.getSpecimen().getAvailableCharacters().contains(charThree));

        IntkeyDirectiveInvocation invoc2 = new UseDirective().doProcess(context, "2,2");
        context.executeDirective(invoc2);

        assertFalse(context.getSpecimen().getAvailableCharacters().contains(charThree));

        IntkeyDirectiveInvocation invoc3 = new UseDirective().doProcess(context, "/M 2,1");
        context.executeDirective(invoc3);

        assertFalse(context.getSpecimen().getAvailableCharacters().contains(charThree));

        IntkeyDirectiveInvocation invoc4 = new UseDirective().doProcess(context, "/M 1,1");
        context.executeDirective(invoc4);

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

        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "4,1");
        context.executeDirective(invoc);

        Specimen specimen = context.getSpecimen();

        IntkeyDirectiveInvocation invoc2 = new UseDirective().doProcess(context, "/M 2,2");
        context.executeDirective(invoc2);

        assertFalse(specimen.getAvailableCharacters().contains(charSeedInShell));
        assertFalse(specimen.getAvailableCharacters().contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));

        IntkeyDirectiveInvocation invoc3 = new UseDirective().doProcess(context, "/M 2,1");
        context.executeDirective(invoc3);

        assertTrue(specimen.getAvailableCharacters().contains(charSeedInShell));
        assertTrue(specimen.getAvailableCharacters().contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
    }

    /**
     * Test that a taxon that has an attribute that has both values specified and 
     * the inapplicability flag set to true in the data file is handled correctly
     * by the USE command. 
     * @throws Exception
     */
    @Test
    public void testAttributeWithValuesAndInapplicabilityFlag() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();
        
        // Check that the taxon "Oryza" - number 10 - is eliminated when character 38 is given a value
        // of 5. Oryza is listed in the data file as both having a value for character 38 - 0 - and 
        // having the inapplicability flag set to true for character 38.
        
        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "38,5");
        context.executeDirective(invoc);
        
        Specimen specimen = context.getSpecimen();
        assertEquals(Arrays.asList(ds.getCharacter(32), ds.getCharacter(38)), specimen.getUsedCharacters());
        
        assertEquals(8, specimen.getTaxonDifferences().size());
        assertTrue(specimen.getTaxonDifferences().containsKey(ds.getTaxon(10)));
    }

}
