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
package au.org.ala.delta.translation;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the TypeSetter class.
 */
public class TypeSetterTest extends TestCase {
	
	private PrintFile _typeSetter;
	private ByteArrayOutputStream _bytes;
	private int _lineWidth = 80;
	
	@Before
	public void setUp() {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes);
		_typeSetter = new PrintFile(pout, _lineWidth);
	}
	
	
	@Test
	public void testCapitaliseFirstWord() {
		String text = "\\b hi there\\b0";
		String result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("\\b Hi there\\b0", result);
		
		text = "hi there";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("Hi there", result);
		
		text = "\\\\Hi there";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("\\\\Hi there", result);
		
		text = "\\-don't really know what that means....";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("\\-Don't really know what that means....", result);
		
		text = "Hi there";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("Hi there", result);
		
		text = "|hi there";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("hi there", result);
		
		text = "";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("", result);
		
		text = null;
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals(null, result);
		
		text = "<\\i test me\\i0>";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("<\\i Test me\\i0>", result);
		
		text = "<\\i{}test me\\i0{}>";
		result = _typeSetter.capitaliseFirstWord(text);
		assertEquals("<\\i{}Test me\\i0{}>", result);
		
		
	}
	
	public void testLineWrap() {
		String input = "The quick brown fox jumps over the lazy dog....";
		_typeSetter.writeJustifiedText(input, -1);
		_typeSetter.printBufferLine(false);
		
		assertEquals(input+SystemUtils.LINE_SEPARATOR, output());
		
		_typeSetter.setIndent(4);
		_typeSetter.indent();
		_typeSetter.writeJustifiedText(input, -1);
		_typeSetter.printBufferLine(false);
		
		assertEquals("    "+input+SystemUtils.LINE_SEPARATOR, output());
		
		input = input+input;
		_typeSetter.setIndent(4);
		_typeSetter.indent();
		_typeSetter.writeJustifiedText(input, -1);
		_typeSetter.printBufferLine(false);
		
		String[] lines = output().split(SystemUtils.LINE_SEPARATOR);
		
		assertEquals("    The quick brown fox jumps over the lazy dog....The quick brown fox jumps", lines[0]);
		assertEquals("over the lazy dog....", lines[1]);
		
		input = "Thisisareallyreallyreallyreallyreallyreallyreallyreallylongstringreallyreallyreally";
		_typeSetter.setIndent(4);
		_typeSetter.indent();
		_typeSetter.writeJustifiedText(input, -1);
		_typeSetter.printBufferLine(false);
		
		lines = output().split(SystemUtils.LINE_SEPARATOR);
		
		assertEquals("    "+input.substring(0, 76), lines[0]);
		assertEquals(input.substring(76, input.length()), lines[1]);
		
	}
	
	public void testLineWrapWithSpaceAtWrapPosition() {
		char[] input = new char[_lineWidth*2];
		Arrays.fill(input, 'a');
		input[_lineWidth - 1] = ' ';
		
		_typeSetter.writeJustifiedText(new String(input), -1);
		_typeSetter.printBufferLine(false);
		
		String[] lines = output().split(SystemUtils.LINE_SEPARATOR);
		char[] expectedLine1Chars = Arrays.copyOfRange(input, 0, _lineWidth-1);
		char[] expectedLine2Chars = Arrays.copyOfRange(input, _lineWidth, input.length);
		
		String expectedLine1 = new String(expectedLine1Chars);
		String expectedLine2 = new String(expectedLine2Chars);
		assertEquals(expectedLine1, lines[0]);
		assertEquals(expectedLine2, lines[1]);
		
		// Now do it again as two separate writes
		_typeSetter.writeJustifiedText(expectedLine1, -1);
		_typeSetter.writeJustifiedText(expectedLine2, -1);
		_typeSetter.printBufferLine(false);
		
		lines = output().split(SystemUtils.LINE_SEPARATOR);
		assertEquals(expectedLine1, lines[0]);
		assertEquals(expectedLine2, lines[1]);
	}
	

	private String output() {
		String output = new String(_bytes.toByteArray());
		setUp();
		return output;
		
	}
}
