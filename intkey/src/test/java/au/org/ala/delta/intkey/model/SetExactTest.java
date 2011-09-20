package au.org.ala.delta.intkey.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Item;

public class SetExactTest extends IntkeyDatasetTestCase {

    @Test
    public void testSetExact() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset dataset = context.getDataset();

        context.setTolerance(1);
        HashSet<Integer> exactCharacterNumbers = new HashSet<Integer>();
        exactCharacterNumbers.add(2);
        context.setExactCharacters(exactCharacterNumbers);

        new UseDirective().parseAndProcess(context, "2,1");

        List<Item> eliminatedTaxa = context.getEliminatedTaxa();
        assertEquals(5, eliminatedTaxa.size());
        assertTrue(eliminatedTaxa.contains(dataset.getTaxon(3)));
        assertTrue(eliminatedTaxa.contains(dataset.getTaxon(4)));
        assertTrue(eliminatedTaxa.contains(dataset.getTaxon(6)));
        assertTrue(eliminatedTaxa.contains(dataset.getTaxon(9)));
        assertTrue(eliminatedTaxa.contains(dataset.getTaxon(12)));
    }

    @Test
    public void testToggleExactOnAndOff() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        context.setTolerance(1);
        HashSet<Integer> exactCharacterNumbers = new HashSet<Integer>();
        exactCharacterNumbers.add(2);
        context.setExactCharacters(exactCharacterNumbers);
        context.setExactCharacters(Collections.EMPTY_SET);
        
        new UseDirective().parseAndProcess(context, "2,1");        
        assertTrue(context.getEliminatedTaxa().isEmpty());
    }
    
}
