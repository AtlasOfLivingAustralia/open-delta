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
package au.org.ala.delta.key;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.Character;

import junit.framework.TestCase;

public class AllowImproperSubgroupsTest extends TestCase {

    public void testAllowImproperSubgroups() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/mykey");
        File directivesFile = new File(directivesFileURL.toURI());
        
        URL charFileURL = getClass().getResource("/sample/kchars");
        File charFile = new File(charFileURL.toURI());

        URL itemsFileURL = getClass().getResource("/sample/kitems");
        File itemsFile = new File(itemsFileURL.toURI());

        // Use dummy temp file for data directory seeing as we don't have a use
        // for it here
        KeyContext context = new KeyContext(directivesFile);
        context.setABase(1.0);
        context.setVaryWt(1.0);
        context.setRBase(1.0);
        context.setReuse(1.0);
        context.setCharactersFile(charFile);
        context.setItemsFile(itemsFile);

        context.setAllowImproperSubgroups(false);

        KeyUtils.loadDataset(context);

        List<Integer> availableCharacterNumbers = Arrays.asList(ArrayUtils.toObject(new IntRange(1, context.getNumberOfCharacters()).toArray()));
        List<Integer> availableTaxaNumbers = Arrays.asList(ArrayUtils.toObject(new IntRange(1, context.getMaximumNumberOfItems()).toArray()));

        Map<Character, Double> bestMap = KeyBest.orderBest(context.getDataSet(), context.getCharacterCostsAsArray(), context.getCalculatedItemAbundanceValuesAsArray(), availableCharacterNumbers,
                availableTaxaNumbers, context.getRBase(), context.getABase(), context.getReuse(), context.getVaryWt(), context.getAllowImproperSubgroups());

        assertFalse(bestMap.containsKey(context.getCharacter(10)));

        context.setAllowImproperSubgroups(true);
        
        Map<Character, Double> bestMap2 = KeyBest.orderBest(context.getDataSet(), context.getCharacterCostsAsArray(), context.getCalculatedItemAbundanceValuesAsArray(), availableCharacterNumbers,
                availableTaxaNumbers, context.getRBase(), context.getABase(), context.getReuse(), context.getVaryWt(), context.getAllowImproperSubgroups());
        
        assertTrue(bestMap2.containsKey(context.getCharacter(10)));
    }

}
