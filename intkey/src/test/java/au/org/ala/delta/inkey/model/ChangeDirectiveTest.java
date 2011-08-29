package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.ChangeDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
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
        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences.size());
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(5)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(6)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(7)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(8)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(9)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(10)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(11)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(12)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(13)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(14)));

        new ChangeDirective().parseAndProcess(context, "2,2");

        assertEquals(Arrays.asList(charLongevity), specimen.getUsedCharacters());

        Map<Item, Integer> taxonDifferences2 = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences2.size());
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(1)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(2)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(3)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(4)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(5)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(6)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(7)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(8)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(9)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(10)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(11)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(12)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(13)));
        assertEquals(1, (int) taxonDifferences2.get(ds.getTaxon(14)));
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

        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences.size());
        assertEquals(6, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(3, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(8, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(3, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(5, (int) taxonDifferences.get(ds.getTaxon(5)));
        assertEquals(6, (int) taxonDifferences.get(ds.getTaxon(6)));
        assertEquals(5, (int) taxonDifferences.get(ds.getTaxon(7)));
        assertEquals(5, (int) taxonDifferences.get(ds.getTaxon(8)));
        assertEquals(7, (int) taxonDifferences.get(ds.getTaxon(9)));
        assertEquals(4, (int) taxonDifferences.get(ds.getTaxon(10)));
        assertEquals(2, (int) taxonDifferences.get(ds.getTaxon(11)));
        assertEquals(7, (int) taxonDifferences.get(ds.getTaxon(12)));
        assertEquals(4, (int) taxonDifferences.get(ds.getTaxon(13)));
        assertEquals(5, (int) taxonDifferences.get(ds.getTaxon(14)));

        new ChangeDirective().parseAndProcess(context, "10,2");

        assertEquals(Arrays.asList(charIncluding, charLongevity, charCulmsMaxHeight, charCulmsWoodyHerbacious, charCulmsBranchedAbove, charCulmNodesHairyGlabrous, charLeafBladesShape,
                charLeafBladesMidWidth, charLeafBladesPseudo, charLigulePresence), specimen.getUsedCharacters());

        Map<Item, Integer> taxonDifferences2 = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences2.size());
        assertEquals(7, (int) taxonDifferences2.get(ds.getTaxon(1)));
        assertEquals(4, (int) taxonDifferences2.get(ds.getTaxon(2)));
        assertEquals(9, (int) taxonDifferences2.get(ds.getTaxon(3)));
        assertEquals(4, (int) taxonDifferences2.get(ds.getTaxon(4)));
        assertEquals(6, (int) taxonDifferences2.get(ds.getTaxon(5)));
        assertEquals(7, (int) taxonDifferences2.get(ds.getTaxon(6)));
        assertEquals(5, (int) taxonDifferences2.get(ds.getTaxon(7)));
        assertEquals(6, (int) taxonDifferences2.get(ds.getTaxon(8)));
        assertEquals(8, (int) taxonDifferences2.get(ds.getTaxon(9)));
        assertEquals(5, (int) taxonDifferences2.get(ds.getTaxon(10)));
        assertEquals(3, (int) taxonDifferences2.get(ds.getTaxon(11)));
        assertEquals(8, (int) taxonDifferences2.get(ds.getTaxon(12)));
        assertEquals(5, (int) taxonDifferences2.get(ds.getTaxon(13)));
        assertEquals(6, (int) taxonDifferences2.get(ds.getTaxon(14)));
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

        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences.size());
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(1)));
        assertEquals(0, (int) taxonDifferences.get(ds.getTaxon(2)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(3)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(4)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(5)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(6)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(7)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(8)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(9)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(10)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(11)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(12)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(13)));
        assertEquals(1, (int) taxonDifferences.get(ds.getTaxon(14)));

        new ChangeDirective().parseAndProcess(context, "16,2");

        Map<Item, Integer> taxonDifferences2 = specimen.getTaxonDifferences();
        assertEquals(14, taxonDifferences2.size());
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(1)));
        assertEquals(1, (int) taxonDifferences2.get(ds.getTaxon(2)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(3)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(4)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(5)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(6)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(7)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(8)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(9)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(10)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(11)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(12)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(13)));
        assertEquals(0, (int) taxonDifferences2.get(ds.getTaxon(14)));
    }

}
