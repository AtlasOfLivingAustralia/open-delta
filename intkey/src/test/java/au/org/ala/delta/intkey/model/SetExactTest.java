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
        assertTrue(eliminatedTaxa.contains(dataset.getItem(3)));
        assertTrue(eliminatedTaxa.contains(dataset.getItem(4)));
        assertTrue(eliminatedTaxa.contains(dataset.getItem(6)));
        assertTrue(eliminatedTaxa.contains(dataset.getItem(9)));
        assertTrue(eliminatedTaxa.contains(dataset.getItem(12)));
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
