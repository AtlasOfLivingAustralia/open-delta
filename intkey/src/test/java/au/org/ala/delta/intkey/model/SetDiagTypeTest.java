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

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.SetDiagTypeSpecimensDirective;
import au.org.ala.delta.intkey.directives.SetDiagTypeTaxaDirective;

public class SetDiagTypeTest extends TestCase {

    @Test
    public void testSetDiagTypeSpecimens() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        new SetDiagTypeSpecimensDirective().parseAndProcess(context, null);
        assertEquals(DiagType.SPECIMENS, context.getDiagType());
    }

    @Test
    public void testSetDiagTypeTaxa() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        new SetDiagTypeTaxaDirective().parseAndProcess(context, null);
        assertEquals(DiagType.TAXA, context.getDiagType());
    }

}
