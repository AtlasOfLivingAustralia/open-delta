package au.org.ala.delta.intkey.model;

import java.util.Arrays;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DefineNamesDirective;
import au.org.ala.delta.model.Item;

public class DefineNamesDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testCommaSeparated() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getItem(1);
        Item taxonApricot = ds.getItem(2);

        new DefineNamesDirective().parseAndProcess(context, "foo Carrot,Apricot");

        assertEquals(Arrays.asList(taxonCarrot, taxonApricot), context.getTaxaForKeyword("foo"));
    }

    @Test
    public void testNewLineSeparated() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getItem(1);
        Item taxonApricot = ds.getItem(2);

        new DefineNamesDirective().parseAndProcess(context, "foo\nCarrot\nApricot");

        assertEquals(Arrays.asList(taxonCarrot, taxonApricot), context.getTaxaForKeyword("foo"));
    }

    @Test
    public void testCommaSeparatedAndKeywordQuoted() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getItem(1);
        Item taxonApricot = ds.getItem(2);

        new DefineNamesDirective().parseAndProcess(context, "\"foo and bar\" Carrot,Apricot");

        assertEquals(Arrays.asList(taxonCarrot, taxonApricot), context.getTaxaForKeyword("foo and bar"));
    }

    @Test
    public void testNewLineSeparatedAndKeywordQuoted() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getItem(1);
        Item taxonApricot = ds.getItem(2);

        new DefineNamesDirective().parseAndProcess(context, "\"foo and bar\"\nCarrot\nApricot");

        assertEquals(Arrays.asList(taxonCarrot, taxonApricot), context.getTaxaForKeyword("foo and bar"));
    }

}
