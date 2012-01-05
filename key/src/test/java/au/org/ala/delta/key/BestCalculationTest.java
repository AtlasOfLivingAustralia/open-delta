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

import junit.framework.TestCase;

public class BestCalculationTest extends TestCase {

    public void testBestCalculationSample() throws Exception {
//        URL directivesFileURL = getClass().getResource("/sample/mykey");
//        File directivesFile = new File(directivesFileURL.toURI());
//
//        Key key = new Key();
//        key.calculateKey(directivesFile);
//
//        KeyContext context = key.getContext();
        //
        // MutableDeltaDataSet dataset = context.getDataSet();
        //
        // // List<Integer> includedCharacters =
        // context.getIncludedCharacters();
        // // includedCharacters.remove(66);
        // //
        // // List<Integer> includedTaxa = context.getIncludedItems();
        // // includedTaxa.removeAll(Arrays.asList(3, 4, 9, 10));
        //
        // LinkedHashMap<au.org.ala.delta.model.Character, Double> bestMap =
        // Best.orderBest(dataset, context.getIncludedCharacters(),
        // context.getIncludedItems(), context.getRBase(), context.getVaryWt());
        //
        // for (au.org.ala.delta.model.Character ch : bestMap.keySet()) {
        // double sepPower = bestMap.get(ch);
        // System.out.println(String.format("%s %s (%s)", sepPower, ch,
        // ch.getReliability()));
        // }
        //
        // System.out.println(dataset.getAllAttributesForCharacter(74));
        // System.out.println(dataset.getAllAttributesForCharacter(78));
       //System.out.println(key.getContext().getDataSet().getAllAttributesForCharacter(7));
    }

    public void testBestCalculationCyperaceae() throws Exception {
//         File directivesFile = new
//         File("C:\\Users\\ChrisF\\Virtualbox Shared Folder\\Cyperaceae_test\\key");
//        
//         Key key = new Key(directivesFile);
//         key.calculateKey(directivesFile);
//        
//         KeyContext context = key.getContext();
//
//        // List<Integer> availableTaxa = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8,
//        // 9, 10);
//        //
//        MutableDeltaDataSet dataset = context.getDataSet();
//        // LinkedHashMap<au.org.ala.delta.model.Character, Double> bestMap =
//        // Best.orderBest(dataset, context.getIncludedCharacters(),
//        // availableTaxa, context.getRBase(),
//        // context.getVaryWt());
//        //
//        // for (au.org.ala.delta.model.Character ch : bestMap.keySet()) {
//        // double sepPower = bestMap.get(ch);
//        // System.out.println(String.format("%s %s (%s)", sepPower, ch,
//        // ch.getReliability()));
//        // }
//        //
//        System.out.println(dataset.getAllAttributesForCharacter(2));
//       System.out.println(key.getContext().getDataSet().getAllAttributesForCharacter(290));
    }
}
