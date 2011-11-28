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
package au.org.ala.delta.model.format;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

/**
 * Tests the Formatter class.
 */
public class FormatterTest extends TestCase {

	private Formatter _formatter;

	
	@Test
	public void testStripInnerComments() {
		
		_formatter = new Formatter(CommentStrippingMode.STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, false);
		String text = "not pseudopetiolate <Test <implicit>>";
		String result = _formatter.defaultFormat(text);
		assertEquals("not pseudopetiolate Test", result);
	}
	
	@Test
	public void testStripInnerCommentsOuterCommentEmpty() {
		
		_formatter = new Formatter(CommentStrippingMode.STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, false);
		String text = "not pseudopetiolate <<implicit>>";
		String result = _formatter.defaultFormat(text);
		assertEquals("not pseudopetiolate", result);
	}
	
	@Test
	public void testTripInnerCommentsMultipleInnerComments() {
		_formatter = new Formatter(CommentStrippingMode.STRIP_INNER, AngleBracketHandlingMode.RETAIN, false, false);
		String text = "<1 long, split, serrate, 1 simple <potentially split>, 2 split <potentially serrate>>";
		String result = _formatter.defaultFormat(text);
		assertEquals("<1 long, split, serrate, 1 simple, 2 split>", result);
		
	}
}
