package au.org.ala.delta.intkey.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.ChangeDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

/**
 * Unit tests for the CHANGE directive
 * 
 * @author ChrisF
 * 
 */
public class ChangeDirectiveTest extends IntkeyDatasetTestCase {

    /**
     * Smoke test for the CHANGE directive.
     * 
     * @throws Exception
     */
    @Test
    public void testChangeDirective() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        IntkeyDataset ds = context.getDataset();
        Specimen specimen = context.getSpecimen();

        UnorderedMultiStateCharacter charLongevity = (UnorderedMultiStateCharacter) ds.getCharacter(2);

        new UseDirective().parseAndProcess(context, "2,1");

        assertEquals(Arrays.asList(charLongevity), specimen.getUsedCharacters());
        Map<Item, Set<Character>> differingCharacters = specimen.getTaxonDifferences();
        assertEquals(14, differingCharacters.size());
        assertEquals(0, differingCharacters.get(ds.getItem(1)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(2)).size());
        assertEquals(1, differingCharacters.get(ds.getItem(3)).size());
        assertEquals(1, differingCharacters.get(ds.getItem(4)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(5)).size());
        assertEquals(1, differingCharacters.get(ds.getItem(6)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(7)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(8)).size());
        assertEquals(1, differingCharacters.get(ds.getItem(9)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(10)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(11)).size());
        assertEquals(1, differingCharacters.get(ds.getItem(12)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(13)).size());
        assertEquals(0, differingCharacters.get(ds.getItem(14)).size());

        new ChangeDirective().parseAndProcess(context, "2,2");

        assertEquals(Arrays.asList(charLongevity), specimen.getUsedCharacters());

