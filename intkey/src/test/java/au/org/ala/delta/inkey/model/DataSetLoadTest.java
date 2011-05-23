package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class DataSetLoadTest extends TestCase {
    
    
    /**
     * Test opening the sample dataset by 
     * setting the characters file and the items file directly 
     */
    @Test
    public void testReadSampleCharactersAndItems() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");

        IntkeyContext context = new IntkeyContext(null);
        context.setFileCharacters(new File(icharsFileUrl.toURI()).getAbsolutePath());
        context.setFileTaxa(new File(iitemsFileUrl.toURI()).getAbsolutePath());
        
        assertEquals(87, context.getDataset().getNumberOfCharacters());
        assertEquals(14, context.getDataset().getNumberOfTaxa());
    }
    
    /**
     * Test opening the sample dataset by opening the initialization file
     * that is supplied with it
     */
    @Test
    public void testReadSampleFromInitializationFile() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        List<String> keywordsWithoutSystemDefinedOnes = new ArrayList<String>(context.getCharacterKeywords());
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_ALL);
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_AVAILABLE);
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_USED);
        
        assertEquals(87, context.getDataset().getNumberOfCharacters());
        assertEquals(14, context.getDataset().getNumberOfTaxa());
        assertEquals(36, keywordsWithoutSystemDefinedOnes.size());
    }
    
    /**
     * Test opening a small dataset with a few controlling characters in it.
     * As this is quite a small dataset is it easy to test that all the character and taxon
     * information has been read correctly
     */
    @Test
    public void testLoadControllingCharsDataset() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        IntkeyDataset ds = context.getDataset();
        
        assertEquals("Chris' Test Data", ds.getHeading());
        assertEquals("Test transitive dependencies", ds.getSubHeading());
        
        assertEquals(8, context.getDataset().getNumberOfCharacters());
        assertEquals(5, context.getDataset().getNumberOfTaxa());
        
        assertEquals("Carrot", ds.getTaxon(1).getDescription());
        assertEquals("Apricot", ds.getTaxon(2).getDescription());
        assertEquals("Strawberry", ds.getTaxon(3).getDescription());
        assertEquals("Plum", ds.getTaxon(4).getDescription());
        assertEquals("Potato", ds.getTaxon(5).getDescription());
        
        RealCharacter charAvgWeight = (RealCharacter) ds.getCharacter(1);
        assertEquals("average weight", charAvgWeight.getDescription());
        assertEquals("kg", charAvgWeight.getUnits());
        
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        assertEquals("seed presence", charSeedPresence.getDescription());
        assertEquals(2, charSeedPresence.getNumberOfStates());
        assertEquals("present", charSeedPresence.getState(1));
        assertEquals("absent", charSeedPresence.getState(2));
        
        //check the dependencies for this character
        List<CharacterDependency> charSeedPresenceDependencies = charSeedPresence.getDependentCharacters();
        assertEquals(1, charSeedPresenceDependencies.size());
        CharacterDependency cd1 = charSeedPresenceDependencies.get(0);
        assertEquals(2, cd1.getControllingCharacterId());
        assertEquals(1, cd1.getStates().size());
        assertTrue(cd1.getStates().contains(2));
        assertEquals(2, cd1.getDependentCharacterIds().size());
        assertTrue(cd1.getDependentCharacterIds().contains(3));
        assertTrue(cd1.getDependentCharacterIds().contains(5));
        
        
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        assertEquals("seed in shell", charSeedInShell.getDescription());
        assertEquals(2, charSeedInShell.getNumberOfStates());
        assertEquals("in shell", charSeedInShell.getState(1));
        assertEquals("not in shell", charSeedInShell.getState(2));
        
        //check the dependencies for this character
        //check the dependencies for this character
        List<CharacterDependency> charSeedInShellDependencies = charSeedInShell.getDependentCharacters();
        assertEquals(1, charSeedInShellDependencies.size());
        CharacterDependency cd2 = charSeedInShellDependencies.get(0);
        assertEquals(3, cd2.getControllingCharacterId());
        assertEquals(1, cd2.getStates().size());
        assertTrue(cd2.getStates().contains(2));
        assertEquals(1, cd2.getDependentCharacterIds().size());
        assertTrue(cd2.getDependentCharacterIds().contains(4));
        
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);
        assertEquals("average thickness of seed shell", charAvgThickness.getDescription());
        assertEquals("mm thick", charAvgThickness.getUnits());
        
        IntegerCharacter charAvgNumberOfSeeds = (IntegerCharacter) ds.getCharacter(5);
        assertEquals("average number of seeds", charAvgNumberOfSeeds.getDescription());
        assertEquals(null, charAvgNumberOfSeeds.getUnits());
        
        OrderedMultiStateCharacter charColor = (OrderedMultiStateCharacter) ds.getCharacter(6);
        assertEquals(5, charColor.getNumberOfStates());
        assertEquals("purple", charColor.getState(1));
        assertEquals("red", charColor.getState(2));
        assertEquals("orange", charColor.getState(3));
        assertEquals("yellow", charColor.getState(4));
        assertEquals("green", charColor.getState(5));
        
        RealCharacter charAvgLength = (RealCharacter) ds.getCharacter(7);
        assertEquals("average length", charAvgLength.getDescription());
        assertEquals("cm long", charAvgLength.getUnits());
        
        TextCharacter charMoreComments = (TextCharacter) ds.getCharacter(8);
        assertEquals("more comments", charMoreComments.getDescription());
    }
    
    @Test
    public void testNonAutomaticControllingCharacters() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_non_auto/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        IntkeyDataset ds = context.getDataset();
        
        RealCharacter charAvgWeight = (RealCharacter) ds.getCharacter(1);
        assertFalse(charAvgWeight.getNonAutoCc());
        
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        assertTrue(charSeedPresence.getNonAutoCc());
        
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        assertTrue(charSeedInShell.getNonAutoCc());
        
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);
        assertFalse(charAvgThickness.getNonAutoCc());
        
        IntegerCharacter charAvgNumberOfSeeds = (IntegerCharacter) ds.getCharacter(5);
        assertFalse(charAvgNumberOfSeeds.getNonAutoCc());
        
        OrderedMultiStateCharacter charColor = (OrderedMultiStateCharacter) ds.getCharacter(6);
        assertFalse(charColor.getNonAutoCc());
        
        RealCharacter charAvgLength = (RealCharacter) ds.getCharacter(7);
        assertFalse(charAvgLength.getNonAutoCc());
        
        TextCharacter charMoreComments = (TextCharacter) ds.getCharacter(8);
        assertFalse(charMoreComments.getNonAutoCc());
    }
    
    @Test
    public void testUseControllingCharactersFirst() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_use_first/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        IntkeyDataset ds = context.getDataset();
        
        RealCharacter charAvgWeight = (RealCharacter) ds.getCharacter(1);
        assertFalse(charAvgWeight.getUseCc());
        
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        assertFalse(charSeedPresence.getUseCc());
        
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        assertTrue(charSeedInShell.getUseCc());
        
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);
        assertTrue(charAvgThickness.getUseCc());
        
        IntegerCharacter charAvgNumberOfSeeds = (IntegerCharacter) ds.getCharacter(5);
        assertTrue(charAvgNumberOfSeeds.getUseCc());
        
        OrderedMultiStateCharacter charColor = (OrderedMultiStateCharacter) ds.getCharacter(6);
        assertFalse(charColor.getUseCc());
        
        RealCharacter charAvgLength = (RealCharacter) ds.getCharacter(7);
        assertFalse(charAvgLength.getUseCc());
        
        TextCharacter charMoreComments = (TextCharacter) ds.getCharacter(8);
        assertFalse(charMoreComments.getUseCc());
    }
    
    @Test
    public void testApplicableCharactersDirective() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_applicable_directive/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        
        IntkeyDataset ds = context.getDataset();
        
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        List<CharacterDependency> charSeedDependencies = charSeedPresence.getDependentCharacters();

        assertEquals(2, charSeedDependencies.size());
        CharacterDependency cd1 = charSeedDependencies.get(0);
        CharacterDependency cd2 = charSeedDependencies.get(1);
        
        assertEquals(1, cd1.getStates().size());
        assertTrue(cd1.getStates().contains(2));
        
        assertEquals(1, cd2.getStates().size());
        assertTrue(cd2.getStates().contains(3));
        
        assertEquals(2, cd1.getDependentCharacterIds().size());
        assertTrue(cd1.getDependentCharacterIds().contains(3));
        assertTrue(cd1.getDependentCharacterIds().contains(5)); 
        
        assertEquals(2, cd2.getDependentCharacterIds().size());
        assertTrue(cd2.getDependentCharacterIds().contains(3));
        assertTrue(cd2.getDependentCharacterIds().contains(5));    
        
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        List<CharacterDependency> charSeedInShellCharacterDependencies = charSeedInShell.getDependentCharacters();
        
        assertEquals(2, charSeedInShellCharacterDependencies.size());
        CharacterDependency cd3 = charSeedInShellCharacterDependencies.get(0);      
        CharacterDependency cd4 = charSeedInShellCharacterDependencies.get(1); 
        
        assertEquals(1, cd3.getStates().size());
        assertTrue(cd3.getStates().contains(2));
        
        assertEquals(1, cd4.getStates().size());
        assertTrue(cd4.getStates().contains(3));
        
        assertEquals(1, cd3.getDependentCharacterIds().size());
        assertTrue(cd3.getDependentCharacterIds().contains(4));
        
        assertEquals(1, cd4.getDependentCharacterIds().size());
        assertTrue(cd4.getDependentCharacterIds().contains(4));
        
    }
    

}
