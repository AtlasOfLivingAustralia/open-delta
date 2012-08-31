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
import java.util.Map;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Character;

public class DisplayCharacterOrderSeparateTest extends IntkeyDatasetTestCase {

    @Test
    public void testSeparate() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        
        new UseDirective().parseAndProcess(context, "11,1");
        
        Map<Character, Double> bestMap = SortingUtils.orderSeparate(context, context.getDataset().getItem(1));
        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());
        
        DisplayCharacterOrderBestTest.bestTestHelper(0, 39, 1.81, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(1, 66, 0.81, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(2, 35, 0.81, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(3, 28, 0.49, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(4, 34, 0.49, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(5, 38, 0.49, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(6, 52, 0.49, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(7, 58, 0.49, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(8, 60, 0.49, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(9, 27, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(10, 44, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(11, 3, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(12, 4, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(13, 5, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(14, 9, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(15, 15, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(16, 16, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(17, 20, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(18, 26, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(19, 53, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(20, 54, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(21, 59, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(22, 61, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(23, 62, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(24, 63, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(25, 12, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(26, 13, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(27, 48, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(28, 2, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(29, 7, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(30, 8, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(31, 14, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(32, 19, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(33, 29, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(34, 30, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(35, 31, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(36, 32, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(37, 33, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(38, 36, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(39, 37, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(40, 45, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(41, 46, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(42, 47, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(43, 49, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(44, 50, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(45, 51, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(46, 56, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(47, 64, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(48, 70, 0.22, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(49, 6, 0.00, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(50, 68, 0.22, orderedCharList, bestMap);
    }
    
    @Test
    public void testSeparate2() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        Map<Character, Double> bestMap = SortingUtils.orderSeparate(context, context.getDataset().getItem(3));
        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());
        
        DisplayCharacterOrderBestTest.bestTestHelper(0, 6, 2.32, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(1, 1, 0.74, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(2, 2, 0.74, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(3, 3, 0.74, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(4, 5, 0.74, orderedCharList, bestMap);
        DisplayCharacterOrderBestTest.bestTestHelper(5, 7, 0.74, orderedCharList, bestMap);
    }
}
