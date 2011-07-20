package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DifferencesDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;

//TODO make these tests check against the present of characters rather than just the number of differences
public class DifferencesDirectiveTest extends TestCase {

    public static final boolean DEFAULT_MATCH_INAPPLICABLES = true;
    public static final boolean DEFAULT_MATCH_UNKNOWNS = true;
    public static final MatchType DEFAULT_MATCH_TYPE = MatchType.OVERLAP;

    /**
     * Smoke test for the differences directive
     * 
     * @throws Exception
     */
    @Test
    public void testDifferencesSmokeTest() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        new DifferencesDirective().parseAndProcess(context, "(all) all");
    }

    @Test
    public void testDifferencesResults() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getTaxon(1);
        Item taxonApricot = ds.getTaxon(2);
        Item taxonPlum = ds.getTaxon(4);

        List<Item> taxa = new ArrayList<Item>();
        taxa.add(taxonCarrot);
        taxa.add(taxonApricot);
        taxa.add(taxonPlum);

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), taxa, null, DEFAULT_MATCH_UNKNOWNS, DEFAULT_MATCH_INAPPLICABLES,
                DEFAULT_MATCH_TYPE, false);

        assertEquals(6, differingChars.size());
        assertTrue(differingChars.contains(ds.getCharacter(1)));
        assertTrue(differingChars.contains(ds.getCharacter(2)));
        assertTrue(differingChars.contains(ds.getCharacter(4)));
        assertTrue(differingChars.contains(ds.getCharacter(6)));
        assertTrue(differingChars.contains(ds.getCharacter(7)));
        assertTrue(differingChars.contains(ds.getCharacter(8)));
    }

    @Test
    public void testOmitTextCharacters() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getTaxon(1);
        Item taxonApricot = ds.getTaxon(2);
        Item taxonPlum = ds.getTaxon(4);

        List<Item> taxa = new ArrayList<Item>();
        taxa.add(taxonCarrot);
        taxa.add(taxonApricot);
        taxa.add(taxonPlum);

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), taxa, null, DEFAULT_MATCH_UNKNOWNS, DEFAULT_MATCH_INAPPLICABLES,
                DEFAULT_MATCH_TYPE, true);

        assertEquals(5, differingChars.size());
        assertTrue(differingChars.contains(ds.getCharacter(1)));
        assertTrue(differingChars.contains(ds.getCharacter(2)));
        assertTrue(differingChars.contains(ds.getCharacter(4)));
        assertTrue(differingChars.contains(ds.getCharacter(6)));
        assertTrue(differingChars.contains(ds.getCharacter(7)));
    }

    @Test
    public void testIncludeSpecimen() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        IntegerCharacter chUpperGlumeNerveNumber = (IntegerCharacter) ds.getCharacter(38);

        new UseDirective().parseAndProcess(context, "38,3");

        Specimen specimen = context.getSpecimen();
        List<Item> remainingTaxa = context.getTaxaForKeyword(IntkeyContext.TAXON_KEYWORD_REMAINING);

        // note that the differences directive automatically sets match
        // inapplicables and unknown to false if the match type is exact.
        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), remainingTaxa, specimen, false, false, MatchType.EXACT, false);

        assertEquals(1, differingChars.size());
        assertTrue(differingChars.contains(chUpperGlumeNerveNumber));
    }

    @Test
    public void testDefaultMatchValues() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), ds.getTaxa(), null, DEFAULT_MATCH_UNKNOWNS, DEFAULT_MATCH_INAPPLICABLES, MatchType.OVERLAP, false);

        assertEquals(71, differingChars.size());
    }
    
    @Test
    public void testExact() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        // note that the differences directive automatically sets match
        // inapplicables and unknown to false if the match type is exact.
        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), ds.getTaxa(), null, false, false, MatchType.EXACT, false);

        assertEquals(85, differingChars.size());
    }

    //TODO fix failing unit test!
    /*
    @Test
    public void testSubset() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), ds.getTaxa(), null, false,
                false, MatchType.SUBSET, false);

        assertEquals(82, differingChars.size());
    }*/

    @Test
    public void testDontMatchUnknowns() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), ds.getTaxa(), null, false,
                DEFAULT_MATCH_INAPPLICABLES, MatchType.OVERLAP, false);

        assertEquals(80, differingChars.size());
    }

    @Test
    public void testDontMatchInapplicables() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharacters(), ds.getTaxa(), null, DEFAULT_MATCH_UNKNOWNS,
                false, MatchType.OVERLAP, false);

        assertEquals(81, differingChars.size());
    }

}
