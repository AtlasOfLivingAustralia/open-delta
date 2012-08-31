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

import org.junit.Test;

import au.org.ala.delta.intkey.directives.SetAutoToleranceDirective;
import au.org.ala.delta.intkey.directives.SetToleranceDirective;
import au.org.ala.delta.intkey.directives.UseDirective;

public class SetAutoToleranceTest extends IntkeyDatasetTestCase {

    @Test
    public void testSetAutoTolerance() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new SetToleranceDirective().parseAndProcess(context, "100");
        new UseDirective().parseAndProcess(context, "38,5");
        assertEquals(100, context.getTolerance());
        new SetAutoToleranceDirective().parseAndProcess(context, "ON");
        new UseDirective().parseAndProcess(context, "54,5");
        assertEquals(0, context.getTolerance());
    }

    public void testSetAutoToleranceOnThenOff() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        new SetToleranceDirective().parseAndProcess(context, "100");
        new SetAutoToleranceDirective().parseAndProcess(context, "ON");
        new SetAutoToleranceDirective().parseAndProcess(context, "OFF");
        new UseDirective().parseAndProcess(context, "38,5");
        assertEquals(100, context.getTolerance());
    }

}
