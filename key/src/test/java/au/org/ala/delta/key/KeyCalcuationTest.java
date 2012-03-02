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

import org.junit.Test;

public class KeyCalcuationTest extends TestCase {

    @Test
    public void testLoad() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/mykey");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey();
    }

    // @Test
    // public void testLoad2() throws Exception {
    // URL directivesFileURL =
    // getClass().getResource("/controlling_characters_simple/key");
    // File directivesFile = new File(directivesFileURL.toURI());
    //
    // Key key = new Key(directivesFile);
    // key.calculateKey(directivesFile);
    // }
    
//     @Test
//     public void testRobinPonerini() throws Exception {
//     File directivesFile = new
//     File("C:\\Users\\ChrisF\\Virtualbox Shared Folder\\Robin2\\key");
//    
//     Key key = new Key(directivesFile);
//     key.calculateKey(directivesFile);
//     }
     
//    @Test
//    public void testLoadPonerini() throws Exception {
//        File directivesFile = new File("C:\\Users\\ChrisF\\Virtualbox Shared Folder\\Cyperaceae_test2\\key");
//
//        Key key = new Key(directivesFile);
//        key.calculateKey(directivesFile);
//    }
}
