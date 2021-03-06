/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.DifferencesDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DiffUtils;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;

public class DifferencesDirectiveTest extends IntkeyDatasetTestCase {

    public static final boolean DEFAULT_MATCH_INAPPLICABLES = true;
    public static final boolean DEFAULT_MATCH_UNKNOWNS = true;
    public static final MatchType DEFAULT_MATCH_TYPE = MatchType.OVERLAP;

    /**
     * Smoke test for the differences directive - argument parsing etc.
     * 
     * @throws Exception
     */
    @Test
    public void testDifferencesSmokeTest() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        new DifferencesDirective().parseAndProcess(context, "(all) all");
    }

    /**
     * Test the results of a differences report using the default parameters
     * 
     * @throws Exception
     */
    @Test
    public void testDifferencesResults() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getItem(1);
        Item taxonApricot = ds.getItem(2);
        Item taxonPlum = ds.getItem(4);

        List<Item> taxa = new ArrayList<Item>();
        taxa.add(taxonCarrot);
        taxa.add(taxonApricot);
        taxa.add(taxonPlum);

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), taxa, null, DEFAULT_MATCH_UNKNOWNS,
                DEFAULT_MATCH_INAPPLICABLES, DEFAULT_MATCH_TYPE, false);

        assertEquals(6, differingChars.size());
        assertTrue(differingChars.contains(ds.getCharacter(1)));
        assertTrue(differingChars.contains(ds.getCharacter(2)));
        assertTrue(differingChars.contains(ds.getCharacter(4)));
        assertTrue(differingChars.contains(ds.getCharacter(6)));
        assertTrue(differingChars.contains(ds.getCharacter(7)));
        assertTrue(differingChars.contains(ds.getCharacter(8)));
    }

    /**
     * Test the results of a differences report, omitting text characters from
     * the results.
     * 
     * @throws Exception
     */
    @Test
    public void testOmitTextCharacters() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        Item taxonCarrot = ds.getItem(1);
        Item taxonApricot = ds.getItem(2);
        Item taxonPlum = ds.getItem(4);

        List<Item> taxa = new ArrayList<Item>();
        taxa.add(taxonCarrot);
        taxa.add(taxonApricot);
        taxa.add(taxonPlum);

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), taxa, null, DEFAULT_MATCH_UNKNOWNS,
                DEFAULT_MATCH_INAPPLICABLES, DEFAULT_MATCH_TYPE, true);

        assertEquals(5, differingChars.size());
        assertTrue(differingChars.contains(ds.getCharacter(1)));
        assertTrue(differingChars.contains(ds.getCharacter(2)));
        assertTrue(differingChars.contains(ds.getCharacter(4)));
        assertTrue(differingChars.contains(ds.getCharacter(6)));
        assertTrue(differingChars.contains(ds.getCharacter(7)));
    }

    /**
     * Test the results of a differences report, including the current specimen
     * in the comparsion
     * 
     * @throws Exception
     */
    @Test
    public void testIncludeSpecimen() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        IntegerCharacter chUpperGlumeNerveNumber = (IntegerCharacter) ds.getCharacter(38);

        new UseDirective().parseAndProcess(context, "38,3");

        Specimen specimen = context.getSpecimen();
        List<Item> remainingTaxa = context.getTaxaForKeyword(IntkeyContext.TAXON_KEYWORD_REMAINING);

        // note that the differences directive automatically sets match
        // inapplicables and unknown to false if the match type is exact.
        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), remainingTaxa, specimen, false, false, MatchType.EXACT,
                false);

        assertEquals(1, differingChars.size());
        assertTrue(differingChars.contains(chUpperGlumeNerveNumber));
    }

    /**
     * Second test of the results of a differences report using the default
     * parameters
     * 
     * @throws Exception
     */
    @Test
    public void testDefaultMatchValues() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), ds.getItemsAsList(), null, DEFAULT_MATCH_UNKNOWNS,
                DEFAULT_MATCH_INAPPLICABLES, MatchType.OVERLAP, false);

        assertEquals(71, differingChars.size());
        assertFalse(differingChars.contains(ds.getCharacter(6)));
        assertFalse(differingChars.contains(ds.getCharacter(10)));
        assertFalse(differingChars.contains(ds.getCharacter(14)));
        assertFalse(differingChars.contains(ds.getCharacter(17)));
        assertFalse(differingChars.contains(ds.getCharacter(21)));
        assertFalse(differingChars.contains(ds.getCharacter(22)));
        assertFalse(differingChars.contains(ds.getCharacter(23)));
        assertFalse(differingChars.contains(ds.getCharacter(24)));
        assertFalse(differingChars.contains(ds.getCharacter(29)));
        assertFalse(differingChars.contains(ds.getCharacter(32)));
        assertFalse(differingChars.contains(ds.getCharacter(33)));
        assertFalse(differingChars.contains(ds.getCharacter(36)));
        assertFalse(differingChars.contains(ds.getCharacter(43)));
        assertFalse(differingChars.contains(ds.getCharacter(83)));
    }

    /**
     * Test using the EXACT match type
     * 
     * @throws Exception
     */
    @Test
    public void testExact() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        // note that the differences directive automatically sets match
        // inapplicables and unknown to false if the match type is exact.
        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), ds.getItemsAsList(), null, false, false, MatchType.EXACT,
                false);

        assertEquals(85, differingChars.size());
        assertFalse(differingChars.contains(ds.getCharacter(33)));
        assertFalse(differingChars.contains(ds.getCharacter(55)));
    }

    /**
     * Test using the SUBSET matchtype
     * 
     * @throws Exception
     */
    @Test
    public void testSubset() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), ds.getItemsAsList(), null, false, false, MatchType.SUBSET,
                false);

        assertEquals(82, differingChars.size());
        assertFalse(differingChars.contains(ds.getCharacter(10)));
        assertFalse(differingChars.contains(ds.getCharacter(32)));
        assertFalse(differingChars.contains(ds.getCharacter(33)));
        assertFalse(differingChars.contains(ds.getCharacter(36)));
        assertFalse(differingChars.contains(ds.getCharacter(55)));
    }

    /**
     * Test using the SUBSET match type to compare real characters
     * 
     * @throws Exception
     */
    @Test
    public void testSubsetReal() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        List<Item> taxa = new ArrayList<Item>();
        taxa.add(ds.getItem(8));
        taxa.add(ds.getItem(9));

        List<au.org.ala.delta.model.Character> characters = new ArrayList<Character>();
        characters.add(ds.getCharacter(26));

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, characters, taxa, null, false, false, MatchType.SUBSET, false);

        assertTrue(differingChars.isEmpty());
    }

    /**
     * Test unknowns do not match any value
     * 
     * @throws Exception
     */
    @Test
    public void testDontMatchUnknowns() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), ds.getItemsAsList(), null, false,
                DEFAULT_MATCH_INAPPLICABLES, MatchType.OVERLAP, false);

        assertEquals(80, differingChars.size());
        assertFalse(differingChars.contains(ds.getCharacter(10)));
        assertFalse(differingChars.contains(ds.getCharacter(17)));
        assertFalse(differingChars.contains(ds.getCharacter(32)));
        assertFalse(differingChars.contains(ds.getCharacter(33)));
        assertFalse(differingChars.contains(ds.getCharacter(36)));
        assertFalse(differingChars.contains(ds.getCharacter(55)));
        assertFalse(differingChars.contains(ds.getCharacter(83)));
    }

    /**
     * Test inapplicables do not match any value
     * 
     * @throws Exception
     */
    @Test
    public void testDontMatchInapplicables() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();

        List<au.org.ala.delta.model.Character> differingChars = DiffUtils.determineDifferingCharactersForTaxa(ds, ds.getCharactersAsList(), ds.getItemsAsList(), null, DEFAULT_MATCH_UNKNOWNS, false,
                MatchType.OVERLAP, false);

        assertEquals(81, differingChars.size());
        assertFalse(differingChars.contains(ds.getCharacter(6)));
        assertFalse(differingChars.contains(ds.getCharacter(10)));
        assertFalse(differingChars.contains(ds.getCharacter(32)));
        assertFalse(differingChars.contains(ds.getCharacter(33)));
        assertFalse(differingChars.contains(ds.getCharacter(36)));
        assertFalse(differingChars.contains(ds.getCharacter(55)));
    }

    /**
     * Test exact match for text. This previously failed.
     */
    @Test
    public void testExactTextMatch() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();
        List<Item> taxa = new ArrayList<Item>();
        taxa.add(ds.getItem(1));
        taxa.add(ds.getItem(2));

        assertTrue(DiffUtils.compareForTaxa(ds, ds.getCharacter(87), taxa, null, false, false, MatchType.EXACT));
    }

    /**
     * An unknown and inapplicable character should match each other
     * 
     * @throws Exception
     */
    @Test
    public void testUnknownMatchesInapplicable() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset ds = context.getDataset();
        List<Item> taxa = new ArrayList<Item>();
        taxa.add(ds.getItem(1));
        taxa.add(ds.getItem(2));

        assertTrue(DiffUtils.compareForTaxa(ds, ds.getCharacter(29), taxa, null, false, false, MatchType.EXACT));
    }

    // TODO test include/exclude characters
    // TODO test include/exclude taxa

    // TODO test character inapplicable for all taxa
    // TODO test character unknown for all taxa

}