        Map<Item, Set<Character>> differingCharacters2 = specimen.getTaxonDifferences();
        assertEquals(14, differingCharacters2.size());
        assertEquals(0, differingCharacters2.get(ds.getItem(1)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(2)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(3)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(4)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(5)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(6)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(7)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(8)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(9)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(10)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(11)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(12)).size());
        assertEquals(0, differingCharacters2.get(ds.getItem(13)).size());
        assertEquals(1, differingCharacters2.get(ds.getItem(14)).size());
    }

    /**
     * This unit test tests the fix for a bug that was found with updating the
     * specimen after a character value is changed. It was fixed in revision
     * 736.
     * 
     * @throws Exception
     */
    @Test
    public void testChangeDirective2() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        IntkeyDataset ds = context.getDataset();
        Specimen specimen = context.getSpecimen();

        TextCharacter charIncluding = (TextCharacter) ds.getCharacter(1);
        UnorderedMultiStateCharacter charLongevity = (UnorderedMultiStateCharacter) ds.getCharacter(2);
        RealCharacter charCulmsMaxHeight = (RealCharacter) ds.getCharacter(3);
        UnorderedMultiStateCharacter charCulmsWoodyHerbacious = (UnorderedMultiStateCharacter) ds.getCharacter(4);
        UnorderedMultiStateCharacter charCulmsBranchedAbove = (UnorderedMultiStateCharacter) ds.getCharacter(5);
        UnorderedMultiStateCharacter charCulmNodesHairyGlabrous = (UnorderedMultiStateCharacter) ds.getCharacter(6);
        OrderedMultiStateCharacter charLeafBladesShape = (OrderedMultiStateCharacter) ds.getCharacter(7);
        RealCharacter charLeafBladesMidWidth = (RealCharacter) ds.getCharacter(8);
        UnorderedMultiStateCharacter charLeafBladesPseudo = (UnorderedMultiStateCharacter) ds.getCharacter(9);
        UnorderedMultiStateCharacter charLigulePresence = (UnorderedMultiStateCharacter) ds.getCharacter(10);

        new UseDirective().parseAndProcess(context, "1-10,1");

        assertEquals(Arrays.asList(charIncluding, charLongevity, charCulmsMaxHeight, charCulmsWoodyHerbacious, charCulmsBranchedAbove, charCulmNodesHairyGlabrous, charLeafBladesShape,
                charLeafBladesMidWidth, charLeafBladesPseudo, charLigulePresence), specimen.getUsedCharacters());

        Map<Item, Set<Character>> differingCharacters = specimen.getTaxonDifferences();
        assertEquals(14, differingCharacters.size());
        assertEquals(6, differingCharacters.get(ds.getItem(1)).size());
        assertEquals(3, differingCharacters.get(ds.getItem(2)).size());
        assertEquals(8, differingCharacters.get(ds.getItem(3)).size());
        assertEquals(3, differingCharacters.get(ds.getItem(4)).size());
        assertEquals(5, differingCharacters.get(ds.getItem(5)).size());
        assertEquals(6, differingCharacters.get(ds.getItem(6)).size());
        assertEquals(5, differingCharacters.get(ds.getItem(7)).size());
        assertEquals(5, differingCharacters.get(ds.getItem(8)).size());
        assertEquals(7, differingCharacters.get(ds.getItem(9)).size());
        assertEquals(4, differingCharacters.get(ds.getItem(10)).size());
        assertEquals(2, differingCharacters.get(ds.getItem(11)).size());
        assertEquals(7, differingCharacters.get(ds.getItem(12)).size());
        assertEquals(4, differingCharacters.get(ds.getItem(13)).size());
        assertEquals(5, differingCharacters.get(ds.getItem(14)).size());

        new ChangeDirective().parseAndProcess(context, "10,2");

        assertEquals(Arrays.asList(charIncluding, charLongevity, charCulmsMaxHeight, charCulmsWoodyHerbacious, charCulmsBranchedAbove, charCulmNodesHairyGlabrous, charLeafBladesShape,
                charLeafBladesMidWidth, charLeafBladesPseudo, charLigulePresence), specimen.getUsedCharacters());

        Map<Item, Set<Character>> differingCharacters2 = specimen.getTaxonDifferences();
        assertEquals(14, differingCharacters2.size());
        assertEquals(7, differingCharacters2.get(ds.getItem(1)).size());
        assertEquals(4, differingCharacters2.get(ds.getItem(2)).size());
        assertEquals(9, differingCharacters2.get(ds.getItem(3)).size());
        assertEquals(4, differingCharacters2.get(ds.getItem(4)).size());
        assertEquals(6, differingCharacters2.get(ds.getItem(5)).size());
        assertEquals(7, differingCharacters2.get(ds.getItem(6)).size());
        assertEquals(5, differingCharacters2.get(ds.getItem(7)).size());
        assertEquals(6, differingCharacters2.get(ds.getItem(8)).size());
        assertEquals(8, differingCharacters2.get(ds.getItem(9)).size());
        assertEquals(5, differingCharacters2.get(ds.getItem(10)).size());
        assertEquals(3, differingCharacters2.get(ds.getItem(11)).size());
        assertEquals(8, differingCharacters2.get(ds.getItem(12)).size());
        assertEquals(5, differingCharacters2.get(ds.getItem(13)).size());
        assertEquals(6, differingCharacters2.get(ds.getItem(14)).size());
    }

    /**
     * This unit test tests the fix for a bug that was found with updating the
     * specimen after a character value is changed. It was fixed in revision
     * 736.
     * 
     * @throws Exception
     */
    @Test
    public void testChangeDirective3() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        IntkeyDataset ds = context.getDataset();
        Specimen specimen = context.getSpecimen();

        new UseDirective().parseAndProcess(context, "16,1");

        Map<Item, Set<Character>> differingCharacters = specimen.getTaxonDifferences();
        assertEquals(14, differingCharacters.size());
        assertEquals(1, differingCharacters.get(ds.getItem(1)).size());
        assertEquals(0,  differingCharacters.get(ds.getItem(2)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(3)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(4)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(5)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(6)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(7)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(8)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(9)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(10)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(11)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(12)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(13)).size());
        assertEquals(1,  differingCharacters.get(ds.getItem(14)).size());

        new ChangeDirective().parseAndProcess(context, "16,2");

        Map<Item, Set<Character>> differingCharacters2 = specimen.getTaxonDifferences();
        assertEquals(14, differingCharacters2.size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(1)).size());
        assertEquals(1,  differingCharacters2.get(ds.getItem(2)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(3)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(4)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(5)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(6)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(7)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(8)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(9)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(10)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(11)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(12)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(13)).size());
        assertEquals(0,  differingCharacters2.get(ds.getItem(14)).size());
    }

}
