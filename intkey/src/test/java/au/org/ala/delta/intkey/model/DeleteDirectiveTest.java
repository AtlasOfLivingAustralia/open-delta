package au.org.ala.delta.intkey.model;

import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DeleteDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Specimen;

public class DeleteDirectiveTest extends IntkeyDatasetTestCase {

    /**
     * Test that if an ancestor (not the direct parent) controlling character
     * has its value deleted, the values of descendant dependent characters are
     * also deleted.
     * 
     * @throws Exception
     */
    @Test
    public void testDependentCharactersRemoved() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        new UseDirective().parseAndProcess(context, "4,1");

        // Delete the ancestor controlling character
        new DeleteDirective().parseAndProcess(context, "2");

        Specimen specimen = context.getSpecimen();

        assertTrue(specimen.getUsedCharacters().isEmpty());
    }

    /**
     * Make a character inapplicable by setting the values of two of its
     * controlling characters. Then delete these values and ensure that the
     * character is no longer inapplicable
     * 
     * @throws Exception
     */
    @Test
    public void testSetInapplicableThenRevert() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charThree = ds.getCharacter(3);

        // State 2 for both characters 1 and 2 will make character 3
        // inapplicable
        context.parseAndExecuteDirective("Use 1,2");
        context.parseAndExecuteDirective("Use 2,2");
        Specimen specimen = context.getSpecimen();
        assertTrue(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertFalse(context.getAvailableCharacters().contains(charThree));

        // Delete value for character 1 - character 3 should still be
        // inapplicable
        context.parseAndExecuteDirective("Delete 1");
        assertTrue(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertFalse(context.getAvailableCharacters().contains(charThree));

        // Delete value for character 1 - character 2 should no longer be
        // inapplicable
        context.parseAndExecuteDirective("Delete 2");

        context.calculateBestOrSeparateCharacters();
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));
    }

    /**
     * Make a character maybe inapplicable by setting the values of two of its
     * controlling characters. Then delete these values and ensure that the
     * character is no longer maybe inapplicable
     * 
     * @throws Exception
     */
    @Test
    public void testSetMaybeInapplicableThenRevert() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charThree = ds.getCharacter(3);

        // State 2 for both characters 1 and 2 will make character 3
        // inapplicable
        context.parseAndExecuteDirective("Use 1,1/2");
        context.parseAndExecuteDirective("Use 2,1/2");
        Specimen specimen = context.getSpecimen();
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertTrue(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));

        // Delete value for character 1 - character 3 should still be
        // inapplicable
        context.parseAndExecuteDirective("Delete 1");
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertTrue(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));

        // Delete value for character 1 - character 2 should no longer be
        // inapplicable
        context.parseAndExecuteDirective("Delete 2");

        context.calculateBestOrSeparateCharacters();
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));
    }

    /**
     * Make a character inapplicable via one controlling character and maybe
     * inapplicable via another. Then delete these values and ensure that the
     * character is no longer inapplicable
     * 
     * @throws Exception
     */
    @Test
    public void testSetInapplicableAndMaybeInapplicableThenRevert() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_multiple_controlling/intkey.ink");
        context.setProcessingDirectivesFile(true);
        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charThree = ds.getCharacter(3);

        // State 2 for both characters 1 and 2 will make character 3
        // inapplicable
        context.parseAndExecuteDirective("Use 1,2");
        context.parseAndExecuteDirective("Use 2,1/2");
        Specimen specimen = context.getSpecimen();
        assertTrue(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertFalse(context.getAvailableCharacters().contains(charThree));

        // Delete value for character 1 - character 3 should still be
        // inapplicable
        context.parseAndExecuteDirective("Delete 1");
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertTrue(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));

        // Delete value for character 1 - character 2 should no longer be
        // inapplicable
        context.parseAndExecuteDirective("Delete 2");

        context.calculateBestOrSeparateCharacters();
        assertFalse(specimen.isCharacterInapplicable(charThree));
        assertFalse(specimen.isCharacterMaybeInapplicable(charThree));
        assertTrue(context.getAvailableCharacters().contains(charThree));
    }

    /**
     * Set a "hierarchy" of characters as inapplicable through their ancestor controlling character. Then
     * delete the value for the controlling character and ensure that the dependent characters are set
     * back to available
     * @throws Exception
     */
    @Test
    public void testInapplicableHierarchy() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charSeedPresence = ds.getCharacter(2);
        au.org.ala.delta.model.Character charSeedInShell = ds.getCharacter(3);
        au.org.ala.delta.model.Character charAvgThickness = ds.getCharacter(4);

        Specimen specimen = context.getSpecimen();

        new UseDirective().parseAndProcess(context, "2,2");

        List<au.org.ala.delta.model.Character> availableCharacters = context.getAvailableCharacters();
        assertFalse(availableCharacters.contains(charSeedInShell));
        assertFalse(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertTrue(specimen.isCharacterInapplicable(charSeedInShell));
        assertTrue(specimen.isCharacterInapplicable(charAvgThickness));
        assertFalse(specimen.isCharacterMaybeInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterMaybeInapplicable(charAvgThickness));
        
        context.parseAndExecuteDirective("Delete 2");
        availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(charSeedInShell));
        assertTrue(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertFalse(specimen.isCharacterInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterInapplicable(charAvgThickness));
        assertFalse(specimen.isCharacterMaybeInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterMaybeInapplicable(charAvgThickness));
    }
    
    /**
     * Set a "hierarchy" of characters as maybe inapplicable through their ancestor controlling character. Then
     * delete the value for the controlling character and ensure that the dependent characters are set
     * back to available
     * @throws Exception
     */
    @Test
    public void testMaybeInapplicableHierarchy() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        au.org.ala.delta.model.Character charSeedPresence = ds.getCharacter(2);
        au.org.ala.delta.model.Character charSeedInShell = ds.getCharacter(3);
        au.org.ala.delta.model.Character charAvgThickness = ds.getCharacter(4);

        Specimen specimen = context.getSpecimen();

        new UseDirective().parseAndProcess(context, "2,1/2");

        List<au.org.ala.delta.model.Character> availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(charSeedInShell));
        assertTrue(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertFalse(specimen.isCharacterInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterInapplicable(charAvgThickness));
        assertTrue(specimen.isCharacterMaybeInapplicable(charSeedInShell));
        assertTrue(specimen.isCharacterMaybeInapplicable(charAvgThickness));
        
        context.parseAndExecuteDirective("Delete 2");
        availableCharacters = context.getAvailableCharacters();
        assertTrue(availableCharacters.contains(charSeedInShell));
        assertTrue(availableCharacters.contains(charAvgThickness));
        assertFalse(specimen.hasValueFor(charSeedInShell));
        assertFalse(specimen.hasValueFor(charAvgThickness));
        assertFalse(specimen.isCharacterInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterInapplicable(charAvgThickness));
        assertFalse(specimen.isCharacterMaybeInapplicable(charSeedInShell));
        assertFalse(specimen.isCharacterMaybeInapplicable(charAvgThickness));
    }

}
