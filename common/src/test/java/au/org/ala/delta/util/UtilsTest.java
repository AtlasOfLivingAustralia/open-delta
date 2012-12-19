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
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsTest extends TestCase {

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
	
	public void testDespaceRtf() {
		String withRtf = "<when present,>,1/3<\\i Bromidium\\i0>/5<rarely>";
		String despaced = Utils.despaceRtf(withRtf, false);
		assertEquals("<when present,>,1/3<\\i{}Bromidium\\i0{}>/5<rarely>", despaced);
		
		
		String noRtf = "<<Test Hypenated-String Word  #2>>";
		despaced = Utils.despaceRtf(noRtf, false);
		assertEquals(noRtf, despaced);

        String rtfWithSlashT = "<Ceratocephale sp_B\t>";
        despaced = Utils.despaceRtf(rtfWithSlashT, false);
        assertEquals("<Ceratocephale sp_B\\tab{}>", despaced);
	}
	
	public void testLowerBound1() {
		lbtest(new Integer[] {1,2,3,4},3,5, new Integer[] {1,2,3,4,5});
	}
	
	public void testLowerBound2() {
		lbtest(new Integer[] {1,2,4,3},2,5, new Integer[] {1,2,4,3,5});
	}
	
	public void testLowerBound3() {
		lbtest(new Integer[] {1,3,4},2,2, new Integer[] {1,3,2,4});
	}
	
	
	
	private void lbtest(Integer[] numbers, int start, int n, Integer[] expected) {
		List<Integer> ilist = new ArrayList<Integer>(Arrays.asList(numbers));
		int lb = Utils.lowerBound(ilist, start, ilist.size(), n);
		ilist.add(lb, n);		
		System.out.println(ilist);	
		assertEquals(Arrays.asList(expected).toString(), ilist.toString());
	}


    @Test
    public void testCapitalizeFirstWord() {
        String text = "\\i{}pleonites 1\\endash{}3\\i0{}";

        assertEquals("\\i{}Pleonites 1\\endash{}3\\i0{}", Utils.capitaliseFirstWord(text));
    }

//	/**
//	 * Tests the remove comments method with nested comments
//	 */
//	public void testRemoveCommentsWithNestedComments() {
//		String rtfWithComments = "Culms <Blah<Test> asdfas>";
//		
//		String withoutComments = Utils.removeComments(rtfWithComments, 1);
//		
//		assertEquals("Culms", withoutComments);
//	}
//	
//	public void testStripComments() {
//		String rtfWithComments = "Culms <maximum Height: Data Unreliable For Large Genera>";
//		
//		String withoutComments = Utils.removeComments(rtfWithComments, 0, false, false, false, true);
//		
//		assertEquals("Culms maximum Height: Data Unreliable For Large Genera", withoutComments);
//	}

}
