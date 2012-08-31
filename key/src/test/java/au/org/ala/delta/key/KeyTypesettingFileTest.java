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

import au.org.ala.delta.util.Utils;

/**
 * Tests for the KEY TYPESETTING FILE directive
 * 
 * @author ChrisF
 * 
 */
public class KeyTypesettingFileTest extends TestCase {

    /**
     * Test the case where typeset output is used, but no output directory or
     * typesetting file name is specified. A file with name
     * {directivesFileName}.rtf should be created in the directory containing
     * the dataset initialisation script
     */
    @Test
    public void testDefaultName() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testTypesettingFile1");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey();

        File typesetFile = Utils.createFileFromPath("testTypesettingFile1.rtf", directivesFile.getParentFile());

        assertTrue(typesetFile.exists());
    }
    
    /**
     * Test the default name when the OUTPUT FORMAT HTML directive is used - it should be
     * {directivesFileName}.html
     */
    @Test
    public void testDefaultNameHtml() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testTypesettingFile4");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey();

        File typesetFile = Utils.createFileFromPath("testTypesettingFile4.html", directivesFile.getParentFile());

        assertTrue(typesetFile.exists());
    }

    /**
     * Test use of KEY TYPESETTING FILE directive to set a custom file name
     * without a path - a file with this name should be created in the directory
     * containing the dataset initialisation script
     * 
     * @throws Exception
     */
    @Test
    public void testCustomFileNameNoPath() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testTypesettingFile2");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey();

        File typesetFile = Utils.createFileFromPath("foo.txt", directivesFile.getParentFile());

        assertTrue(typesetFile.exists());
    }

    /**
     * Test use of KEY TYPESETTING FILE directive to set a custom file name with
     * a path - a file with this name should be created at the specified path -
     * if the path is relative, it will be relative to the directory containing
     * the dataset initialisation file.
     * 
     * @throws Exception
     */
    @Test
    public void testCustomFileNameWithPath() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testTypesettingFile3");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey();

        File typesetFile = Utils.createFileFromPath("xxx\\foo.txt", directivesFile.getParentFile());

        assertTrue(typesetFile.exists());
    }
    
    /**
     * Test using OUTPUT DIRECTORY to change the directory where the typesetting file is output
     * @throws Exception
     */
    @Test
    public void testDefaultFileNameWithCustomDirectory() throws Exception {
        URL directivesFileURL = getClass().getResource("/sample/testTypesettingFile5");
        File directivesFile = new File(directivesFileURL.toURI());

        Key key = new Key(directivesFile);
        key.calculateKey();

        File typesetFile = Utils.createFileFromPath("foo/bar.rtf", directivesFile.getParentFile());

        assertTrue(typesetFile.exists());
    }    
}
