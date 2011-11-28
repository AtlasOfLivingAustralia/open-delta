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

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.intkey.directives.FileTaxaDirective;

/**
 * Unit tests for the FILE TAXA directive
 * 
 * @author ChrisF
 * 
 */
public class FileTaxaDirectiveTest extends TestCase {

    @Test
    public void testSetValidTaxaFile() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        URL iitemsFileUrl = getClass().getResource("/dataset/sample/iitems");

        File fileTaxa = new File(iitemsFileUrl.toURI());

        new FileTaxaDirective().parseAndProcess(context, fileTaxa.getAbsolutePath());

        assertEquals(fileTaxa, context.getTaxaFile());
    }

    @Test
    public void testSetInvalidCharactersFile() throws Exception {
        // TODO
    }
}
