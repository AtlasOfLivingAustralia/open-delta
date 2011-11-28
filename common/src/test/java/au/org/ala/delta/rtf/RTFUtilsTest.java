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
package au.org.ala.delta.rtf;

import junit.framework.TestCase;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

/**
 * Tests the RTFUtils class.
 */
public class RTFUtilsTest extends TestCase {

	/**
	 * Tests the stripFormatting method leaves plain text untouched.
	 */
	@Test
	public void testStripFormattingStringPlainText() {
	
		String text = "I am simple text";
		assertEquals(text, RTFUtils.stripFormatting(text));
		
	}
	
	
	@Test
	public void testRtfToHtml() {
		String text = "\\i{}Ornithospermum\\i0{} Dumoulin, \\i{}Tema\\i0{} Adans.";
		String result = RTFUtils.rtfToHtml(text);
		
		String expected = "<I>Ornithospermum</I> Dumoulin, <I>Tema</I> Adans.";
		assertEquals(expected, result);
		
		text = "First paragraph \\par{} Second paragraph";
	    result = RTFUtils.rtfToHtml(text);
		expected = "First paragraph <P> Second paragraph";
		assertEquals(expected, result);
		
		
		text = "\\sub{}Test\\nosupersub{}";
		result = RTFUtils.rtfToHtml(text);
		expected = "<SUB>Test</SUB>";
		assertEquals(expected, result);
	}
	
	@Test
	public void testMarkKeyword() {
		IntRange range = RTFUtils.markKeyword("\\i{}Ornithospermum\\i0{} Dumoulin, \\i{}Tema\\i0{} Adans.");
		assertEquals(0, range.getMinimumInteger());
		assertEquals(5, range.getMaximumInteger());
		
		range = RTFUtils.markKeyword("No keywords here");
		assertEquals(-1, range.getMinimumInteger());
		assertEquals(-1, range.getMaximumInteger());
		
		
		range = RTFUtils.markKeyword("Hi\\i there.");
		assertEquals(2, range.getMinimumInteger());
		assertEquals(6, range.getMaximumInteger());
		
		range = RTFUtils.markKeyword("Hi\\i ");
		assertEquals(2, range.getMinimumInteger());
		assertEquals(6, range.getMaximumInteger());
	}

}
