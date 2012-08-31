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

import au.org.ala.delta.key.directives.KeyDirectiveParser;
import au.org.ala.delta.model.MultiStateAttribute;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TreatCharactersAsVariableTest extends TestCase {


    @Test
    public void testTreatCharactersAsVariable() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testTreatCharactersAsVariableInputFile");
        File directivesFile = new File(directivesFileURL.toURI());
        
        KeyContext context = new KeyContext(directivesFile);
        context.setMaximumNumberOfItems(100);
        context.setNumberOfCharacters(100);
        
        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        parser.parse(directivesFile, context);

        assertEquals(new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(new int[] { 44, 66 }))), context.getVariableCharactersForTaxon(1));
        assertEquals(new HashSet<Integer>(Arrays.asList(ArrayUtils.toObject(new int[] { 4, 5, 6, 7, 8, 9, 10 }))), context.getVariableCharactersForTaxon(4));
        
        KeyUtils.loadDataset(context);
        
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(1, 44)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(1, 66)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 4)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 5)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 6)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 7)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 8)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 9)));
        assertTrue(isVariable( (MultiStateAttribute) context.getDataSet().getAttribute(4, 10)));
    }
    
    private boolean isVariable(MultiStateAttribute attr) {
        int numberOfStates = attr.getCharacter().getNumberOfStates();
        Set<Integer> presentStates = attr.getPresentStates();
        
        for (int i = 0; i < numberOfStates; i++) {
            int stateNumber = i + 1;
            if (!presentStates.contains(stateNumber)) {
                return false;
            }
        }
        
        return true;
    }
}
