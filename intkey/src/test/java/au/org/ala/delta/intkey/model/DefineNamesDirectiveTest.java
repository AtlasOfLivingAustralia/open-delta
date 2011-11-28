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
