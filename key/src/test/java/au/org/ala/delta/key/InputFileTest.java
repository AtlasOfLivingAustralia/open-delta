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

import org.junit.Test;

import junit.framework.TestCase;

public class InputFileTest extends TestCase {

    @Test
    public void testInputFile() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/scriptToCallInputFile");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey(directivesFile);
        
        assertEquals(3d, key.getContext().getABase());
        assertEquals(3d, key.getContext().getRBase());
        assertEquals(3d, key.getContext().getVaryWt());
    }
    
    @Test
    public void testNonExistentInputFile() throws Exception {
        
    }
}
