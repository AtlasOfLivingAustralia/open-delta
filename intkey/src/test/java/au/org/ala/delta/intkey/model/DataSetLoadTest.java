package au.org.ala.delta.intkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.math.FloatRange;
import org.junit.Test;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.util.Pair;

/**
 * Unit tests for the loading of dataset information from the items and
 * characters files.
 * 
 * @author ChrisF
 * 
 */
public class DataSetLoadTest extends IntkeyDatasetTestCase {

    /**
     * Test opening the sample dataset by setting the characters file and the
     * items file directly
     */
    @Test
    public void testReadSampleCharactersAndItems() throws Exception {
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");

        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.setFileCharacters(new File(icharsFileUrl.toURI()));
        context.setFileTaxa(new File(iitemsFileUrl.toURI()));

        assertEquals(87, context.getDataset().getNumberOfCharacters());
        assertEquals(14, context.getDataset().getNumberOfTaxa());
    }

    /**
     * Test opening the sample dataset by opening the initialization file that
     * is supplied with it
     */
    @Test
    public void testReadSampleFromInitializationFile() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        List<String> keywordsWithoutSystemDefinedOnes = new ArrayList<String>(context.getCharacterKeywords());
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_ALL);
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_AVAILABLE);
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_USED);
        keywordsWithoutSystemDefinedOnes.remove(IntkeyContext.CHARACTER_KEYWORD_NONE);

        assertEquals(87, context.getDataset().getNumberOfCharacters());
        assertEquals(14, context.getDataset().getNumberOfTaxa());
        assertEquals(36, keywordsWithoutSystemDefinedOnes.size());
    }

    /**
     * Test opening a small dataset with a few controlling characters in it. As
     * this is quite a small dataset is it easy to test that all the character
     * and taxon information has been read correctly
     */
    @Test
    public void testLoadControllingCharsDataset() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        IntkeyDataset ds = context.getDataset();

        assertEquals("Chris' Test Data", ds.getHeading());
        assertEquals("Test transitive dependencies", ds.getSubHeading());

        assertEquals(8, context.getDataset().getNumberOfCharacters());
        assertEquals(5, context.getDataset().getNumberOfTaxa());

        assertEquals("Carrot", ds.getItem(1).getDescription());
        assertEquals("Apricot", ds.getItem(2).getDescription());
        assertEquals("Strawberry", ds.getItem(3).getDescription());
        assertEquals("Plum", ds.getItem(4).getDescription());
        assertEquals("Potato", ds.getItem(5).getDescription());

        RealCharacter charAvgWeight = (RealCharacter) ds.getCharacter(1);
        assertEquals("average weight", charAvgWeight.getDescription());
        assertEquals("kg", charAvgWeight.getUnits());

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        assertEquals("seed presence", charSeedPresence.getDescription());
        assertEquals(2, charSeedPresence.getNumberOfStates());
        assertEquals("present", charSeedPresence.getState(1));
        assertEquals("absent", charSeedPresence.getState(2));

        // check the dependencies for this character
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

        // check the dependencies for this character
        // check the dependencies for this character
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

    /**
     * Test a dataset generated using the NON AUTOMATIC CONTROLLING CHARACTERS
     * confor directive
     * 
     * @throws Exception
     */
    @Test
    public void testNonAutomaticControllingCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_non_auto/intkey.ink");

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

    /**
     * Test a dataset generated using the USE CONTROLLING CHARACTERS FIRST
     * confor directive
     * 
     * @throws Exception
     */
    @Test
    public void testUseControllingCharactersFirst() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_use_first/intkey.ink");

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

    /**
     * Test a dataset generated using the APPLICABLE CHARACTERS confor directive
     * 
     * @throws Exception
     */
    @Test
    public void testApplicableCharactersDirective() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_applicable_directive/intkey.ink");

        IntkeyDataset ds = context.getDataset();

        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        List<CharacterDependency> charSeedDependencies = charSeedPresence.getDependentCharacters();

        assertEquals(1, charSeedDependencies.size());
        CharacterDependency cd1 = charSeedDependencies.get(0);

        assertEquals(2, cd1.getControllingCharacterId());

        assertEquals(2, cd1.getStates().size());
        assertTrue(cd1.getStates().contains(2));
        assertTrue(cd1.getStates().contains(3));

        assertEquals(2, cd1.getDependentCharacterIds().size());
        assertTrue(cd1.getDependentCharacterIds().contains(3));
        assertTrue(cd1.getDependentCharacterIds().contains(5));

        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        List<CharacterDependency> charSeedInShellCharacterDependencies = charSeedInShell.getDependentCharacters();

        assertEquals(1, charSeedInShellCharacterDependencies.size());
        CharacterDependency cd2 = charSeedInShellCharacterDependencies.get(0);

        assertEquals(3, cd2.getControllingCharacterId());

        assertEquals(2, cd2.getStates().size());
        assertTrue(cd2.getStates().contains(2));
        assertTrue(cd2.getStates().contains(3));

        assertEquals(1, cd2.getDependentCharacterIds().size());
        assertTrue(cd2.getDependentCharacterIds().contains(4));
    }

    /**
     * Test reading attributes (character-taxon data). These are loaded
     * on-demand from disk by the IntkeyDataset, as they are too large to hold
     * in memory.
     * 
     * @throws Exception
     */
    @Test
    public void testReadAttributes() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        IntkeyDataset ds = context.getDataset();

        RealCharacter charAvgWeight = (RealCharacter) ds.getCharacter(1);
        UnorderedMultiStateCharacter charSeedPresence = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        UnorderedMultiStateCharacter charSeedInShell = (UnorderedMultiStateCharacter) ds.getCharacter(3);
        RealCharacter charAvgThickness = (RealCharacter) ds.getCharacter(4);
        IntegerCharacter charAvgNumberOfSeeds = (IntegerCharacter) ds.getCharacter(5);
        OrderedMultiStateCharacter charColor = (OrderedMultiStateCharacter) ds.getCharacter(6);
        RealCharacter charAvgLength = (RealCharacter) ds.getCharacter(7);
        TextCharacter charMoreComments = (TextCharacter) ds.getCharacter(8);

        Item itemCarrot = ds.getItem(1);
        Item itemApricot = ds.getItem(2);
        Item itemStrawberry = ds.getItem(3);
        Item itemPlum = ds.getItem(4);
        Item itemPotato = ds.getItem(5);

        List<Attribute> averageWeightAttrs = ds.getAttributesForCharacter(1);
        assertEquals(5, averageWeightAttrs.size());

        for (Attribute attr : averageWeightAttrs) {
            assertTrue(attr instanceof RealAttribute);
        }

        assertEquals(itemCarrot, averageWeightAttrs.get(0).getItem());
        assertEquals(itemApricot, averageWeightAttrs.get(1).getItem());
        assertEquals(itemStrawberry, averageWeightAttrs.get(2).getItem());
        assertEquals(itemPlum, averageWeightAttrs.get(3).getItem());
        assertEquals(itemPotato, averageWeightAttrs.get(4).getItem());

        assertEquals(charAvgWeight, averageWeightAttrs.get(0).getCharacter());
        assertEquals(charAvgWeight, averageWeightAttrs.get(1).getCharacter());
        assertEquals(charAvgWeight, averageWeightAttrs.get(2).getCharacter());
        assertEquals(charAvgWeight, averageWeightAttrs.get(3).getCharacter());
        assertEquals(charAvgWeight, averageWeightAttrs.get(4).getCharacter());

        assertEquals(new FloatRange(50.0, 50.0), ((RealAttribute) averageWeightAttrs.get(0)).getPresentRange());
        assertEquals(new FloatRange(10.0, 20.0), ((RealAttribute) averageWeightAttrs.get(1)).getPresentRange());
        assertEquals(new FloatRange(5.0, 10.0), ((RealAttribute) averageWeightAttrs.get(2)).getPresentRange());
        assertEquals(new FloatRange(8.5, 15.0), ((RealAttribute) averageWeightAttrs.get(3)).getPresentRange());
        assertEquals(new FloatRange(100.0, 200.0), ((RealAttribute) averageWeightAttrs.get(4)).getPresentRange());

        List<Attribute> seedPresenceAttrs = ds.getAttributesForCharacter(2);
        assertEquals(5, seedPresenceAttrs.size());

        for (Attribute attr : seedPresenceAttrs) {
            assertTrue(attr instanceof MultiStateAttribute);
        }

        assertEquals(itemCarrot, seedPresenceAttrs.get(0).getItem());
        assertEquals(itemApricot, seedPresenceAttrs.get(1).getItem());
        assertEquals(itemStrawberry, seedPresenceAttrs.get(2).getItem());
        assertEquals(itemPlum, seedPresenceAttrs.get(3).getItem());
        assertEquals(itemPotato, seedPresenceAttrs.get(4).getItem());

        assertEquals(charSeedPresence, seedPresenceAttrs.get(0).getCharacter());
        assertEquals(charSeedPresence, seedPresenceAttrs.get(1).getCharacter());
        assertEquals(charSeedPresence, seedPresenceAttrs.get(2).getCharacter());
        assertEquals(charSeedPresence, seedPresenceAttrs.get(3).getCharacter());
        assertEquals(charSeedPresence, seedPresenceAttrs.get(4).getCharacter());

        assertEquals(new HashSet<Integer>(Arrays.asList(2)), ((MultiStateAttribute) seedPresenceAttrs.get(0)).getPresentStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(1)), ((MultiStateAttribute) seedPresenceAttrs.get(1)).getPresentStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(1)), ((MultiStateAttribute) seedPresenceAttrs.get(2)).getPresentStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(1)), ((MultiStateAttribute) seedPresenceAttrs.get(3)).getPresentStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(2)), ((MultiStateAttribute) seedPresenceAttrs.get(4)).getPresentStates());

        List<Attribute> seedInShellAttrs = ds.getAttributesForCharacter(3);
        assertEquals(5, seedInShellAttrs.size());

        for (Attribute attr : seedInShellAttrs) {
            assertTrue(attr instanceof MultiStateAttribute);
        }

        assertEquals(itemCarrot, seedInShellAttrs.get(0).getItem());
        assertEquals(itemApricot, seedInShellAttrs.get(1).getItem());
        assertEquals(itemStrawberry, seedInShellAttrs.get(2).getItem());
        assertEquals(itemPlum, seedInShellAttrs.get(3).getItem());
        assertEquals(itemPotato, seedInShellAttrs.get(4).getItem());

        assertEquals(charSeedInShell, seedInShellAttrs.get(0).getCharacter());
        assertEquals(charSeedInShell, seedInShellAttrs.get(1).getCharacter());
        assertEquals(charSeedInShell, seedInShellAttrs.get(2).getCharacter());
        assertEquals(charSeedInShell, seedInShellAttrs.get(3).getCharacter());
        assertEquals(charSeedInShell, seedInShellAttrs.get(4).getCharacter());

        assertTrue(seedInShellAttrs.get(0).isInapplicable());
        assertEquals(new HashSet<Integer>(Arrays.asList(1)), ((MultiStateAttribute) seedInShellAttrs.get(1)).getPresentStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(2)), ((MultiStateAttribute) seedInShellAttrs.get(2)).getPresentStates());
        assertEquals(new HashSet<Integer>(Arrays.asList(1)), ((MultiStateAttribute) seedInShellAttrs.get(3)).getPresentStates());
        assertTrue(seedInShellAttrs.get(4).isInapplicable());

        List<Attribute> avgThicknessAttrs = ds.getAttributesForCharacter(4);
        assertEquals(5, avgThicknessAttrs.size());

        for (Attribute attr : avgThicknessAttrs) {
            assertTrue(attr instanceof RealAttribute);
        }

        assertEquals(itemCarrot, avgThicknessAttrs.get(0).getItem());
        assertEquals(itemApricot, avgThicknessAttrs.get(1).getItem());
        assertEquals(itemStrawberry, avgThicknessAttrs.get(2).getItem());
        assertEquals(itemPlum, avgThicknessAttrs.get(3).getItem());
        assertEquals(itemPotato, avgThicknessAttrs.get(4).getItem());

        assertEquals(charAvgThickness, avgThicknessAttrs.get(0).getCharacter());
        assertEquals(charAvgThickness, avgThicknessAttrs.get(1).getCharacter());
        assertEquals(charAvgThickness, avgThicknessAttrs.get(2).getCharacter());
        assertEquals(charAvgThickness, avgThicknessAttrs.get(3).getCharacter());
        assertEquals(charAvgThickness, avgThicknessAttrs.get(4).getCharacter());

        assertTrue(avgThicknessAttrs.get(0).isInapplicable());
        assertEquals(new FloatRange(3, 3), ((RealAttribute) avgThicknessAttrs.get(1)).getPresentRange());
        assertTrue(avgThicknessAttrs.get(2).isInapplicable());
        assertEquals(new FloatRange(5, 5), ((RealAttribute) avgThicknessAttrs.get(3)).getPresentRange());
        assertTrue(avgThicknessAttrs.get(4).isInapplicable());

        List<Attribute> avgNumSeedsAttrs = ds.getAttributesForCharacter(5);
        assertEquals(5, avgNumSeedsAttrs.size());

        for (Attribute attr : avgNumSeedsAttrs) {
            assertTrue(attr instanceof IntegerAttribute);
        }

        assertEquals(itemCarrot, avgNumSeedsAttrs.get(0).getItem());
        assertEquals(itemApricot, avgNumSeedsAttrs.get(1).getItem());
        assertEquals(itemStrawberry, avgNumSeedsAttrs.get(2).getItem());
        assertEquals(itemPlum, avgNumSeedsAttrs.get(3).getItem());
        assertEquals(itemPotato, avgNumSeedsAttrs.get(4).getItem());

        assertEquals(charAvgNumberOfSeeds, avgNumSeedsAttrs.get(0).getCharacter());
        assertEquals(charAvgNumberOfSeeds, avgNumSeedsAttrs.get(1).getCharacter());
        assertEquals(charAvgNumberOfSeeds, avgNumSeedsAttrs.get(2).getCharacter());
        assertEquals(charAvgNumberOfSeeds, avgNumSeedsAttrs.get(3).getCharacter());
        assertEquals(charAvgNumberOfSeeds, avgNumSeedsAttrs.get(4).getCharacter());

        assertTrue(avgNumSeedsAttrs.get(0).isInapplicable());
        assertEquals(new HashSet(Arrays.asList(1)), ((IntegerAttribute) avgNumSeedsAttrs.get(1)).getPresentValues());
        assertEquals(new HashSet(Arrays.asList(50)), ((IntegerAttribute) avgNumSeedsAttrs.get(2)).getPresentValues());
        assertEquals(new HashSet(Arrays.asList(1)), ((IntegerAttribute) avgNumSeedsAttrs.get(3)).getPresentValues());
        assertTrue(avgNumSeedsAttrs.get(4).isInapplicable());

        List<Attribute> colorAttrs = ds.getAttributesForCharacter(6);
        assertEquals(5, colorAttrs.size());

        for (Attribute attr : colorAttrs) {
            assertTrue(attr instanceof MultiStateAttribute);
            assertEquals(charColor, attr.getCharacter());
        }

        assertEquals(itemCarrot, colorAttrs.get(0).getItem());
        assertEquals(itemApricot, colorAttrs.get(1).getItem());
        assertEquals(itemStrawberry, colorAttrs.get(2).getItem());
        assertEquals(itemPlum, colorAttrs.get(3).getItem());
        assertEquals(itemPotato, colorAttrs.get(4).getItem());

        assertEquals(new HashSet(Arrays.asList(3)), ((MultiStateAttribute) colorAttrs.get(0)).getPresentStates());
        assertEquals(new HashSet(Arrays.asList(3)), ((MultiStateAttribute) colorAttrs.get(1)).getPresentStates());
        assertEquals(new HashSet(Arrays.asList(2)), ((MultiStateAttribute) colorAttrs.get(2)).getPresentStates());
        assertEquals(new HashSet(Arrays.asList(1)), ((MultiStateAttribute) colorAttrs.get(3)).getPresentStates());
        assertEquals(new HashSet(Arrays.asList(4)), ((MultiStateAttribute) colorAttrs.get(4)).getPresentStates());

        List<Attribute> averageLengthAttrs = ds.getAttributesForCharacter(7);
        assertEquals(5, averageLengthAttrs.size());

        for (Attribute attr : averageLengthAttrs) {
            assertTrue(attr instanceof RealAttribute);
            assertEquals(charAvgLength, attr.getCharacter());
        }

        assertEquals(itemCarrot, averageLengthAttrs.get(0).getItem());
        assertEquals(itemApricot, averageLengthAttrs.get(1).getItem());
        assertEquals(itemStrawberry, averageLengthAttrs.get(2).getItem());
        assertEquals(itemPlum, averageLengthAttrs.get(3).getItem());
        assertEquals(itemPotato, averageLengthAttrs.get(4).getItem());

        assertEquals(new FloatRange(20.0, 20.0), ((RealAttribute) averageLengthAttrs.get(0)).getPresentRange());
        assertEquals(new FloatRange(5.0, 10.0), ((RealAttribute) averageLengthAttrs.get(1)).getPresentRange());
        assertEquals(new FloatRange(3.0, 6.0), ((RealAttribute) averageLengthAttrs.get(2)).getPresentRange());
        assertEquals(new FloatRange(6.0, 10.0), ((RealAttribute) averageLengthAttrs.get(3)).getPresentRange());
        assertEquals(new FloatRange(10.0, 20.0), ((RealAttribute) averageLengthAttrs.get(4)).getPresentRange());

        List<Attribute> moreCommentsAttrs = ds.getAttributesForCharacter(8);
        assertEquals(5, moreCommentsAttrs.size());

        for (Attribute attr : moreCommentsAttrs) {
            assertTrue(attr instanceof TextAttribute);
            assertEquals(charMoreComments, attr.getCharacter());
        }

        assertEquals(itemCarrot, moreCommentsAttrs.get(0).getItem());
        assertEquals(itemApricot, moreCommentsAttrs.get(1).getItem());
        assertEquals(itemStrawberry, moreCommentsAttrs.get(2).getItem());
        assertEquals(itemPlum, moreCommentsAttrs.get(3).getItem());
        assertEquals(itemPotato, moreCommentsAttrs.get(4).getItem());

        assertEquals("<carrot>", ((TextAttribute) moreCommentsAttrs.get(0)).getText());
        assertEquals("<apricot>", ((TextAttribute) moreCommentsAttrs.get(1)).getText());
        assertEquals("<strawberry>", ((TextAttribute) moreCommentsAttrs.get(2)).getText());
        assertEquals("<plum>", ((TextAttribute) moreCommentsAttrs.get(3)).getText());
        assertEquals("<potato>", ((TextAttribute) moreCommentsAttrs.get(4)).getText());
    }

    /**
     * Second test for reading attributes. Test reading of integer ranges, and
     * multistates with multiple valid states.
     * 
     * @throws Exception
     */
    @Test
    public void testReadAttributes2() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        IntkeyDataset ds = context.getDataset();

        Character charLongevityPlants = ds.getCharacter(2);
        Character charUpperGlumeMidZoneNerveNo = ds.getCharacter(38);
        Character charNumberOfSpecies = ds.getCharacter(85);
        Character charFloristicKingdoms = ds.getCharacter(86);

        MultiStateAttribute attrLongevityPlants = (MultiStateAttribute) ds.getAttribute(7, 2);
        IntegerAttribute attrUpperGlumeMidZoneNerveNo = (IntegerAttribute) ds.getAttribute(8, 38);
        MultiStateAttribute attrFloristicKingdoms = (MultiStateAttribute) ds.getAttribute(7, 86);

        assertEquals(new HashSet(Arrays.asList(1, 2)), attrLongevityPlants.getPresentStates());
        assertEquals(new HashSet(Arrays.asList(3, 4, 5, 6, 7)), attrUpperGlumeMidZoneNerveNo.getPresentValues());
        assertEquals(new HashSet(Arrays.asList(1, 2, 3, 5)), attrFloristicKingdoms.getPresentStates());
    }

    /**
     * Test reading two datasets in succession. Ensure that number of
     * characters, keywords etc is correct after reading the second dataset.
     * 
     * @throws Exception
     */
    @Test
    public void testReadTwoDatasets() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()));

        // The dataset is loaded on a separate thread so we need to wait until
        // it is loaded.
        while (true) {
            Thread.sleep(250);
            if (context.getDataset() != null) {
                break;
            }
        }

        IntkeyDataset ds = context.getDataset();

        assertEquals(87, ds.getNumberOfCharacters());
        assertEquals(14, ds.getNumberOfTaxa());
        assertEquals(39, context.getCharacterKeywords().size());

        initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        context.newDataSetFile(new File(initFileUrl.toURI()));

        // The dataset is loaded on a separate thread so we need to wait until
        // it is loaded.
        while (true) {
            Thread.sleep(250);
            if (context.getDataset() != null) {
                break;
            }
        }

        ds = context.getDataset();

        assertEquals(8, ds.getNumberOfCharacters());
        assertEquals(5, ds.getNumberOfTaxa());
        assertEquals(3, context.getCharacterKeywords().size());
    }

    /**
     * Test that synonymy character information is correctly read from the
     * dataset.
     */
    @Test
    public void testReadSynonymyCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        IntkeyDataset ds = context.getDataset();

        for (Character ch : context.getDataset().getCharactersAsList()) {
            if (ch instanceof RealCharacter) {
                if (((RealCharacter) ch).isIntegerRepresentedAsReal()) {
                    System.out.println(ch.getCharacterId());
                }
            }
        }

        List<TextCharacter> synonymyCharacters = ds.getSynonymyCharacters();

        assertEquals(1, synonymyCharacters.size());

        Character synonymyCharacter = synonymyCharacters.get(0);
        assertEquals(1, synonymyCharacter.getCharacterId());

    }

    @Test
    public void testParseFileOverlayData() {
        List<Pair<String, String>> strPairList = IntkeyDatasetFileReader.parseFileData("cyperaggregat6.jpg <@subject Specimen> <AQ671479> cyperaggregat7.jpg <@subject Inflorescence>");
        assertEquals(2, strPairList.size());

        Pair<String, String> firstPair = strPairList.get(0);
        assertEquals("cyperaggregat6.jpg", firstPair.getFirst());
        assertEquals("<@subject Specimen> <AQ671479>", firstPair.getSecond());

        Pair<String, String> secondPair = strPairList.get(1);
        assertEquals("cyperaggregat7.jpg", secondPair.getFirst());
        assertEquals("<@subject Inflorescence>", secondPair.getSecond());
    }

}
