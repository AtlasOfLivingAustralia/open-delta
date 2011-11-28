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

import au.org.ala.delta.intkey.directives.FileCharactersDirective;

/**
 * Unit tests for the FILE CHARACTERS directive.
 * 
 * @author ChrisF
 * 
 */
public class FileCharactersDirectiveTest extends TestCase {

    @Test
    public void testSetValidCharactersFile() throws Exception {
        IntkeyContext context = new IntkeyContext(new MockIntkeyUI(), new MockDirectivePopulator());
        URL icharsFileUrl = getClass().getResource("/dataset/sample/ichars");

        File fileCharacters = new File(icharsFileUrl.toURI());

        new FileCharactersDirective().parseAndProcess(context, fileCharacters.getAbsolutePath());

        assertEquals(fileCharacters, context.getCharactersFile());
    }

    @Test
    public void testSetInvalidCharactersFile() throws Exception {
        // TODO
    }

}
