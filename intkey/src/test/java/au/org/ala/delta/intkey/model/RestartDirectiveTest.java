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

import org.junit.Test;

import au.org.ala.delta.intkey.directives.RestartDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Specimen;

/**
 * Unit tests for the RESTART directive
 * 
 * @author ChrisF
 * 
 */
public class RestartDirectiveTest extends IntkeyDatasetTestCase {

    /**
     * Set some values for characters in the specimen, then run the restart
     * directive
     * 
     * @throws Exception
     */
    @Test
    public void testRestart() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new UseDirective().parseAndProcess(context, "2-5,1");

        new RestartDirective().parseAndProcess(context, null);

        Specimen specimen = context.getSpecimen();
        assertEquals(Collections.EMPTY_LIST, specimen.getUsedCharacters());
    }

    /**
     * Run the restart directive without having set any values for characters
     * first
     * 
     * @throws Exception
     */
    @Test
    public void testRestartImmediately() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new RestartDirective().parseAndProcess(context, null);

        Specimen specimen = context.getSpecimen();
        assertEquals(Collections.EMPTY_LIST, specimen.getUsedCharacters());
    }

    // TODO check that characters that have had their values fixed using SET FIX
    // have their values maintained when the RESTART directive is run.

}
