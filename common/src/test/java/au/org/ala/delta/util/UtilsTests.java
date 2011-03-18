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

import au.org.ala.delta.util.Utils;
import junit.framework.TestCase;

public class UtilsTests extends TestCase {

	public void testLOWORD() {
		int dword = 0xffffeeee;
		short word = Utils.LOWORD(dword);
		assertEquals(word, (short) 0xeeee);
	}
	
	public void testHIWORD() {
		int dword = 0xffffeeee;
		short word = Utils.HIWORD(dword);
		assertEquals(word, (short) 0xffff);		
	}
	
	public void testRTFToANSI() {
		String testString = "\\i Agrostis\\i0  <L.>";
		String expected = "Agrostis <L.>";
		String actual = Utils.RTFToANSI(testString);		
		assertEquals(expected, actual);
	}
	
	public void testStrtol1() {
		String test = "123";
		int expected = 123;
		int actual = Utils.strtol(test);
		assertEquals(expected, actual);
	}
	
	public void testStrtol2() {
		String test = "123abc";
		int expected = 123;
		int actual = Utils.strtol(test);
		assertEquals(expected, actual);
	}
	public void testStrtol3() {
		String test = "abc";
		int expected = 0;
		int actual = Utils.strtol(test);
		assertEquals(expected, actual);
	}
	

	
	/**
	 * Tests the remove comments method.
	 */
	public void testRemoveCommentsSingleCommentAtEnd() {
		String rtfWithComments = "\\i{}Echinochloa\\i0{} <P. Beauv.>";
		
		String withoutComments = Utils.removeComments(rtfWithComments, 1);
		
		assertEquals("\\i{}Echinochloa\\i0{}", withoutComments);
	}
	
	/**
	 * Tests the remove comments method.
	 */
	public void testRemoveCommentsSingleCommentAtStart() {
		String rtfWithComments = "<mature> Culms";
		
		String withoutComments = Utils.removeComments(rtfWithComments, 1);
		
		assertEquals(" Culms", withoutComments);
	}
	
	/**
	 * Tests the remove comments method.
	 */
	public void testRemoveCommentsCommentAtStartAndEnd() {
		String rtfWithComments = "<mature> Culms <maximum Height: Data Unreliable For Large Genera>";
		
		String withoutComments = Utils.removeComments(rtfWithComments, 1);
		
		assertEquals(" Culms", withoutComments);
	}
	
	/**
	 * Tests the remove comments method.
	 */
	public void testRemoveCommentsCommentInMiddle() {
		String rtfWithComments = "Culms <maximum Height: Data Unreliable For Large Genera> Again";
		
		String withoutComments = Utils.removeComments(rtfWithComments, 1);
		
		assertEquals("Culms Again", withoutComments);
	}
	
	/**
	 * Tests the remove comments method.
	 */
	public void testRemoveCommentsCommentInStartMiddleAndEnd() {
		String rtfWithComments = "<start>Culms <middle> Again<end>";
		
		String withoutComments = Utils.removeComments(rtfWithComments, 1);
		
		// This seems to be a bug with the existing (C++) system - the first comment is not removed in
		// this case.
		assertEquals("<start>Culms Again", withoutComments);
	}

}
