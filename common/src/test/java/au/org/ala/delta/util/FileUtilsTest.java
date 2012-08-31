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
package au.org.ala.delta.util;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

	public void testFindFileIgnoreCase() {
		FileUtils.findFileIgnoreCase("c:\\zz\\JSON.JSON");
	}
	
//	public void testMakeRelativeTo1() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("c:\\testfolder\\test1"));
//		String expected = "test1";
//		assertEquals(expected, actual);
//	}
//	
//	public void testMakeRelativeTo2() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("c:\\otherFolder\\test1"));
//		String expected = "..\\otherFolder\\test1";
//		assertEquals(expected, actual);
//	}
//	
//	public void testMakeRelativeTo3() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("c:\\testfolder"));
//		String expected = ".";
//		assertEquals(expected, actual);
//	}
//	
//	public void testMakeRelativeTo4() {
//		String actual = FileUtils.makeRelativeTo("c:\\testfolder", new File("d:\\testfolder"));
//		String expected = null;
//		assertEquals(expected, actual);
//	}
	
}
