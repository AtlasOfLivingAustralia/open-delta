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

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.util.Pair;

@Ignore
public class DiagnoseDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testDiagnose() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        
        //Item taxon1 = context.getDataset().getItem(1);
        Pair<List<Integer>, List<Integer>> availableStuff = getCharacterAndTaxonNumbersForBest(context);
        
        
        LinkedHashMap<Character, Double> bestMap = Best.orderDiagnose(1, context.getDiagType(), context.getStopBest(), context.getDataset(), availableStuff.getFirst(), availableStuff.getSecond(), context.getRBase(), context.getVaryWeight());
        
        for (Character ch : bestMap.keySet()) {
            System.out.println(String.format("%s  %s  %s", bestMap.get(ch), ch.getCharacterId(), ch.getDescription()));
        }
    }
    
}
