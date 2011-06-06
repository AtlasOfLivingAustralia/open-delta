package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.ChangeDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class ChangeDirectiveTest extends TestCase {

    /**
     * Smoke test for the CHANGE directive. This directive simply delegates to
     * the USE directive so it does not need to be extensively tested.
     * 
     * @throws Exception
     */
    @Test
    public void testChangeDirective() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        IntkeyDataset ds = context.getDataset();
        Specimen specimen = context.getSpecimen();

        UnorderedMultiStateCharacter charLongevity = (UnorderedMultiStateCharacter) ds.getCharacter(2);

        IntkeyDirectiveInvocation invoc = new UseDirective().doProcess(context, "2,1");
        context.executeDirective(invoc);

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

        IntkeyDirectiveInvocation invoc2 = new ChangeDirective().doProcess(context, "2,2");
        context.executeDirective(invoc2);

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

}
